package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OfferDto(
    val id: String,

    @SerialName("company_name")
    val companyName: String,

    val title: String,

    val location: String,

    @SerialName("is_active")
    val isActive: Boolean = true
)