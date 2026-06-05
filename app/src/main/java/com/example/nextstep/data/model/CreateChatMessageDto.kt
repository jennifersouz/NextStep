package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateChatMessageDto(
    @SerialName("application_id")
    val applicationId: String,

    @SerialName("sender_profile_id")
    val senderProfileId: String,

    @SerialName("receiver_profile_id")
    val receiverProfileId: String,

    val content: String
)