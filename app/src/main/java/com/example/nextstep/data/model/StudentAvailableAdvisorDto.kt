package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentAvailableAdvisorDto(
    @SerialName("advisor_profile_id")
    val advisorProfileId: String,

    @SerialName("company_profile_id")
    val companyProfileId: String? = null,

    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val department: String? = null,

    @SerialName("request_id")
    val requestId: String? = null,

    @SerialName("request_status")
    val requestStatus: String? = null
)