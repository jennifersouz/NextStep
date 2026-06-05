package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateCompanyAdvisorDto(
    @SerialName("company_profile_id")
    val companyProfileId: String,

    val name: String,
    val email: String,
    val phone: String?,
    val department: String?,
    val status: String = "pending"
)