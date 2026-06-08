package com.example.nextstep.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.repository.StudentAssignedAdvisorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentAssignedAdvisorViewModel : ViewModel() {

    private val repository = StudentAssignedAdvisorRepository()

    private val _uiState = MutableStateFlow(StudentAssignedAdvisorUiState())
    val uiState: StateFlow<StudentAssignedAdvisorUiState> = _uiState.asStateFlow()

    fun loadAssignedAdvisor(applicationId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = repository.getAssignedAdvisor(applicationId)

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    assignedAdvisor = result.getOrNull(),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    assignedAdvisor = null,
                    isLoading = false,
                    errorMessageRes = null
                )
            }
        }
    }
}