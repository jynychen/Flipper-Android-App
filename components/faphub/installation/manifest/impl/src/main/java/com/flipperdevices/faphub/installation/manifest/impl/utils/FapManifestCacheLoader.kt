package com.flipperdevices.faphub.installation.manifest.impl.utils

import android.content.Context
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.md5
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.ktx.jre.withLockResult
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

data class FapManifestCacheLoadResult(
    val cachedNames: List<Pair<File, String>>,
    val toLoadNames: List<String>
)

@Singleton
class FapManifestCacheLoader @Inject constructor(
    context: Context,
    private val fFeatureProvider: FFeatureProvider,
    private val parser: FapManifestParser
) : LogTagProvider {
    private val cacheDir = File(context.cacheDir, "fap_manifests")
    private val mutex = Mutex()
    override val TAG = "FapManifestCacheLoader"

    suspend fun loadCache(): FapManifestCacheLoadResult = withLockResult(mutex, "load") {
        val namesWithHash = fFeatureProvider.getSync<FStorageFeatureApi>()
            ?.listingApi()
            ?.lsWithMd5(FapManifestConstants.FAP_MANIFESTS_FOLDER_ON_FLIPPER)
            ?.getOrNull()
            .orEmpty()
            .filter { item -> File(item.fileName).extension == FapManifestConstants.FAP_MANIFEST_EXTENSION }
            .filterNot { item -> item.fileName.startsWith(".") }
            .map { item -> item.fileName to item.md5 }
        val filesWithHash = getLocalFilesWithHash().associate { (file, md5) -> md5 to file }
        info { "Find ${filesWithHash.size} files in cache" }
        val cached = mutableListOf<Pair<File, String>>()
        val toLoad = mutableListOf<String>()

        for ((name, hash) in namesWithHash) {
            val foundFile = filesWithHash[hash]
            if (foundFile != null) {
                cached.add(foundFile to name)
            } else {
                toLoad.add(name)
            }
        }

        return@withLockResult FapManifestCacheLoadResult(
            cachedNames = cached,
            toLoadNames = toLoad
        )
    }

    suspend fun invalidate(
        manifestItems: List<FapManifestItem>
    ) = withLock(mutex, "invalidate") {
        withContext(FlipperDispatchers.workStealingDispatcher) {
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            val existedFiles = getLocalFilesWithHash()
                .associate { (file, md5) -> md5 to file }
                .toMutableMap()
            val toDeleteFiles = HashSet<File>(existedFiles.map { it.value })
            for (manifest in manifestItems) {
                val manifestHash =
                    manifest.sourceFileHash ?: parser.encode(manifest).openStream().md5()
                val existedFile = existedFiles[manifestHash]
                if (existedFile != null) {
                    toDeleteFiles.remove(existedFile)
                } else {
                    val cachedFile = File(cacheDir, manifestHash)
                    cachedFile.createNewFile()

                    parser.encode(manifest).openStream().use { manifestStream ->
                        cachedFile.outputStream().use { fileStream ->
                            manifestStream.copyTo(fileStream)
                        }
                    }
                    existedFiles[manifestHash] = cachedFile
                }
            }
            toDeleteFiles.forEach { it.delete() }
        }
    }

    private suspend fun getLocalFilesWithHash(): List<Pair<File, String>> =
        withContext(FlipperDispatchers.workStealingDispatcher) {
            return@withContext cacheDir.listFiles()?.map {
                it to it.name
            }.orEmpty()
        }
}
