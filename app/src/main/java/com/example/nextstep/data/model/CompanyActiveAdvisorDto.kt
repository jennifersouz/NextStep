package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyActiveAdvisorDto(
    @SerialName("profile_id")
    val profileId: String,

    @SerialName("company_profile_id")
    val companyProfileId: String,

    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val department: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)
