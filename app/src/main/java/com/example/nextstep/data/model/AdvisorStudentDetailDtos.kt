package com.example.nextstep.data.model

import kotlinx.serialization.Serializable

@Serializable
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
    val tasks: List<AdvisorTaskListItemDto> = emptyList()
    // A avaliação é gerida separadamente via AdvisorEvaluationDto em AdvisorStudentDetailUiState
)

/**
 * DTO for documents used in TeacherStudentDetailDto.
 * Kept here because Teacher screens still use it.
 */
@Serializable
data class AdvisorDocumentDto(
    val id: String,
    val name: String,
    val type: String? = null,
    val url: String? = null
)
