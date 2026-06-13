package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.CompanyInternStudentRepository
import com.example.nextstep.data.repository.CompanyInternStudentsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyInternStudentProfileViewModel : ViewModel() {

    private val repository = CompanyInternStudentRepository()
    private val studentsRepository = CompanyInternStudentsRepository()

    private val _uiState = MutableStateFlow(CompanyInternStudentProfileUiState())
    val uiState: StateFlow<CompanyInternStudentProfileUiState> = _uiState.asStateFlow()

    private var currentApplicationId: String = ""

    fun loadProfile(applicationId: String) {
        currentApplicationId = applicationId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = repository.getInternStudentProfile(applicationId)

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    profile = result.getOrNull(),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                val errorMsg = result.exceptionOrNull()?.message.orEmpty()
                val errorRes = when {
                    errorMsg.contains("PERMISSION_DENIED", ignoreCase = true) ->
                        R.string.company_intern_profile_permission_denied
                    errorMsg.contains("NOT_IN_INTERNSHIP", ignoreCase = true) ->
                        R.string.company_intern_profile_not_in_internship
                    errorMsg.contains("APPLICATION_ID_EMPTY", ignoreCase = true) ->
                        R.string.company_intern_profile_load_error
                    errorMsg.contains("EMPLOYER_NOT_AUTHENTICATED", ignoreCase = true) ->
                        R.string.company_intern_profile_load_error
                    else ->
                        R.string.company_intern_profile_load_error
                }
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = errorRes
                )
            }
        }
    }

    // --- RF24: Status Update ---

    fun onMarkInactiveClick() {
        _uiState.value = _uiState.value.copy(showConfirmInactiveDialog = true)
    }

    fun onMarkActiveClick() {
        _uiState.value = _uiState.value.copy(showConfirmActiveDialog = true)
    }

    fun dismissStatusDialog() {
        _uiState.value = _uiState.value.copy(
            showConfirmInactiveDialog = false,
            showConfirmActiveDialog = false
        )
    }

    fun confirmMarkInactive() {
        updateStatus("inactive")
    }

    fun confirmMarkActive() {
        updateStatus("active")
    }

    private fun updateStatus(newStatus: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUpdatingStatus = true,
                statusUpdateErrorRes = null,
                statusUpdateSuccessRes = null,
                showConfirmInactiveDialog = false,
                showConfirmActiveDialog = false
            )

            val result = studentsRepository.updateInternStudentStatus(currentApplicationId, newStatus)

            if (result.isSuccess) {
                val updatedStudent = result.getOrThrow()
                _uiState.value = _uiState.value.copy(
                    isUpdatingStatus = false,
                    statusUpdateSuccessRes = R.string.company_intern_status_updated_success,
                    profile = _uiState.value.profile?.copy(
                        internshipStatus = updatedStudent.internshipStatus,
                        statusUpdatedAt = updatedStudent.statusUpdatedAt
                    )
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isUpdatingStatus = false,
                    statusUpdateErrorRes = R.string.company_intern_status_updated_error
                )
            }
        }
    }

    fun consumeStatusMessages() {
        _uiState.value = _uiState.value.copy(
            statusUpdateSuccessRes = null,
            statusUpdateErrorRes = null
        )
    }

    // --- RF27: Activities ---

    fun loadActivities(applicationId: String) {
        if (applicationId.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingActivities = true,
                activitiesErrorRes = null
            )

            val result = repository.getStudentActivities(applicationId)

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    activities = result.getOrNull().orEmpty(),
                    isLoadingActivities = false,
                    activitiesErrorRes = null
                )
            } else {
                _uiState.value.copy(
                    isLoadingActivities = false,
                    activitiesErrorRes = R.string.company_intern_activities_load_error
                )
            }
        }
    }

    // --- RF28: Advisor Evaluation ---

    fun loadAdvisorEvaluation(applicationId: String) {
        if (applicationId.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingEvaluation = true,
                evaluationErrorRes = null
            )

            val result = repository.getAdvisorEvaluation(applicationId)

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    advisorEvaluation = result.getOrNull(),
                    isLoadingEvaluation = false,
                    evaluationErrorRes = null
                )
            } else {
                _uiState.value.copy(
                    isLoadingEvaluation = false,
                    evaluationErrorRes = R.string.company_intern_evaluation_load_error
                )
            }
        }
    }

    // --- Tab Navigation ---

    fun onTabChange(tab: CompanyInternStudentTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)

        // Load data lazily when switching tabs
        when (tab) {
            CompanyInternStudentTab.ACTIVITIES -> {
                if (_uiState.value.activities.isEmpty() && _uiState.value.activitiesErrorRes == null) {
                    loadActivities(currentApplicationId)
                }
            }
            CompanyInternStudentTab.EVALUATION -> {
                if (_uiState.value.advisorEvaluation == null && _uiState.value.evaluationErrorRes == null) {
                    loadAdvisorEvaluation(currentApplicationId)
                }
            }
            else -> { /* Summary is loaded via loadProfile */ }
        }
    }

    // --- Activity Filters ---

    fun onActivityFilterChange(filter: CompanyActivityFilter) {
        _uiState.value = _uiState.value.copy(selectedActivityFilter = filter)
    }

    fun getFilteredActivities(): List<com.example.nextstep.data.model.CompanyStudentActivityDto> {
        val activities = _uiState.value.activities
        val filter = _uiState.value.selectedActivityFilter
        return when (filter) {
            CompanyActivityFilter.ALL -> activities
            CompanyActivityFilter.PENDING -> activities.filter {
                it.status?.trim()?.lowercase() == "pending"
            }
            CompanyActivityFilter.IN_PROGRESS -> activities.filter {
                it.status?.trim()?.lowercase() == "in_progress"
            }
            CompanyActivityFilter.COMPLETED -> activities.filter {
                it.status?.trim()?.lowercase() == "completed"
            }
        }
    }

    // --- Documents ---

    fun openCv() {
        val path = _uiState.value.profile?.cvPath
        if (path.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                documentErrorRes = R.string.company_application_document_missing
            )
            return
        }
        createSignedUrl(path)
    }

    fun openMotivationLetter() {
        val path = _uiState.value.profile?.motivationLetterPath
        if (path.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                documentErrorRes = R.string.company_application_document_missing
            )
            return
        }
        createSignedUrl(path)
    }

    fun consumeDocumentUrl() {
        _uiState.value = _uiState.value.copy(documentUrlToOpen = null)
    }

    fun onDocumentOpenFailed() {
        _uiState.value = _uiState.value.copy(
            documentUrlToOpen = null,
            documentErrorRes = R.string.company_application_document_open_error
        )
    }

    private fun createSignedUrl(path: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isOpeningDocument = true,
                documentErrorRes = null,
                documentUrlToOpen = null
            )

            val result = repository.createSignedDocumentUrl(path)

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isOpeningDocument = false,
                    documentUrlToOpen = result.getOrNull(),
                    documentErrorRes = null
                )
            } else {
                _uiState.value.copy(
                    isOpeningDocument = false,
                    documentUrlToOpen = null,
                    documentErrorRes = R.string.company_application_document_open_error
                )
            }
        }
    }
}
