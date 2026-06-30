package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationIdDto(
    val id: String,
    @SerialName("offer_id")
    val offerId: String? = null
)
