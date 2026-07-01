package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdvisorInviteDto(
    val id: String = "",

    @SerialName("company_profile_id")
    val companyProfileId: String = "",

    val email: String = "",

    val name: String? = null,

    val phone: String? = null,

    val department: String? = null,

    val status: String = "pending",

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("accepted_at")
    val acceptedAt: String? = null
)