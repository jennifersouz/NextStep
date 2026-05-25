package com.example.nextstep.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateApplicationStatusDto(
    val status: String
)