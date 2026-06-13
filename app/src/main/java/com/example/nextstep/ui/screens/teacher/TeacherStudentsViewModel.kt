package com.example.nextstep.ui.screens.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.model.TeacherStudentDto
import com.example.nextstep.data.repository.TeacherStudentsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeacherStudentsViewModel : ViewModel() {

    private val repository = TeacherStudentsRepository()

    private val _uiState = MutableStateFlow(TeacherStudentsUiState(isLoading = true))
    val uiState: StateFlow<TeacherStudentsUiState> = _uiState.asStateFlow()

    init {
        loadStudents()
    }

    fun loadStudents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository.getStudents()
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

    fun onFilterSelected(filter: TeacherStudentsFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun getFilteredStudents(): List<TeacherStudentDto> {
        val state = _uiState.value
        var result = state.students

        // Apply filter
        result = when (state.selectedFilter) {
            TeacherStudentsFilter.ALL -> result
            TeacherStudentsFilter.ACTIVE -> result.filter { student ->
                val s = student.status?.lowercase() ?: ""
                s == "active" || s == "accepted" || s == "ativo" || s == "aceite" ||
                s == "in_progress" || s == "em_curso" || s == "em curso"
            }
            TeacherStudentsFilter.TO_EVALUATE -> result.filter { student ->
                student.hasPendingEvaluation
            }
            TeacherStudentsFilter.COMPLETED -> result.filter { student ->
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
                student.offerTitle?.lowercase()?.contains(query) == true ||
                student.companyName?.lowercase()?.contains(query) == true
            }
        }

        return result
    }
}
