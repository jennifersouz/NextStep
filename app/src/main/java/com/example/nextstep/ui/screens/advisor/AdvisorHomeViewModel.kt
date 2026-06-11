package com.example.nextstep.ui.screens.advisor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.repository.AdvisorHomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdvisorHomeViewModel : ViewModel() {

    private val repository = AdvisorHomeRepository()

    private val _uiState = MutableStateFlow(AdvisorHomeUiState(isLoading = true))
    val uiState: StateFlow<AdvisorHomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val nameResult = repository.getAdvisorName()
            val summaryResult = repository.getAdvisorSummary()
            val studentsResult = repository.getAssignedStudentsPreview()
            val activitiesResult = repository.getRecentActivities()

            val errors = listOfNotNull(
                nameResult.exceptionOrNull(),
                summaryResult.exceptionOrNull(),
                studentsResult.exceptionOrNull()
            ).takeIf { it.isNotEmpty() }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                advisorName = nameResult.getOrDefault(""),
                summary = summaryResult.getOrDefault(com.example.nextstep.data.model.AdvisorSummaryDto()),
                students = studentsResult.getOrDefault(emptyList()),
                recentActivities = activitiesResult.getOrDefault(emptyList()),
                errorMessage = errors?.firstOrNull()?.message
            )
        }
    }
}