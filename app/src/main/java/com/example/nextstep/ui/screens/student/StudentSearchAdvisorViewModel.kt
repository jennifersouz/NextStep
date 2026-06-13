package com.example.nextstep.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.StudentSearchAdvisorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentSearchAdvisorViewModel : ViewModel() {

    private val repository = StudentSearchAdvisorRepository()

    private val _uiState = MutableStateFlow(StudentSearchAdvisorUiState())
    val uiState: StateFlow<StudentSearchAdvisorUiState> = _uiState.asStateFlow()

    fun loadTeachers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessageRes = null)

            val teachersResult = repository.getAllTeachers()
            
            if (teachersResult.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    teachers = teachersResult.getOrDefault(emptyList())
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.error_loading_teachers
                )
            }
        }
    }

    fun onSearchChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun sendRequest(internshipId: String, teacherId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSendingRequest = true,
                sendingTeacherId = teacherId,
                errorMessageRes = null
            )

            val result = repository.sendOrientationRequest(internshipId, teacherId)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSendingRequest = false,
                    sendingTeacherId = null,
                    isRequestSent = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSendingRequest = false,
                    sendingTeacherId = null,
                    errorMessageRes = R.string.error_sending_request
                )
            }
        }
    }
}
