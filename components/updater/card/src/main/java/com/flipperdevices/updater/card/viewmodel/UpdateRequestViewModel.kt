package com.flipperdevices.updater.card.viewmodel

import com.flipperdevices.bridge.connection.feature.getinfo.api.FGattInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.ktx.android.filename
import com.flipperdevices.core.ktx.android.length
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.updater.card.model.BatteryState
import com.flipperdevices.updater.card.model.SyncingState
import com.flipperdevices.updater.card.model.UpdatePending
import com.flipperdevices.updater.card.model.UpdatePendingState
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.InternalStorageFirmware
import com.flipperdevices.updater.model.UpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

private const val EXT_UPDATER_FILE = "tgz"
private const val SIZE_FOLDER_UPDATE_MAX = 1024L * 1024L * 1024L * 10 // 10Mb

class UpdateRequestViewModel @Inject constructor(
    private val synchronizationApi: SynchronizationApi,
    private val fFeatureProvider: FFeatureProvider
) : DecomposeViewModel() {

    private val batteryFlow = fFeatureProvider.get<FGattInfoFeatureApi>()
        .map { status -> status as? FFeatureStatus.Supported<FGattInfoFeatureApi> }
        .flatMapLatest { status -> status?.featureApi?.getGattInfoFlow() ?: flowOf(null) }
        .map { gattInfo ->
            if (gattInfo == null) {
                BatteryState.Unknown
            } else {
                gattInfo.batteryLevel?.let { batteryLevel ->
                    BatteryState.Ready(gattInfo.isCharging, batteryLevel)
                } ?: BatteryState.Unknown
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, BatteryState.Unknown)

    fun getBatteryState(): StateFlow<BatteryState> = batteryFlow

    private val pendingFlow = MutableStateFlow<UpdatePendingState?>(null)
    fun getUpdatePendingState(): StateFlow<UpdatePendingState?> = pendingFlow

    init {
        synchronizationApi.getSynchronizationState().onEach { syncState ->
            pendingFlow.update {
                if (it != null && it is UpdatePendingState.Ready) {
                    it.copy(syncingState = syncState.toSyncingState())
                } else {
                    it
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onUpdateRequest(updatePending: UpdatePending) {
        viewModelScope.launch {
            if (batteryFlow.value.isAllowToUpdate().not()) {
                pendingFlow.emit(UpdatePendingState.LowBattery)
                return@launch
            }
            when (updatePending) {
                is UpdatePending.Request -> startUpdateFromServer(updatePending)
                is UpdatePending.URI -> startUpdateFromFile(updatePending)
            }
        }
    }

    fun stopSyncAndStartUpdate(request: UpdateRequest) {
        viewModelScope.launch {
            pendingFlow.emit(UpdatePendingState.Ready(request, SyncingState.STOP))
        }
    }

    fun resetState() {
        viewModelScope.launch { pendingFlow.emit(null) }
    }

    private suspend fun startUpdateFromServer(updatePending: UpdatePending.Request) {
        val syncState = synchronizationApi.getSynchronizationState().first()
        pendingFlow.emit(
            UpdatePendingState.Ready(
                updatePending.updateRequest,
                syncState.toSyncingState()
            )
        )
    }

    private fun startUpdateFromFile(updatePending: UpdatePending.URI) {
        viewModelScope.launch {
            val context = updatePending.context
            val uri = updatePending.uri
            val sizeFile = updatePending.uri.length(context.contentResolver)
            val file = uri.filename(context.contentResolver)?.let { File(it) }
            val extension = file?.extension

            if (extension != EXT_UPDATER_FILE) {
                pendingFlow.emit(UpdatePendingState.FileExtension)
                return@launch
            }

            if (sizeFile == null || sizeFile > SIZE_FOLDER_UPDATE_MAX) {
                pendingFlow.emit(UpdatePendingState.FileBig)
                return@launch
            }

            val request = UpdateRequest(
                updateFrom = updatePending.currentVersion,
                updateTo = FirmwareVersion(
                    channel = FirmwareChannel.CUSTOM,
                    version = file.nameWithoutExtension
                ),
                changelog = null,
                content = InternalStorageFirmware(updatePending.uri.toString())
            )

            val syncState = synchronizationApi.getSynchronizationState().first()
            pendingFlow.emit(UpdatePendingState.Ready(request, syncState.toSyncingState()))
        }
    }

    private fun SynchronizationState.toSyncingState(): SyncingState = when (this) {
        SynchronizationState.NotStarted,
        SynchronizationState.Finished -> SyncingState.COMPLETE

        is SynchronizationState.InProgress -> SyncingState.IN_PROGRESS
    }
}
