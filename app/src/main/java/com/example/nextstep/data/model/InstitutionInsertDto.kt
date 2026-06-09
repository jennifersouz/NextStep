package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstitutionInsertDto(
    @SerialName("profile_id")
    val profileId: String,
    val name: String,
    val nif: String? = null,
    val locality: String? = null,
    val address: String? = null,
    val phone: String? = null
)
