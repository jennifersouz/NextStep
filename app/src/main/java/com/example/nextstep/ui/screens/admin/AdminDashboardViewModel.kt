package com.example.nextstep.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.repository.AdminDashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminDashboardViewModel : ViewModel() {

    private val repository = AdminDashboardRepository()

    private val _uiState = MutableStateFlow(AdminDashboardUiState(isLoading = true))
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val internshipsResult = repository.getActiveInternshipsCount()
            val applicationsResult = repository.getApplicationsCount()
            val evaluationsResult = repository.getCompletedEvaluationsCount()
            val usersResult = repository.getUsersCount()
            val nameResult = repository.getAdminProfileName()
            val recentResult = repository.getRecentProfiles()
            val totalCompaniesResult = repository.getTotalCompaniesCount()
            val activeCompaniesResult = repository.getActiveCompaniesCount()

            val errors = listOfNotNull(
                internshipsResult.exceptionOrNull(),
                applicationsResult.exceptionOrNull(),
                evaluationsResult.exceptionOrNull(),
                usersResult.exceptionOrNull()
            ).takeIf { it.isNotEmpty() }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                activeInternshipsCount = internshipsResult.getOrDefault(0),
                applicationsCount = applicationsResult.getOrDefault(0),
                completedEvaluationsCount = evaluationsResult.getOrDefault(0),
                usersCount = usersResult.getOrDefault(0),
                adminName = nameResult.getOrDefault(""),
                recentProfiles = recentResult.getOrDefault(emptyList()),
                totalCompaniesCount = totalCompaniesResult.getOrDefault(0),
                activeCompaniesCount = activeCompaniesResult.getOrDefault(0),
                errorMessage = errors?.firstOrNull()?.message
            )
        }
    }
}