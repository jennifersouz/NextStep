package com.example.nextstep.ui.screens.institution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.InstitutionHomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InstitutionHomeViewModel : ViewModel() {

    private val repository = InstitutionHomeRepository()

    private val _uiState = MutableStateFlow(InstitutionHomeUiState())
    val uiState: StateFlow<InstitutionHomeUiState> = _uiState.asStateFlow()

    fun loadHome() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val profileResult = repository.getInstitutionProfile()
            val usersResult = repository.getInstitutionUsers()

            val institutionName = profileResult.getOrNull()?.name.orEmpty()

            val users = usersResult.getOrNull().orEmpty()

            val totalStudents = users.count { it.targetRole == "student" }
            val totalTeachers = users.count { it.targetRole == "teacher" }
            val pendingInvites = users.count {
                it.acceptedAt == null || it.inviteStatus == "pending"
            }
            val acceptedInvites = users.count {
                it.acceptedAt != null || it.inviteStatus == "accepted"
            }
            val latestInvites = users
                .sortedByDescending { it.email }
                .take(5)

            val hasError = profileResult.isFailure && usersResult.isFailure

            _uiState.value = _uiState.value.copy(
                institutionName = institutionName,
                users = users,
                totalStudents = totalStudents,
                totalTeachers = totalTeachers,
                pendingInvites = pendingInvites,
                acceptedInvites = acceptedInvites,
                latestInvites = latestInvites,
                isLoading = false,
                errorMessageRes = if (hasError) {
                    R.string.institution_home_load_error
                } else {
                    null
                }
            )
        }
    }
}