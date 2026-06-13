package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationTaskDto(
    @SerialName("id") val id: String,
    @SerialName("application_id") val applicationId: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("status") val status: String,
    @SerialName("priority") val priority: String? = null,
    @SerialName("due_date") val dueDate: String? = null,
    @SerialName("completed_at") val completedAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)
