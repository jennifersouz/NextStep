package com.example.nextstep.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileEmailDto(
    val id: String,
    val email: String
)