package com.example.nextstep.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAdvisorProfileDto(
    val name: String,
    val phone: String? = null,
    val department: String? = null
)