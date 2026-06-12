package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeacherStudentDto(
    @SerialName("application_id")
    val applicationId: String,
    @SerialName("student_profile_id")
    val studentProfileId: String,
    @SerialName("student_name")
    val studentName: String,
    @SerialName("student_email")
    val studentEmail: String? = null,
    @SerialName("offer_title")
    val offerTitle: String? = null,
    @SerialName("company_name")
    val companyName: String? = null,
    val status: String? = null,
    @SerialName("tasks_count")
    val tasksCount: Int = 0,
    @SerialName("completed_tasks_count")
    val completedTasksCount: Int = 0,
    @SerialName("has_pending_evaluation")
    val hasPendingEvaluation: Boolean = false,
    @SerialName("created_at")
    val createdAt: String? = null
)