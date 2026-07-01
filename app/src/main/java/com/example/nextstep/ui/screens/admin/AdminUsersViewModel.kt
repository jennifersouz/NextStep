package com.example.nextstep.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.model.AdminProfileDto
import com.example.nextstep.data.model.AdminProfileUpdateDto
import com.example.nextstep.data.repository.AdminUsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class UserTypeFilter {
    ALL,
    STUDENTS,
    TEACHERS,
    COMPANIES,
    ADVISORS,
    INSTITUTIONS,
    ADMINISTRATORS
}

enum class UserStatusFilter {
    ALL,
    ACTIVE,
    INACTIVE,
    ARCHIVED,
    PENDING,
    ACCEPTED
}

fun UserTypeFilter.labelRes(): Int {
    return when (this) {
        UserTypeFilter.ALL -> R.string.filter_all_masc
        UserTypeFilter.STUDENTS -> R.string.tab_students
        UserTypeFilter.TEACHERS -> R.string.tab_teachers
        UserTypeFilter.COMPANIES -> R.string.companies_label
        UserTypeFilter.ADVISORS -> R.string.user_type_advisors
        UserTypeFilter.INSTITUTIONS -> R.string.user_type_institutions
        UserTypeFilter.ADMINISTRATORS -> R.string.user_type_admins
    }
}

fun UserStatusFilter.labelRes(): Int {
    return when (this) {
        UserStatusFilter.ALL -> R.string.filter_all_masc
        UserStatusFilter.ACTIVE -> R.string.filter_active
        UserStatusFilter.INACTIVE -> R.string.filter_inactive
        UserStatusFilter.ARCHIVED -> R.string.filter_archived
        UserStatusFilter.PENDING -> R.string.filter_pending
        UserStatusFilter.ACCEPTED -> R.string.filter_accepted
    }
}

class AdminUsersViewModel : ViewModel() {

    private val repository = AdminUsersRepository()

    private val _uiState = MutableStateFlow(AdminUsersUiState(isLoading = true))
    val uiState: StateFlow<AdminUsersUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = repository.getUsers()

            if (result.isSuccess) {
                val users = result.getOrDefault(emptyList())
                val refreshedSelected = _uiState.value.selectedUser?.let { current ->
                    users.find { it.id == current.id }
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    users = users,
                    filteredUsers = applyFilter(users),
                    selectedUser = refreshedSelected ?: _uiState.value.selectedUser
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.error_could_not_load_users
                )
            }
        }
    }

    fun selectUser(profile: AdminProfileDto) {
        _uiState.value = _uiState.value.copy(selectedUser = profile)
    }

    fun clearSelectedUser() {
        _uiState.value = _uiState.value.copy(selectedUser = null)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFiltersAndSearch()
    }

    fun onTypeFilterChange(type: UserTypeFilter) {
        _uiState.value = _uiState.value.copy(selectedTypeFilter = type)
        applyFiltersAndSearch()
    }

    fun onStatusFilterChange(status: UserStatusFilter) {
        _uiState.value = _uiState.value.copy(selectedStatusFilter = status)
        applyFiltersAndSearch()
    }

    fun updateUser(userId: String, updateData: AdminProfileUpdateDto) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = repository.updateUser(userId, updateData)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    successMessageRes = R.string.user_updated_success
                )
                loadUsers()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = mapGenericError(result.exceptionOrNull()?.message)
                )
            }
        }
    }

    fun toggleUserActive(userId: String, active: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = if (active) {
                repository.reactivateUser(userId)
            } else {
                repository.deactivateUser(userId)
            }

            if (result.isSuccess) {
                val updatedUser = repository.getUserById(userId).getOrNull()
                val users = _uiState.value.users.map {
                    if (it.id == userId) (updatedUser ?: it) else it
                }

                _uiState.value = _uiState.value.copy(
                    users = users,
                    filteredUsers = applyFilter(users),
                    selectedUser = updatedUser ?: _uiState.value.selectedUser,
                    isLoading = false,
                    successMessageRes = if (active) {
                        R.string.user_reactivated_success
                    } else {
                        R.string.user_deactivated_success
                    }
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.error_could_not_update_user_status
                )
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            val result = repository.deactivateUser(userId)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    successMessageRes = R.string.user_deleted_success,
                    selectedUser = null
                )
                loadUsers()
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessageRes = mapGenericError(result.exceptionOrNull()?.message)
                )
            }
        }
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun applyFiltersAndSearch() {
        val state = _uiState.value
        val filtered = applyFilter(state.users)

        _uiState.value = _uiState.value.copy(
            filteredUsers = if (state.searchQuery.isBlank()) {
                filtered
            } else {
                filtered.filter { profile ->
                    profile.email?.contains(state.searchQuery, ignoreCase = true) == true ||
                            profile.firstName?.contains(state.searchQuery, ignoreCase = true) == true ||
                            profile.lastName?.contains(state.searchQuery, ignoreCase = true) == true
                }
            }
        )
    }

    private fun mapGenericError(message: String?): Int {
        val normalized = message.orEmpty().lowercase()
        return when {
            "already exists" in normalized || "duplicate" in normalized ->
                R.string.error_email_already_exists
            "not found" in normalized ->
                R.string.error_user_not_found
            "permission" in normalized || "unauthorized" in normalized ->
                R.string.error_could_not_update_user_status
            else ->
                R.string.error_could_not_update_user
        }
    }

    private fun applyFilter(users: List<AdminProfileDto>): List<AdminProfileDto> {
        val state = _uiState.value

        return users.filter { user ->
            val matchesType = when (state.selectedTypeFilter) {
                UserTypeFilter.ALL -> true
                UserTypeFilter.STUDENTS -> user.role?.trim()?.lowercase() == "student"
                UserTypeFilter.TEACHERS -> user.role?.trim()?.lowercase() == "teacher"
                UserTypeFilter.COMPANIES -> user.role?.trim()?.lowercase() == "company"
                UserTypeFilter.ADVISORS -> user.role?.trim()?.lowercase() == "advisor"
                UserTypeFilter.INSTITUTIONS -> user.role?.trim()?.lowercase() in setOf("institution", "instituicao")
                UserTypeFilter.ADMINISTRATORS -> user.role?.trim()?.lowercase() == "admin"
            }

            val matchesStatus = when (state.selectedStatusFilter) {
                UserStatusFilter.ALL -> true
                UserStatusFilter.ACTIVE -> user.isActive == true && !user.isArchived
                UserStatusFilter.INACTIVE -> user.isActive == false && !user.isArchived
                UserStatusFilter.ARCHIVED -> user.isArchived
                UserStatusFilter.PENDING -> false
                else -> true
            }

            matchesType && matchesStatus
        }
    }
}
