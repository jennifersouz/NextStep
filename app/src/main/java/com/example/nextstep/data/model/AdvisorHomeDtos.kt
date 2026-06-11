package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActivityDto(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    val type: String? = null
)

data class AdvisorSummaryDto(
    val assignedStudentsCount: Int = 0,
    val activeInternshipsCount: Int = 0,
    val pendingTasksCount: Int = 0
)

data class AdvisorAssignedStudentDto(
    val applicationId: String,
    val studentName: String,
    val studentEmail: String? = null,
    val companyName: String? = null,
    val offerTitle: String? = null,
    val status: String? = null
)

data class AdvisorActivityDto(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val createdAt: String? = null,
    val type: String? = null
)