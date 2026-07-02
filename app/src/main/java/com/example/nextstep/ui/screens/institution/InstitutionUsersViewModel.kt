package com.example.nextstep.ui.screens.institution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.model.InstitutionUserDto
import com.example.nextstep.data.repository.InstitutionUsersRepository
import com.example.nextstep.ui.screens.admin.UserStatusFilter
import com.example.nextstep.ui.screens.admin.UserTypeFilter
import com.example.nextstep.ui.screens.admin.labelRes
import com.example.nextstep.ui.utils.isInstitutionArchived
import com.example.nextstep.ui.utils.isInviteAccepted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InstitutionUsersViewModel : ViewModel() {

    private val institutionUsersRepository = InstitutionUsersRepository()

    private val _uiState = MutableStateFlow(InstitutionUsersUiState())
    val uiState: StateFlow<InstitutionUsersUiState> = _uiState.asStateFlow()

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = institutionUsersRepository.getInstitutionUsers()

            _uiState.value = _uiState.value.copy(isLoading = false)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    users = result.getOrNull() ?: emptyList()
                )
                applyFiltersAndSearch()
            } else {
                _uiState.value = _uiState.value.copy(users = emptyList())
            }
        }
    }

    fun onTypeFilterChange(type: UserTypeFilter) {
        _uiState.value = _uiState.value.copy(selectedTypeFilter = type)
        applyFiltersAndSearch()
    }

    fun onStatusFilterChange(status: UserStatusFilter) {
        _uiState.value = _uiState.value.copy(selectedStatusFilter = status)
        applyFiltersAndSearch()
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFiltersAndSearch()
    }

    fun deleteInvite(invite: InstitutionUserDto) {
        if (invite.isInviteAccepted()) {
            _uiState.value = _uiState.value.copy(
                errorMessageRes = R.string.cannot_delete_accepted_invite
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = institutionUsersRepository.deletePendingInvite(invite.inviteId)

            if (result.isSuccess) {
                loadUsers()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.delete_invite_error
                )
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessageRes = null)
    }

    private fun applyFiltersAndSearch() {
        val state = _uiState.value
        val filtered = applyFilter(state.users)

        _uiState.value = _uiState.value.copy(
            filteredUsers = if (state.searchQuery.isBlank()) {
                filtered
            } else {
                val query = state.searchQuery.trim().lowercase()
                filtered.filter { user ->
                    val fullName = "${user.firstName.orEmpty()} ${user.lastName.orEmpty()}".lowercase()
                    fullName.contains(query) ||
                        user.email.lowercase().contains(query) ||
                        user.course.orEmpty().lowercase().contains(query) ||
                        user.department.orEmpty().lowercase().contains(query) ||
                        user.studentNumber.orEmpty().lowercase().contains(query)
                }
            }
        )
    }

    private fun applyFilter(users: List<InstitutionUserDto>): List<InstitutionUserDto> {
        val state = _uiState.value

        return users.filter { user ->
            val matchesType = when (state.selectedTypeFilter) {
                UserTypeFilter.ALL -> true
                UserTypeFilter.STUDENTS -> user.targetRole.trim().lowercase() == "student"
                UserTypeFilter.TEACHERS -> user.targetRole.trim().lowercase() == "teacher"
                else -> true
            }

            val accepted = user.isInviteAccepted()
            val archived = user.isInstitutionArchived()

            val matchesStatus = when (state.selectedStatusFilter) {
                UserStatusFilter.ALL -> true
                UserStatusFilter.PENDING -> !accepted && !archived
                UserStatusFilter.ACCEPTED -> accepted && !archived
                UserStatusFilter.ARCHIVED -> archived
                else -> true
            }

            matchesType && matchesStatus
        }
    }
}

data class InstitutionUsersUiState(
    val users: List<InstitutionUserDto> = emptyList(),
    val filteredUsers: List<InstitutionUserDto> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTypeFilter: UserTypeFilter = UserTypeFilter.ALL,
    val selectedStatusFilter: UserStatusFilter = UserStatusFilter.ALL,
    val searchQuery: String = "",
    val errorMessageRes: Int? = null
)
