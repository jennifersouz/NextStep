package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentChatConversationDto(
    val id: String,

    @SerialName("application_id")
    val applicationId: String = "",

    @SerialName("participant_type")
    val participantType: String = "",

    val status: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("offer_title")
    val offerTitle: String? = null,

    @SerialName("company_name")
    val companyName: String? = null,

    @SerialName("participant_name")
    val participantName: String? = null,

    @SerialName("chat_label")
    val chatLabel: String? = null,

    @SerialName("last_message")
    val lastMessage: String? = null,

    @SerialName("last_message_at")
    val lastMessageAt: String? = null
)