package com.flipperdevices.ifrmvp.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignalRequestModel(
    @SerialName("success_results")
    val successResults: List<SignalResultData> = emptyList(),
    @SerialName("failed_results")
    val failedResults: List<SignalResultData> = emptyList(),
    @SerialName("category_id")
    val categoryId: Long,
    @SerialName("brand_id")
    val brandId: Long
) {
    @Serializable
    data class SignalResultData(
        @SerialName("signal_id")
        val signalId: Long,
        @SerialName("ifr_file_id")
        val ifrFileId: Long
    )
}
