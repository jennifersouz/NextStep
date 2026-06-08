package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdvisorProfileDto(
    @SerialName("profile_id")
    val profileId: String,

    val name: String? = null,

    val email: String? = null,

    val phone: String? = null,

    val department: String? = null
)