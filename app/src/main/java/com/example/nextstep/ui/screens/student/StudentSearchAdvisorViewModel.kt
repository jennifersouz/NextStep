package com.example.nextstep.ui.screens.student

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.StudentSearchAdvisorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StudentSearchAdvisorViewModel : ViewModel() {

    private val repository = StudentSearchAdvisorRepository()

    private val _uiState = MutableStateFlow(StudentSearchAdvisorUiState())
    val uiState: StateFlow<StudentSearchAdvisorUiState> = _uiState.asStateFlow()

    fun loadTeachers(applicationId: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            val teachersResult = repository.getAllTeachers()
            
            if (teachersResult.isSuccess) {
                _uiState.update { it.copy(
                    isLoading = false,
                    teachers = teachersResult.getOrDefault(emptyList())
                )}
            } else {
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessageRes = R.string.error_loading_teachers
                )}
                return@launch
            }

            if (applicationId != null) {
                val appResult = repository.getApplicationTeacherStatus(applicationId)
                if (appResult.isSuccess) {
                    val (teacherId, teacherStatus) = appResult.getOrDefault(Pair(null, null))
                    Log.d("TeacherRequestDebug", "Loaded application status: teacherId=$teacherId, teacherStatus=$teacherStatus")
                    _uiState.update { it.copy(
                        currentTeacherProfileId = teacherId,
                        currentTeacherStatus = teacherStatus
                    )}
                }
            }
        }
    }

    fun onSearchChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun sendRequest(internshipId: String, teacherId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isSendingRequest = true,
                sendingTeacherId = teacherId,
                errorMessageRes = null
            )}

            val result = repository.sendOrientationRequest(internshipId, teacherId)

            if (result.isSuccess) {
                _uiState.update { it.copy(
                    isSendingRequest = false,
                    sendingTeacherId = null,
                    currentTeacherProfileId = teacherId,
                    currentTeacherStatus = "pending"
                )}
            } else {
                _uiState.update { it.copy(
                    isSendingRequest = false,
                    sendingTeacherId = null,
                    errorMessageRes = R.string.error_sending_request
                )}
            }
        }
    }
}
