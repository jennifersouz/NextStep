package com.example.nextstep.ui.screens.institution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.model.InstitutionUserDto
import com.example.nextstep.data.repository.InstitutionUsersRepository
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
            } else {
                _uiState.value = _uiState.value.copy(
                    users = emptyList()
                )
            }
        }
    }

    fun selectFilter(filter: InstitutionUserFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
}

enum class InstitutionUserFilter {
    ALL,
    STUDENTS,
    TEACHERS,
    PENDING,
    ACCEPTED
}

data class InstitutionUsersUiState(
    val users: List<InstitutionUserDto> = emptyList(),
    val isLoading: Boolean = false,
    val selectedFilter: InstitutionUserFilter = InstitutionUserFilter.ALL,
    val searchQuery: String = ""
)
