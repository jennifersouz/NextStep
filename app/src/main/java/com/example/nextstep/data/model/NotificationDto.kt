package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    @SerialName("id") val id: String,
    @SerialName("receiver_profile_id") val receiverProfileId: String,
    @SerialName("sender_profile_id") val senderProfileId: String? = null,
    @SerialName("application_id") val applicationId: String? = null,
    @SerialName("title") val title: String,
    @SerialName("message") val message: String,
    @SerialName("type") val type: String,
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class UpdateNotificationReadDto(
    @SerialName("is_read") val isRead: Boolean
)
