package com.example.nextstep.ui.screens.advisor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.repository.AdvisorStudentsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdvisorStudentsViewModel : ViewModel() {

    private val repository = AdvisorStudentsRepository()

    private val _uiState = MutableStateFlow(AdvisorStudentsUiState(isLoading = true))
    val uiState: StateFlow<AdvisorStudentsUiState> = _uiState.asStateFlow()

    init {
        loadStudents()
    }

    fun loadStudents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository.getAssignedStudents()
                .onSuccess { students ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        students = students,
                        errorMessage = null
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

    fun onFilterSelected(filter: AdvisorStudentsFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    fun onSearchChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun getFilteredStudents(): List<com.example.nextstep.data.model.AdvisorAssignedStudentDto> {
        val state = _uiState.value
        var result = state.students

        // Apply filter
        result = when (state.selectedFilter) {
            AdvisorStudentsFilter.ALL -> result
            AdvisorStudentsFilter.ACTIVE -> result.filter { student ->
                val s = student.status?.lowercase() ?: ""
                s == "active" || s == "accepted" || s == "ativo" || s == "aceite"
            }
            AdvisorStudentsFilter.TO_COMPLETE -> result.filter { student ->
                val s = student.status?.lowercase() ?: ""
                s == "to_complete" || s == "por_concluir" || s == "por concluir" ||
                s == "pending" || s == "pendente"
            }
            AdvisorStudentsFilter.COMPLETED -> result.filter { student ->
                val s = student.status?.lowercase() ?: ""
                s == "completed" || s == "concluido" || s == "concluído"
            }
        }

        // Apply search
        val query = state.searchQuery.trim().lowercase()
        if (query.isNotBlank()) {
            result = result.filter { student ->
                student.studentName.lowercase().contains(query) ||
                student.studentEmail?.lowercase()?.contains(query) == true ||
                student.offerTitle?.lowercase()?.contains(query) == true
            }
        }

        return result
    }
}