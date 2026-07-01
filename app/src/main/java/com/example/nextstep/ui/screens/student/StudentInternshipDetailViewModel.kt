package com.example.nextstep.ui.screens.student

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.AdvisorTasksRepository
import com.example.nextstep.data.repository.ApplicationsRepository
import com.example.nextstep.data.repository.CompanyEvaluationRepository
import com.example.nextstep.data.repository.StudentApplicationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentInternshipDetailViewModel : ViewModel() {

    private val applicationRepository = StudentApplicationsRepository()
    private val applicationsRepository = ApplicationsRepository()
    private val tasksRepository = AdvisorTasksRepository()
    private val evaluationRepository = CompanyEvaluationRepository()

    private val _uiState = MutableStateFlow(StudentInternshipDetailUiState())
    val uiState: StateFlow<StudentInternshipDetailUiState> = _uiState.asStateFlow()

    private var currentInternshipId: String = ""

    private fun extractFileNameFromPath(path: String): String {
        return path.substringAfterLast("/")
            .substringAfter("_", path.substringAfterLast("/"))
    }

    private fun updateReportFileNameFromPath(path: String?) {
        val name = if (path != null) {
            val raw = extractFileNameFromPath(path)
            val underscoreIndex = raw.indexOf("_")
            if (underscoreIndex >= 0 && underscoreIndex < raw.length - 1) {
                raw.substring(underscoreIndex + 1)
            } else {
                raw
            }
        } else null
        _uiState.value = _uiState.value.copy(reportFileName = name)
    }

    fun loadDetail(internshipId: String) {
        currentInternshipId = internshipId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessageRes = null)

            val applicationResult = applicationRepository.getSubmittedApplicationById(internshipId)
            
            if (applicationResult.isSuccess) {
                val application = applicationResult.getOrNull()
                val tasksResult = tasksRepository.getTasksByApplication(internshipId)
                val tasks = tasksResult.getOrDefault(emptyList())

                Log.d("TeacherAssignment", "=== DADOS DOCENTE NO VIEWMODEL ===")
                Log.d("TeacherAssignment", "teacherProfileId=${application?.teacherProfileId}")
                Log.d("TeacherAssignment", "teacherStatus=${application?.teacherStatus}")
                Log.d("TeacherAssignment", "teacherName=${application?.teacherName}")
                Log.d("TeacherAssignment", "institutionName=${application?.institutionName}")

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    internship = application,
                    tasks = tasks
                )
                updateReportFileNameFromPath(application?.reportPath)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.internship_detail_load_error
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
        _uiState.value = _uiState.value.copy(showAddTaskDialog = false, taskErrorRes = null)
    }

    fun updateTaskTitle(title: String) {
        _uiState.value = _uiState.value.copy(taskTitle = title)
    }

    fun createTask() {
        val state = _uiState.value
        if (state.taskTitle.isBlank()) {
            _uiState.value = state.copy(taskErrorRes = R.string.error_task_title_required)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingTask = true, taskErrorRes = null)

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
                    taskErrorRes = R.string.error_saving_task
                )
            }
        }
    }

    fun uploadReport(context: Context, uri: Uri, fileName: String) {
        val application = _uiState.value.internship ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUploadingReport = true,
                reportErrorMessage = null,
                reportSuccessMessage = null
            )

            val reportBytes = context.contentResolver.openInputStream(uri)?.use {
                it.readBytes()
            }

            if (reportBytes == null) {
                _uiState.value = _uiState.value.copy(
                    isUploadingReport = false,
                    reportErrorMessage = "Não foi possível ler o ficheiro."
                )
                return@launch
            }

            val result = applicationsRepository.uploadReport(
                application = application,
                reportFileName = fileName,
                reportBytes = reportBytes
            )

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isUploadingReport = false,
                    reportFileName = fileName,
                    reportSuccessMessage = "Relatório submetido com sucesso."
                )
                loadDetail(currentInternshipId)
            } else {
                _uiState.value = _uiState.value.copy(
                    isUploadingReport = false,
                    reportErrorMessage = "Não foi possível anexar o relatório."
                )
            }
        }
    }

    fun clearReportMessages() {
        _uiState.value = _uiState.value.copy(
            reportErrorMessage = null,
            reportSuccessMessage = null
        )
    }

    fun updateTaskStatus(taskId: String, newStatus: String) {
        viewModelScope.launch {
            val result = tasksRepository.updateTaskStatus(taskId, newStatus)
            if (result.isSuccess) {
                Log.d("TaskDebug", "Tarefa atualizada com sucesso")
                val tasksResult = tasksRepository.getTasksByApplication(currentInternshipId)
                _uiState.value = _uiState.value.copy(
                    tasks = tasksResult.getOrDefault(emptyList())
                )
            } else {
                Log.e("TaskDebug", "Erro ao atualizar tarefa", result.exceptionOrNull())
            }
        }
    }
}
