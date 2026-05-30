package com.example.nextstep.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.StudentApplicationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentSubmittedApplicationsViewModel : ViewModel() {

    private val repository = StudentApplicationsRepository()

    private val _uiState = MutableStateFlow(StudentSubmittedApplicationsUiState())
    val uiState: StateFlow<StudentSubmittedApplicationsUiState> = _uiState.asStateFlow()

    init {
        loadApplications()
    }

    fun loadApplications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = repository.getSubmittedApplications()

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    applications = result.getOrDefault(emptyList()),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    applications = emptyList(),
                    isLoading = false,
                    errorMessageRes = R.string.student_submitted_applications_load_error
                )
            }
        }
    }
}