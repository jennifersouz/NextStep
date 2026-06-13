package com.example.nextstep.ui.screens.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val loadError: String? = null,
    val actionError: String? = null,
    val isActionSuccess: Boolean = false
)

class TeacherRequestDetailViewModel(
    private val repository: TeacherRequestsRepository = TeacherRequestsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherRequestDetailUiState())
    val uiState: StateFlow<TeacherRequestDetailUiState> = _uiState.asStateFlow()

    fun loadRequestDetail(applicationId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = null) }
            repository.getRequestDetail(applicationId)
                .onSuccess { request ->
                    _uiState.update { it.copy(isLoading = false, request = request) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, loadError = "Não foi possível carregar os detalhes do pedido.") }
                }
        }
    }

    fun acceptRequest(applicationId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, actionError = null) }
            repository.acceptRequest(applicationId)
                .onSuccess {
                    _uiState.update { it.copy(isUpdating = false, isActionSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isUpdating = false, actionError = error.message ?: "Erro ao aceitar pedido.") }
                }
        }
    }

    fun rejectRequest(applicationId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, actionError = null) }
            repository.rejectRequest(applicationId)
                .onSuccess {
                    _uiState.update { it.copy(isUpdating = false, isActionSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isUpdating = false, actionError = error.message ?: "Erro ao rejeitar pedido.") }
                }
        }
    }

    fun openDocument(path: String, onUrlGenerated: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionError = null) }
            repository.getDocumentUrl(path)
                .onSuccess { url ->
                    onUrlGenerated(url)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(actionError = error.message ?: "Não foi possível abrir o documento.") }
                }
        }
    }

    fun clearActionError() {
        _uiState.update { it.copy(actionError = null) }
    }
}
