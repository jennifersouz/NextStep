package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyNameDto(
    @SerialName("company_name")
    val companyName: String
)