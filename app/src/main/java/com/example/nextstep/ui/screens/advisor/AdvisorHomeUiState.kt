package com.example.nextstep.ui.screens.advisor

import com.example.nextstep.data.model.AdvisorActivityDto
import com.example.nextstep.data.model.AdvisorAssignedStudentDto
import com.example.nextstep.data.model.AdvisorSummaryDto

data class AdvisorHomeUiState(
    val isLoading: Boolean = false,
    val advisorName: String = "",
    val summary: AdvisorSummaryDto = AdvisorSummaryDto(),
    val students: List<AdvisorAssignedStudentDto> = emptyList(),
    val recentActivities: List<AdvisorActivityDto> = emptyList(),
    val errorMessage: String? = null
)