package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstitutionProfileDto(
    @SerialName("profile_id")
    val profileId: String,

    val email: String? = null,

    val name: String? = null,

    val nif: String? = null,

    val locality: String? = null,

    val address: String? = null,

    val phone: String? = null
)