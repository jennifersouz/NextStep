package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyProfileDto(
    @SerialName("profile_id")
    val profileId: String,

    @SerialName("company_name")
    val companyName: String,

    val nif: String? = null,

    @SerialName("business_area")
    val businessArea: String? = null,

    val location: String? = null,

    val description: String? = null,

    val phone: String? = null
)