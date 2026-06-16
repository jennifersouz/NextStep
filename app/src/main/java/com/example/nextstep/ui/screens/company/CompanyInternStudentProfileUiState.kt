package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.data.model.CompanyAdvisorEvaluationDto
import com.example.nextstep.data.model.CompanyEvaluationDto
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
    // Evaluation (RF28) — Advisor evaluation
    val advisorEvaluation: CompanyAdvisorEvaluationDto? = null,
    val isLoadingEvaluation: Boolean = false,
    @StringRes val evaluationErrorRes: Int? = null,
    // Evaluation (RF91) — Company evaluation
    val companyEvaluation: CompanyEvaluationDto? = null,
    val isLoadingCompanyEvaluation: Boolean = false,
    @StringRes val companyEvaluationErrorRes: Int? = null,
    val isSavingCompanyEvaluation: Boolean = false,
    @StringRes val companyEvaluationSaveSuccessRes: Int? = null,
    @StringRes val companyEvaluationSaveErrorRes: Int? = null,
    // Company evaluation form state
    val companyEvaluationGradeText: String = "",
    val companyEvaluationFeedbackText: String = "",
    val companyEvaluationStrengthsText: String = "",
    val companyEvaluationImprovementsText: String = "",
    val companyEvaluationRecommendationText: String = "",
    @StringRes val companyEvaluationGradeErrorRes: Int? = null,
    @StringRes val companyEvaluationFeedbackErrorRes: Int? = null,
    
    // RF24: Status Update
    val isUpdatingStatus: Boolean = false,
    @StringRes val statusUpdateErrorRes: Int? = null,
    @StringRes val statusUpdateSuccessRes: Int? = null,
    val showConfirmInactiveDialog: Boolean = false,
    val showConfirmActiveDialog: Boolean = false
)
