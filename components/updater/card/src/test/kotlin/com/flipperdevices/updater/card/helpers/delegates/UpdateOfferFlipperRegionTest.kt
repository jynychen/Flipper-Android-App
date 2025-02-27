package com.flipperdevices.updater.card.helpers.delegates

import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FListingStorageApi
import com.flipperdevices.bridge.connection.feature.update.api.RegionApi.Companion.REGION_FILE
import com.flipperdevices.updater.card.helpers.FileExistHelper
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UpdateOfferFlipperRegionTest {
    private val fListingStorageApi: FListingStorageApi = mockk()
    private val fStorageFeatureApi: FStorageFeatureApi = mockk()
    private val fileExistHelper: FileExistHelper = mockk()
    private val delegate: UpdateOfferDelegate = UpdateOfferFlipperRegionFile(
        fileExistHelper = fileExistHelper
    )

    @Before
    fun setup() {
        coEvery { fStorageFeatureApi.listingApi() } returns fListingStorageApi
    }

    @Test
    fun `Manifest not exist`() = runTest {
        every {
            fileExistHelper.isFileExist(REGION_FILE, fListingStorageApi)
        } returns flowOf(false)
        delegate.isRequire(fStorageFeatureApi).collect {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun `Manifest exist`() = runTest {
        every {
            fileExistHelper.isFileExist(REGION_FILE, fListingStorageApi)
        } returns flowOf(true)
        delegate.isRequire(fStorageFeatureApi).collect {
            Assert.assertFalse(it)
        }
    }
}
