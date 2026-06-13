package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.model.CompanyInternStudentDto
import com.example.nextstep.data.repository.CompanyInternStudentsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyInternStudentsViewModel : ViewModel() {

    private val repository = CompanyInternStudentsRepository()

    private val _uiState = MutableStateFlow(CompanyInternStudentsUiState())
    val uiState: StateFlow<CompanyInternStudentsUiState> = _uiState.asStateFlow()

    init {
        loadStudents()
    }

    fun loadStudents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = repository.getInternStudents()

            _uiState.value = if (result.isSuccess) {
                val students = result.getOrDefault(emptyList())
                _uiState.value.copy(
                    students = students,
                    filteredStudents = applyFilters(
                        students,
                        _uiState.value.searchQuery,
                        _uiState.value.selectedFilter
                    ),
                    isLoading = false,
                    errorMessage = null
                )
            } else {
                _uiState.value.copy(
                    students = emptyList(),
                    filteredStudents = emptyList(),
                    isLoading = false,
                    errorMessage = "Não foi possível carregar os alunos em estágio."
                )
            }
        }
    }

    fun refresh() {
        loadStudents()
    }

    fun onSearchChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun onFilterChange(filter: CompanyInternStudentsFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        _uiState.value = state.copy(
            filteredStudents = applyFilters(
                state.students,
                state.searchQuery,
                state.selectedFilter
            )
        )
    }

    private fun applyFilters(
        students: List<CompanyInternStudentDto>,
        searchQuery: String,
        filter: CompanyInternStudentsFilter
    ): List<CompanyInternStudentDto> {
        var result = students

        // Filter by status
        result = when (filter) {
            CompanyInternStudentsFilter.ACTIVE -> result.filter {
                val status = it.internshipStatus?.trim()?.lowercase()
                status in setOf("accepted", "active", "in_progress")
            }
            CompanyInternStudentsFilter.INACTIVE -> result.filter {
                val status = it.internshipStatus?.trim()?.lowercase()
                status == "inactive"
            }
            CompanyInternStudentsFilter.COMPLETED -> result.filter {
                val status = it.internshipStatus?.trim()?.lowercase()
                status == "completed"
            }
            CompanyInternStudentsFilter.ALL -> result
        }

        // Filter by search query
        if (searchQuery.isNotBlank()) {
            val query = searchQuery.lowercase().trim()
            result = result.filter { student ->
                student.studentName?.lowercase()?.contains(query) == true ||
                        student.studentEmail?.lowercase()?.contains(query) == true ||
                        student.course?.lowercase()?.contains(query) == true ||
                        student.offerTitle?.lowercase()?.contains(query) == true
            }
        }

        return result
    }

    companion object {
        fun translateStatus(status: String?): String {
            return when (status?.trim()?.lowercase()) {
                "accepted", "active" -> "Ativo"
                "in_progress" -> "Em progresso"
                "completed" -> "Concluído"
                "inactive" -> "Inativo"
                "pending" -> "Pendente"
                "rejected" -> "Recusada"
                "cancelled" -> "Cancelado"
                else -> status ?: "Desconhecido"
            }
        }
    }
}
