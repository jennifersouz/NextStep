package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationMessageDto(
    val id: String,

    @SerialName("application_id")
    val applicationId: String,

    @SerialName("sender_profile_id")
    val senderProfileId: String,

    @SerialName("receiver_profile_id")
    val receiverProfileId: String,

    val content: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("read_at")
    val readAt: String? = null,

    @SerialName("sender_email")
    val senderEmail: String? = null,

    @SerialName("receiver_email")
    val receiverEmail: String? = null,

    @SerialName("is_mine")
    val isMine: Boolean = false
)
