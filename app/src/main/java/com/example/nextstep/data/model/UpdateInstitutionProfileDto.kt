package com.example.nextstep.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateInstitutionProfileDto(
    val name: String,
    val nif: String? = null,
    val locality: String? = null,
    val address: String? = null,
    val phone: String? = null
)