package com.example.nextstep.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.remote.SupabaseClientProvider
import com.example.nextstep.data.repository.StudentSentAdvisorRequestsRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StudentSentAdvisorRequestsViewModel : ViewModel() {

    private val repository = StudentSentAdvisorRequestsRepository()
    private val auth = SupabaseClientProvider.client.auth

    private val _uiState = MutableStateFlow(StudentSentAdvisorRequestsUiState())
    val uiState: StateFlow<StudentSentAdvisorRequestsUiState> = _uiState.asStateFlow()

    fun loadRequests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            val studentId = auth.currentUserOrNull()?.id
            if (studentId == null) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessageRes = R.string.error_loading_sent_requests
                    )
                }
                return@launch
            }

            val result = repository.getSentRequests(studentId)

            if (result.isSuccess) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        requests = result.getOrNull() ?: emptyList()
                    )
                }
            } else {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessageRes = R.string.error_loading_sent_requests
                    )
                }
            }
        }
    }

    fun onSearchChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun cancelRequest(applicationId: String) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isCancellingId = applicationId,
                    errorMessageRes = null
                )
            }

            val result = repository.cancelRequest(applicationId)

            if (result.isSuccess) {
                _uiState.update { state ->
                    val updatedRequests = state.requests.filter { it.id != applicationId }
                    state.copy(
                        requests = updatedRequests,
                        isCancellingId = null
                    )
                }
            } else {
                _uiState.update { 
                    it.copy(
                        isCancellingId = null,
                        errorMessageRes = R.string.error_cancelling_request
                    )
                }
            }
        }
    }
}
