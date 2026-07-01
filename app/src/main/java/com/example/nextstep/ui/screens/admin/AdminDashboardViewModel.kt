package com.example.nextstep.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.model.ProfileDto
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
            val offersResult = repository.getPublishedOffersCount()
            val usersResult = repository.getUsersCount()
            val nameResult = repository.getAdminProfileName()
            val emailResult = repository.getAdminProfileEmail()
            val recentResult = repository.getRecentProfiles()
            val totalCompaniesResult = repository.getTotalCompaniesCount()
            val activeCompaniesResult = repository.getActiveCompaniesCount()

            val errors = listOfNotNull(
                internshipsResult.exceptionOrNull(),
                applicationsResult.exceptionOrNull(),
                offersResult.exceptionOrNull(),
                usersResult.exceptionOrNull()
            ).takeIf { it.isNotEmpty() }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                activeInternshipsCount = internshipsResult.getOrDefault(0),
                applicationsCount = applicationsResult.getOrDefault(0),
                publishedOffersCount = offersResult.getOrDefault(0),
                usersCount = usersResult.getOrDefault(0),
                adminName = nameResult.getOrDefault(""),
                adminEmail = emailResult.getOrDefault(""),
                recentActivities = recentResult.getOrDefault(emptyList()).map { it.toActivityUiModel() },
                recentActivitiesLoading = false,
                totalCompaniesCount = totalCompaniesResult.getOrDefault(0),
                activeCompaniesCount = activeCompaniesResult.getOrDefault(0),
                errorMessage = errors?.firstOrNull()?.message
            )
        }
    }
}

private fun ProfileDto.toActivityUiModel(): RecentActivityUiModel {
    val displayName = listOfNotNull(
        firstName?.takeIf { it.isNotBlank() },
        lastName?.takeIf { it.isNotBlank() }
    ).joinToString(" ").ifBlank { email }

    val type = when (role.trim().lowercase()) {
        "student", "aluno" -> RecentActivityType.STUDENT_CREATED
        "teacher", "docente" -> RecentActivityType.TEACHER_CREATED
        "advisor", "orientador" -> RecentActivityType.ADVISOR_CREATED
        "company", "empresa" -> RecentActivityType.COMPANY_CREATED
        "institution", "instituição", "instituicao" -> RecentActivityType.INSTITUTION_CREATED
        else -> RecentActivityType.STUDENT_CREATED
    }

    return RecentActivityUiModel(
        id = id,
        type = type,
        name = displayName,
        email = email,
        roleName = role,
        createdAt = createdAt
    )
}
