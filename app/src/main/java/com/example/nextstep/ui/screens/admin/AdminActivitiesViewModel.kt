package com.example.nextstep.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.model.ProfileDto
import com.example.nextstep.data.repository.AdminDashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminActivitiesViewModel : ViewModel() {

    private val repository = AdminDashboardRepository()

    private val _uiState = MutableStateFlow(AdminActivitiesUiState(isLoading = true))
    val uiState: StateFlow<AdminActivitiesUiState> = _uiState.asStateFlow()

    init {
        loadActivities()
    }

    fun loadActivities() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = repository.getAllProfiles()

            result.onSuccess { profiles ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    activities = profiles.map { it.toActivityUiModel() }
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message
                )
            }
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
