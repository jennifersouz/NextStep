package com.example.nextstep.ui.screens.advisor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.repository.AdvisorTasksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdvisorTasksViewModel : ViewModel() {

    private val repository = AdvisorTasksRepository()

    private val _uiState = MutableStateFlow(AdvisorTasksUiState(isLoading = true))
    val uiState: StateFlow<AdvisorTasksUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository.getAdvisorTasks()
                .onSuccess { tasks ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tasks = tasks
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

    fun onFilterSelected(filter: AdvisorTaskFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    fun getFilteredTasks(): List<com.example.nextstep.data.model.AdvisorTaskListItemDto> {
        val state = _uiState.value
        return when (state.selectedFilter) {
            AdvisorTaskFilter.ALL -> state.tasks
            AdvisorTaskFilter.PENDING -> state.tasks.filter {
                val s = it.status.lowercase()
                s == "pending" || s == "pendente" || s == "to_complete" || s == "por_concluir"
            }
            AdvisorTaskFilter.COMPLETED -> state.tasks.filter {
                val s = it.status.lowercase()
                s == "completed" || s == "concluido" || s == "concluída" || s == "done"
            }
        }
    }
}