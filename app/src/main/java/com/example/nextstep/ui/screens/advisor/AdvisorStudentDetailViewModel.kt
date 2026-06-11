package com.example.nextstep.ui.screens.advisor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.repository.AdvisorStudentDetailRepository
import com.example.nextstep.data.repository.AdvisorTasksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdvisorStudentDetailViewModel : ViewModel() {

    private val repository = AdvisorStudentDetailRepository()
    private val tasksRepository = AdvisorTasksRepository()

    private val _uiState = MutableStateFlow(AdvisorStudentDetailUiState())
    val uiState: StateFlow<AdvisorStudentDetailUiState> = _uiState.asStateFlow()

    fun loadDetail(applicationId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository.getStudentDetail(applicationId)
                .onSuccess { detail ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        detail = detail
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
        }
    }

    fun updateTaskStatus(taskId: String, status: String, applicationId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true) // or a separate isUpdating state
            tasksRepository.updateTaskStatus(taskId, status)
                .onSuccess {
                    loadDetail(applicationId)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Erro ao atualizar tarefa: ${exception.message}"
                    )
                }
        }
    }

    fun refresh(applicationId: String) {
        loadDetail(applicationId)
    }
}
