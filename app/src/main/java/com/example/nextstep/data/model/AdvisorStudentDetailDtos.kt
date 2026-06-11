package com.example.nextstep.data.model

data class AdvisorStudentDetailDto(
    val applicationId: String,
    val studentName: String,
    val studentEmail: String? = null,
    val companyName: String? = null,
    val offerTitle: String? = null,
    val course: String? = null,
    val status: String? = null,
    val startDate: String? = null,
    val expectedEndDate: String? = null,
    val completedTasks: Int = 0,
    val totalTasks: Int = 0,
    val tasks: List<AdvisorTaskDto> = emptyList(),
    val documents: List<AdvisorDocumentDto> = emptyList(),
    val evaluation: AdvisorEvaluationDto? = null
)

data class AdvisorTaskDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val status: String? = null,
    val dueDate: String? = null
)

data class AdvisorDocumentDto(
    val id: String,
    val name: String,
    val type: String? = null,
    val url: String? = null
)

data class AdvisorEvaluationDto(
    val grade: String? = null,
    val comments: String? = null,
    val submittedAt: String? = null
)