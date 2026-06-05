package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentChatConversationDto(
    val id: String,

    val status: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("offer_title")
    val offerTitle: String? = null,

    @SerialName("company_name")
    val companyName: String? = null
)