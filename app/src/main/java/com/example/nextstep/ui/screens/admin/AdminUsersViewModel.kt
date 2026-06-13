package com.example.nextstep.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.model.AdminProfileDto
import com.example.nextstep.data.model.AdminProfileUpdateDto
import com.example.nextstep.data.repository.AdminUsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    users = users,
                    filteredUsers = applyFilter(users)
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Não foi possível carregar os utilizadores."
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFiltersAndSearch()
    }

    fun onFilterChange(filter: AdminUsersFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        applyFiltersAndSearch()
    }

    fun updateUser(userId: String, updateData: AdminProfileUpdateDto) {
        viewModelScope.launch {
            val result = repository.updateUser(userId, updateData)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    successMessage = "Utilizador atualizado com sucesso."
                )
                loadUsers()
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun setUserActive(userId: String, isActive: Boolean) {
        viewModelScope.launch {
            val result = repository.setUserActive(userId, isActive)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    successMessage = if (isActive) "Conta ativada com sucesso." else "Conta desativada com sucesso."
                )
                loadUsers()
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            val result = repository.deleteUserProfile(userId)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    successMessage = "Utilizador removido com sucesso."
                )
                loadUsers()
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.message
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

    private fun applyFilter(users: List<AdminProfileDto>): List<AdminProfileDto> {
        return when (_uiState.value.selectedFilter) {
            AdminUsersFilter.ALL -> users
            AdminUsersFilter.STUDENTS -> users.filter { it.role == "student" }
            AdminUsersFilter.TEACHERS -> users.filter { it.role == "teacher" }
            AdminUsersFilter.COMPANIES -> users.filter { it.role == "company" }
            AdminUsersFilter.ADMINS -> users.filter { it.role == "admin" }
            AdminUsersFilter.ACTIVE -> users.filter { it.isActive == true }
            AdminUsersFilter.INACTIVE -> users.filter { it.isActive == false }
        }
    }
}
