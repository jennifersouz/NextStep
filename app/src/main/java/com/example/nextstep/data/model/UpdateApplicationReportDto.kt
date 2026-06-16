package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateApplicationReportDto(
    @SerialName("report_path")
    val reportPath: String
)