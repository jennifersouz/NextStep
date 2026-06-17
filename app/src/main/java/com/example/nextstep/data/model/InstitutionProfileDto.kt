package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstitutionProfileDto(
    val id: String,

    val email: String? = null,

    @SerialName("first_name")
    val name: String? = null,

    val phone: String? = null,

    val role: String? = null,

    @SerialName("is_active")
    val isActive: Boolean? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)