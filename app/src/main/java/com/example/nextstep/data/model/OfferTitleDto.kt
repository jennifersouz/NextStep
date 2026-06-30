package com.example.nextstep.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OfferTitleDto(
    val id: String,
    val title: String
)
