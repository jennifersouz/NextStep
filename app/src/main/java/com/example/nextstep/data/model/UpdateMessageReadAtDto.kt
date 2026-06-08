package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateMessageReadAtDto(
    @SerialName("read_at")
    val readAt: String
)
