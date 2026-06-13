package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdvisorTaskListItemDto(
    @SerialName("id") val id: String,
    @SerialName("application_id") val applicationId: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("status") val status: String = "pending",
    @SerialName("priority") val priority: String? = "medium",
    @SerialName("due_date") val dueDate: String? = null,
    @SerialName("student_name") val studentName: String? = null,
    @SerialName("student_email") val studentEmail: String? = null,
    @SerialName("offer_title") val offerTitle: String? = null,
    @SerialName("company_name") val companyName: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("completed_at") val completedAt: String? = null
)

@Serializable
data class CreateApplicationTaskDto(
    @SerialName("application_id") val applicationId: String,
    @SerialName("title") val title: String,
    @SerialName("status") val status: String = "pending"
)

@Serializable
data class UpdateApplicationTaskStatusDto(
    @SerialName("status") val status: String
)

@Serializable
data class AdvisorConversationDto(
    @SerialName("application_id") val applicationId: String,
    @SerialName("student_name") val studentName: String,
    @SerialName("student_email") val studentEmail: String? = null,
    @SerialName("offer_title") val offerTitle: String? = null,
    @SerialName("last_message") val lastMessage: String? = null,
    @SerialName("last_message_at") val lastMessageAt: String? = null,
    @SerialName("unread_count") val unreadCount: Int = 0
)
