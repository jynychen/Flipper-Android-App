package com.flipperdevices.wearable.emulate.handheld.impl.di

import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main.MainRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main.MainResponse
import com.flipperdevices.wearable.emulate.handheld.impl.request.WearableAppStateProcessor
import com.flipperdevices.wearable.emulate.handheld.impl.request.WearableCommandProcessor
import com.flipperdevices.wearable.emulate.handheld.impl.request.WearableFlipperStatusProcessor
import com.flipperdevices.wearable.emulate.handheld.impl.request.WearablePingProcessor
import com.flipperdevices.wearable.emulate.handheld.impl.request.WearableSendProcessor
import com.flipperdevices.wearable.emulate.handheld.impl.request.WearableStartEmulateProcessor
import com.flipperdevices.wearable.emulate.handheld.impl.request.WearableStopEmulateProcessor
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope

class WearServiceComponentImpl(
    deps: WearServiceComponentDependencies,
    scope: CoroutineScope
) : WearServiceComponent, WearServiceComponentDependencies by deps {
    private val channelClient = Wearable.getChannelClient(context)
    override val commandInputStream = WearableCommandInputStream(
        channelClient = channelClient,
        parser = MainRequest::parseDelimitedFrom
    )
    override val commandOutputStream = WearableCommandOutputStream<MainResponse>(
        channelClient = channelClient
    )
    override val commandProcessors: Set<WearableCommandProcessor> = buildSet {
        WearableAppStateProcessor(
            commandOutputStream = commandOutputStream,
            scope = scope,
            fFeatureProvider = fFeatureProvider
        ).run(::add)
        WearableFlipperStatusProcessor(
            commandInputStream = commandInputStream,
            commandOutputStream = commandOutputStream,
            scope = scope,
            fFeatureProvider = fFeatureProvider,
            fDeviceOrchestrator = fDeviceOrchestrator
        ).run(::add)
        WearablePingProcessor(
            commandInputStream = commandInputStream,
            commandOutputStream = commandOutputStream,
            scope = scope,
        ).run(::add)
        WearableSendProcessor(
            commandInputStream = commandInputStream,
            commandOutputStream = commandOutputStream,
            scope = scope,
            simpleKeyApi = simpleKeyApi,
            keyParser = keyParser,
            fFeatureProvider = fFeatureProvider
        ).run(::add)
        WearableStartEmulateProcessor(
            commandInputStream = commandInputStream,
            commandOutputStream = commandOutputStream,
            scope = scope,
            fFeatureProvider = fFeatureProvider
        ).run(::add)
        WearableStopEmulateProcessor(
            commandInputStream = commandInputStream,
            commandOutputStream = commandOutputStream,
            scope = scope,
            fFeatureProvider = fFeatureProvider
        ).run(::add)
    }
}
