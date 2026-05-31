package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateCompanyProfileDto(
    @SerialName("company_name")
    val companyName: String,

    @SerialName("business_area")
    val businessArea: String,

    val location: String,

    val description: String,

    val phone: String
)