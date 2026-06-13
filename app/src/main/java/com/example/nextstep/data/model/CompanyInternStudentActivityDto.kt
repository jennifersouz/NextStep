package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyStudentActivityDto(
    val id: String? = null,

    @SerialName("application_id")
    val applicationId: String? = null,

    val title: String? = null,
    val description: String? = null,
    val status: String? = null,
    val priority: String? = null,

    @SerialName("due_date")
    val dueDate: String? = null,

    @SerialName("completed_at")
    val completedAt: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("created_by_profile_id")
    val createdByProfileId: String? = null,

    @SerialName("assigned_to_profile_id")
    val assignedToProfileId: String? = null
)