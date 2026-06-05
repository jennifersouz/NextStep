package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDto(
    val id: String,

    @SerialName("application_id")
    val applicationId: String,

    @SerialName("sender_profile_id")
    val senderProfileId: String,

    @SerialName("receiver_profile_id")
    val receiverProfileId: String,

    val content: String,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("read_at")
    val readAt: String? = null
)