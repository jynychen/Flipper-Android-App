package com.flipperdevices.bridge.connection.feature.alarm.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi

interface FAlarmFeatureApi : FDeviceFeatureApi {
    suspend fun makeSound()
}
