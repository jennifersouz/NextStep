package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.data.model.CompanyAdvisorEvaluationDto
import com.example.nextstep.data.model.CompanyInternStudentProfileDto
import com.example.nextstep.data.model.CompanyStudentActivityDto

enum class CompanyInternStudentTab {
    SUMMARY,
    ACTIVITIES,
    EVALUATION
}

enum class CompanyActivityFilter {
    ALL,
    PENDING,
    IN_PROGRESS,
    COMPLETED
}

data class CompanyInternStudentProfileUiState(
    val profile: CompanyInternStudentProfileDto? = null,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
    val isOpeningDocument: Boolean = false,
    @StringRes val documentErrorRes: Int? = null,
    val documentUrlToOpen: String? = null,
    // Tabs
    val selectedTab: CompanyInternStudentTab = CompanyInternStudentTab.SUMMARY,
    // Activities (RF27)
    val activities: List<CompanyStudentActivityDto> = emptyList(),
    val isLoadingActivities: Boolean = false,
    @StringRes val activitiesErrorRes: Int? = null,
    val selectedActivityFilter: CompanyActivityFilter = CompanyActivityFilter.ALL,
    // Evaluation (RF28)
    val advisorEvaluation: CompanyAdvisorEvaluationDto? = null,
    val isLoadingEvaluation: Boolean = false,
    @StringRes val evaluationErrorRes: Int? = null,
    
    // RF24: Status Update
    val isUpdatingStatus: Boolean = false,
    @StringRes val statusUpdateErrorRes: Int? = null,
    @StringRes val statusUpdateSuccessRes: Int? = null,
    val showConfirmInactiveDialog: Boolean = false,
    val showConfirmActiveDialog: Boolean = false
)
