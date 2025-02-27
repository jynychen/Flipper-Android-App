package com.flipperdevices.firstpair.impl.storage

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.connection.config.api.FDevicePersistedStorage
import com.flipperdevices.bridge.connection.config.api.model.FDeviceFlipperZeroBleModel
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.preference.pb.FlipperZeroBle
import com.flipperdevices.core.preference.pb.PairSettings
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FirstPairStorage::class)
class FirstPairStorageImpl @Inject constructor(
    private val pairSettingsStore: DataStore<PairSettings>,
    private val persistedStorage: FDevicePersistedStorage
) : FirstPairStorage, LogTagProvider {
    override val TAG = "FirstPairStorage"

    override fun isTosPassed(): Boolean {
        return runBlockingWithLog("is_passed") { pairSettingsStore.data.first() }.tos_passed
    }

    override fun isDeviceSelected(): Boolean {
        return runBlockingWithLog("is_device_selected") { pairSettingsStore.data.first() }.pair_device_pass
    }

    override fun markTosPassed(): Unit = runBlockingWithLog("mark_tos_passed") {
        pairSettingsStore.updateData {
            it.copy(
                tos_passed = true
            )
        }
    }

    override fun markDeviceSelected(
        deviceId: String?,
        deviceName: String?,
        address: String?
    ): Unit = runBlockingWithLog("mark_device_selected") {
        pairSettingsStore.updateData {
            var pairSetting = it.copy(pair_device_pass = true)

            if (deviceId != null) {
                pairSetting = pairSetting.copy(device_id = deviceId)
            }
            if (deviceName != null) {
                var deviceNameFormatted = deviceName.trim()
                if (deviceNameFormatted.startsWith(Constants.DEVICENAME_PREFIX)) {
                    deviceNameFormatted = deviceNameFormatted
                        .replaceFirst(Constants.DEVICENAME_PREFIX, "")
                        .trim()
                }
                pairSetting = pairSetting.copy(device_name = deviceNameFormatted)
                val device = FDeviceFlipperZeroBleModel(
                    name = deviceNameFormatted,
                    address = address.orEmpty(),
                    uniqueId = deviceId ?: UUID.randomUUID().toString(),
                    hardwareColor = FlipperZeroBle.HardwareColor.fromValue(pairSetting.hardware_color.value)
                )
                persistedStorage.addDevice(device)
                persistedStorage.setCurrentDevice(deviceId)
            }
            pairSetting
        }
    }
}
