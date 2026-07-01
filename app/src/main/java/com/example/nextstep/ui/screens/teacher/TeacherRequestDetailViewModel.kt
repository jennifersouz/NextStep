package com.example.nextstep.ui.screens.teacher

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.model.TeacherOrientationRequestDto
import com.example.nextstep.data.repository.TeacherRequestsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TeacherRequestDetailUiState(
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val request: TeacherOrientationRequestDto? = null,
    @StringRes val loadErrorRes: Int? = null,
    @StringRes val actionErrorRes: Int? = null,
    val isActionSuccess: Boolean = false
)

class TeacherRequestDetailViewModel(
    private val repository: TeacherRequestsRepository = TeacherRequestsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherRequestDetailUiState())
    val uiState: StateFlow<TeacherRequestDetailUiState> = _uiState.asStateFlow()

    fun loadRequestDetail(applicationId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadErrorRes = null) }
            repository.getRequestDetail(applicationId)
                .onSuccess { request ->
                    _uiState.update { it.copy(isLoading = false, request = request) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, loadErrorRes = R.string.teacher_request_detail_load_error) }
                }
        }
    }

    fun acceptRequest(applicationId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, actionErrorRes = null) }
            repository.acceptRequest(applicationId)
                .onSuccess {
                    _uiState.update { it.copy(isUpdating = false, isActionSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isUpdating = false, actionErrorRes = R.string.teacher_accept_request_error) }
                }
        }
    }

    fun rejectRequest(applicationId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, actionErrorRes = null) }
            repository.rejectRequest(applicationId)
                .onSuccess {
                    _uiState.update { it.copy(isUpdating = false, isActionSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isUpdating = false, actionErrorRes = R.string.teacher_reject_request_error) }
                }
        }
    }

    fun openDocument(path: String, onUrlGenerated: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionErrorRes = null) }
            repository.getDocumentUrl(path)
                .onSuccess { url ->
                    onUrlGenerated(url)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(actionErrorRes = R.string.error_opening_document) }
                }
        }
    }

    fun clearActionError() {
        _uiState.update { it.copy(actionErrorRes = null) }
    }
}
