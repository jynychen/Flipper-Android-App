package com.flipperdevices.bridge.connection.feature.common.api

import dagger.MapKey

enum class FDeviceFeature {
    RPC,
    SERIAL_LAGS_DETECTOR,
    SERIAL_RESTART_RPC,
    SERIAL_SPEED,
    VERSION,
    RPC_INFO,
    STORAGE,
    STORAGE_INFO,
    GET_INFO,
    ALARM,
    DEVICE_COLOR,
    GATT_INFO,
    SDK_VERSION,
    APP_START,
    SCREEN_STREAMING,
    SCREEN_UNLOCK,
    UPDATE,
    EMULATE
}

@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class FDeviceFeatureQualifier(val enum: FDeviceFeature)
