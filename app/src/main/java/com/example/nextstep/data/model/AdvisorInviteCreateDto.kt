package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdvisorInviteCreateDto(
    @SerialName("company_profile_id")
    val companyProfileId: String,
    val email: String,
    val status: String = "pending"
)