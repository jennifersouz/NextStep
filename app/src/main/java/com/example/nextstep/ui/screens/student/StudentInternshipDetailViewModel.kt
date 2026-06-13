package com.example.nextstep.ui.screens.student

import android.util.Log
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

    private var currentInternshipId: String = ""

    fun loadDetail(internshipId: String) {
        currentInternshipId = internshipId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val applicationResult = applicationRepository.getSubmittedApplicationById(internshipId)
            
            if (applicationResult.isSuccess) {
                val application = applicationResult.getOrNull()
                val tasksResult = tasksRepository.getTasksByApplication(internshipId)
                val tasks = tasksResult.getOrDefault(emptyList())

                Log.d("TasksDebug", "Tarefas recebidas no ViewModel: ${tasks.size}")

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    internship = application,
                    tasks = tasks
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar detalhes do estágio."
                )
            }
        }
    }

    fun showAddTaskDialog() {
        _uiState.value = _uiState.value.copy(
            showAddTaskDialog = true,
            taskTitle = "",
            taskError = null
        )
    }

    fun hideAddTaskDialog() {
        _uiState.value = _uiState.value.copy(showAddTaskDialog = false, taskError = null)
    }

    fun updateTaskTitle(title: String) {
        _uiState.value = _uiState.value.copy(taskTitle = title)
    }

    fun createTask() {
        val state = _uiState.value
        if (state.taskTitle.isBlank()) {
            _uiState.value = state.copy(taskError = "O título é obrigatório.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingTask = true, taskError = null)

            val result = tasksRepository.createTask(
                applicationId = currentInternshipId,
                title = state.taskTitle.trim()
            )

            if (result.isSuccess) {
                val tasksResult = tasksRepository.getTasksByApplication(currentInternshipId)
                _uiState.value = _uiState.value.copy(
                    isSavingTask = false,
                    showAddTaskDialog = false,
                    tasks = tasksResult.getOrDefault(emptyList())
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isSavingTask = false,
                    taskError = "Erro ao guardar tarefa."
                )
            }
        }
    }

    fun updateTaskStatus(taskId: String, newStatus: String) {
        viewModelScope.launch {
            val result = tasksRepository.updateTaskStatus(taskId, newStatus)
            if (result.isSuccess) {
                val tasksResult = tasksRepository.getTasksByApplication(currentInternshipId)
                _uiState.value = _uiState.value.copy(
                    tasks = tasksResult.getOrDefault(emptyList())
                )
            }
        }
    }
}
