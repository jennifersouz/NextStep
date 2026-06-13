package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OfferActiveStatusUpdateDto(
    @SerialName("is_active")
    val isActive: Boolean
)