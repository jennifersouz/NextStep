package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeacherStudentDetailDto(
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
    val course: String? = null,
    val status: String? = null,
    val location: String? = null,
    @SerialName("work_mode")
    val workMode: String? = null,
    val duration: String? = null,
    @SerialName("start_date")
    val startDate: String? = null,
    @SerialName("expected_end_date")
    val expectedEndDate: String? = null,
    @SerialName("company_advisor_name")
    val companyAdvisorName: String? = null,
    @SerialName("last_activity_at")
    val lastActivityAt: String? = null,
    @SerialName("completed_tasks")
    val completedTasks: Int = 0,
    @SerialName("total_tasks")
    val totalTasks: Int = 0,
    val tasks: List<AdvisorTaskListItemDto> = emptyList(),
    val documents: List<AdvisorDocumentDto> = emptyList(),
    val evaluation: AdvisorEvaluationDto? = null
)

data class TeacherStudentDetailNonSerializable(
    val applicationId: String,
    val studentProfileId: String,
    val studentName: String,
    val studentEmail: String? = null,
    val offerTitle: String? = null,
    val companyName: String? = null,
    val course: String? = null,
    val status: String? = null,
    val location: String? = null,
    val workMode: String? = null,
    val duration: String? = null,
    val startDate: String? = null,
    val expectedEndDate: String? = null,
    val companyAdvisorName: String? = null,
    val lastActivityAt: String? = null,
    val completedTasks: Int = 0,
    val totalTasks: Int = 0,
    val tasks: List<AdvisorTaskListItemDto> = emptyList(),
    val documents: List<AdvisorDocumentDto> = emptyList(),
    val evaluation: TeacherEvaluationDto? = null
)

