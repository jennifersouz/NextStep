package com.example.nextstep.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.StudentAdvisorsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentAdvisorsViewModel : ViewModel() {

    private val repository = StudentAdvisorsRepository()

    private val _uiState = MutableStateFlow(StudentAdvisorsUiState())
    val uiState: StateFlow<StudentAdvisorsUiState> = _uiState.asStateFlow()

    init {
        loadAdvisors()
    }

    fun loadAdvisors() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = repository.getAvailableAdvisors()

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    advisors = result.getOrDefault(emptyList()),
                    isLoading = false
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.student_advisors_load_error
                )
            }
        }
    }

    fun onSearchChange(value: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = value
        )
    }

    fun sendRequest(advisorProfileId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                sendingAdvisorId = advisorProfileId,
                errorMessageRes = null
            )

            val result = repository.sendAdvisorRequest(advisorProfileId)

            if (result.isSuccess) {
                loadAdvisors()
            } else {
                _uiState.value = _uiState.value.copy(
                    sendingAdvisorId = null,
                    errorMessageRes = R.string.student_advisor_request_error
                )
            }
        }
    }
}