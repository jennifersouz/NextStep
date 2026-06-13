package com.example.nextstep.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.repository.AdvisorTasksRepository
import com.example.nextstep.data.repository.StudentApplicationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentInternshipDetailViewModel : ViewModel() {

    private val applicationRepository = StudentApplicationsRepository()
    private val tasksRepository = AdvisorTasksRepository()

    private val _uiState = MutableStateFlow(StudentInternshipDetailUiState())
    val uiState: StateFlow<StudentInternshipDetailUiState> = _uiState.asStateFlow()

    fun loadDetail(internshipId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val applicationResult = applicationRepository.getSubmittedApplicationById(internshipId)
            
            if (applicationResult.isSuccess) {
                val application = applicationResult.getOrNull()
                val tasksResult = tasksRepository.getTasksByApplication(internshipId)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    internship = application,
                    tasks = tasksResult.getOrDefault(emptyList())
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar detalhes do estágio."
                )
            }
        }
    }
}
