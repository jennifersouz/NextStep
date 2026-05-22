package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyDto(
    @SerialName("profile_id")
    val profileId: String,

    @SerialName("company_name")
    val companyName: String,

    val nif: String,

    @SerialName("business_area")
    val businessArea: String,

    val location: String
)