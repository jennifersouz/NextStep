package com.example.nextstep.ui.screens.teacher

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.model.ApplicationTaskDto
import com.example.nextstep.data.model.TeacherEvaluationDto
import com.example.nextstep.data.model.TeacherStudentDetailNonSerializable
import com.example.nextstep.data.repository.InternshipActionsRepository
import com.example.nextstep.data.repository.TeacherEvaluationRepository
import com.example.nextstep.data.repository.TeacherRequestsRepository
import com.example.nextstep.data.repository.TeacherStudentsRepository
import com.example.nextstep.data.repository.TeacherTasksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TeacherStudentDetailState(
    val isLoading: Boolean = false,
    val detail: TeacherStudentDetailNonSerializable? = null,
    val errorMessage: String? = null,
    val isActingOnInternship: Boolean = false,
    val internshipActionSuccess: String? = null,
    val internshipActionError: String? = null
)

data class TeacherTasksState(
    val isLoadingTasks: Boolean = false,
    val tasks: List<ApplicationTaskDto> = emptyList(),
    val errorMessage: String? = null,
    val completedTasksCount: Int = 0,
    val pendingTasksCount: Int = 0,
    val inProgressTasksCount: Int = 0,
    val progressPercentage: Int = 0
)

data class TeacherEvaluationState(
    val isLoadingEvaluation: Boolean = false,
    val isSavingEvaluation: Boolean = false,
    val evaluation: TeacherEvaluationDto? = null,
    val grade: String = "",
    val qualitativeFeedback: String = "",
    val strengths: String = "",
    val improvements: String = "",
    val gradeError: String? = null,
    @androidx.annotation.StringRes val gradeErrorRes: Int? = null,
    val feedbackError: String? = null,
    @androidx.annotation.StringRes val feedbackErrorRes: Int? = null,
    val errorMessage: String? = null,
    @androidx.annotation.StringRes val errorMessageRes: Int? = null,
    val successMessage: String? = null,
    @androidx.annotation.StringRes val successMessageRes: Int? = null,
    val isEditing: Boolean = false
)

class TeacherStudentDetailViewModel : ViewModel() {

    private val repository = TeacherStudentsRepository()
    private val evaluationRepository = TeacherEvaluationRepository()
    private val tasksRepository = TeacherTasksRepository()
    private val requestsRepository = TeacherRequestsRepository()
    private val internshipActionsRepository = InternshipActionsRepository()

    private val _detailState = MutableStateFlow(TeacherStudentDetailState())
    val detailState: StateFlow<TeacherStudentDetailState> = _detailState.asStateFlow()

    private val _tasksState = MutableStateFlow(TeacherTasksState())
    val tasksState: StateFlow<TeacherTasksState> = _tasksState.asStateFlow()

    private val _evaluationState = MutableStateFlow(TeacherEvaluationState())
    val evaluationState: StateFlow<TeacherEvaluationState> = _evaluationState.asStateFlow()

    fun loadStudentDetail(applicationId: String) {
        viewModelScope.launch {
            _detailState.value = TeacherStudentDetailState(isLoading = true)
            
            launch { loadTasks(applicationId) }
            launch { loadEvaluation(applicationId) }

            repository.getStudentDetail(applicationId)
                .onSuccess { detail ->
                    _detailState.value = TeacherStudentDetailState(
                        isLoading = false,
                        detail = detail
                    )
                }
                .onFailure { exception ->
                    _detailState.value = TeacherStudentDetailState(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
        }
    }

    private suspend fun loadTasks(applicationId: String) {
        _tasksState.value = _tasksState.value.copy(isLoadingTasks = true, errorMessage = null)
        
        val tasks = tasksRepository.getTasksByApplication(applicationId)
        
        val completed = tasks.count { it.status.lowercase() in listOf("completed", "concluida", "concluída") }
        val pending = tasks.count { it.status.lowercase() in listOf("pending", "pendente") }
        val inProgress = tasks.count { it.status.lowercase() in listOf("in_progress", "em_progresso") }
        val percentage = if (tasks.isNotEmpty()) (completed * 100) / tasks.size else 0
        
        _tasksState.value = TeacherTasksState(
            isLoadingTasks = false,
            tasks = tasks,
            completedTasksCount = completed,
            pendingTasksCount = pending,
            inProgressTasksCount = inProgress,
            progressPercentage = percentage
        )
    }

    fun loadEvaluation(applicationId: String) {
        viewModelScope.launch {
            _evaluationState.value = _evaluationState.value.copy(
                isLoadingEvaluation = true,
                errorMessage = null,
                successMessage = null
            )

            evaluationRepository.getEvaluation(applicationId)
                .onSuccess { evaluation ->
                    if (evaluation != null) {
                        _evaluationState.value = _evaluationState.value.copy(
                            isLoadingEvaluation = false,
                            evaluation = evaluation,
                            grade = evaluation.grade?.toString() ?: "",
                            qualitativeFeedback = evaluation.qualitativeFeedback ?: "",
                            strengths = evaluation.strengths ?: "",
                            improvements = evaluation.improvements ?: "",
                            isEditing = false
                        )
                    } else {
                        _evaluationState.value = _evaluationState.value.copy(
                            isLoadingEvaluation = false,
                            evaluation = null,
                            grade = "",
                            qualitativeFeedback = "",
                            strengths = "",
                            improvements = "",
                            isEditing = true
                        )
                    }
                }
                .onFailure {
                    _evaluationState.value = _evaluationState.value.copy(
                        isLoadingEvaluation = false,
                        errorMessageRes = R.string.evaluation_load_error
                    )
                }
        }
    }

    fun onGradeChange(grade: String) {
        _evaluationState.value = _evaluationState.value.copy(
            grade = grade,
            gradeErrorRes = null
        )
    }

    fun onQualitativeFeedbackChange(feedback: String) {
        _evaluationState.value = _evaluationState.value.copy(
            qualitativeFeedback = feedback,
            feedbackErrorRes = null
        )
    }

    fun onStrengthsChange(strengths: String) {
        _evaluationState.value = _evaluationState.value.copy(strengths = strengths)
    }

    fun onImprovementsChange(improvements: String) {
        _evaluationState.value = _evaluationState.value.copy(improvements = improvements)
    }

    fun startEditing() {
        _evaluationState.value = _evaluationState.value.copy(isEditing = true)
    }

    fun cancelEditing() {
        val eval = _evaluationState.value.evaluation
        _evaluationState.value = _evaluationState.value.copy(
            isEditing = eval == null,
            grade = eval?.grade?.toString() ?: "",
            qualitativeFeedback = eval?.qualitativeFeedback ?: "",
            strengths = eval?.strengths ?: "",
            improvements = eval?.improvements ?: "",
            gradeErrorRes = null,
            feedbackErrorRes = null,
            errorMessage = null,
            successMessage = null
        )
    }

    fun saveEvaluation(applicationId: String) {
        val state = _evaluationState.value

        var hasError = false
        var gradeErrorRes: Int? = null
        var feedbackErrorRes: Int? = null

        val gradeValue = state.grade.trim()
        if (gradeValue.isBlank()) {
            gradeErrorRes = R.string.evaluation_error_grade_required
            hasError = true
        } else {
            val parsedGrade = gradeValue.replace(",", ".").toDoubleOrNull()
            if (parsedGrade == null) {
                gradeErrorRes = R.string.evaluation_error_grade_invalid
                hasError = true
            } else if (parsedGrade < 0.0 || parsedGrade > 20.0) {
                gradeErrorRes = R.string.evaluation_error_grade_invalid
                hasError = true
            }
        }

        if (state.qualitativeFeedback.isBlank()) {
            feedbackErrorRes = R.string.evaluation_error_feedback_required
            hasError = true
        }

        if (hasError) {
            _evaluationState.value = state.copy(
                gradeErrorRes = gradeErrorRes,
                feedbackErrorRes = feedbackErrorRes
            )
            return
        }

        val gradeValueParsed = state.grade.trim().replace(",", ".").toDoubleOrNull() ?: return

        viewModelScope.launch {
            _evaluationState.value = state.copy(
                isSavingEvaluation = true,
                errorMessage = null,
                successMessage = null
            )

            evaluationRepository.saveEvaluation(
                applicationId = applicationId,
                grade = gradeValueParsed,
                qualitativeFeedback = state.qualitativeFeedback.trim(),
                strengths = state.strengths.trim().ifBlank { null },
                improvements = state.improvements.trim().ifBlank { null }
            )
                .onSuccess { savedEvaluation ->
                    _evaluationState.value = _evaluationState.value.copy(
                        isSavingEvaluation = false,
                        evaluation = savedEvaluation,
                        isEditing = false,
                        successMessageRes = R.string.evaluation_saved,
                        gradeErrorRes = null,
                        feedbackErrorRes = null
                    )
                }
                .onFailure { exception ->
                    _evaluationState.value = _evaluationState.value.copy(
                        isSavingEvaluation = false,
                        errorMessageRes = R.string.evaluation_save_error
                    )
                }
        }
    }

    // ── Acções de estágio ─────────────────────────────────────────────────────

    fun cancelInternship(applicationId: String) {
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(
                isActingOnInternship = true,
                internshipActionSuccess = null,
                internshipActionError = null
            )
            internshipActionsRepository.cancelInternship(applicationId)
                .onSuccess {
                    Log.d("InternshipAction", "Operation successful")
                    _detailState.value = _detailState.value.copy(
                        isActingOnInternship = false,
                        internshipActionSuccess = "Estágio cancelado com sucesso."
                    )
                }
                .onFailure { exception ->
                    Log.e("TeacherDetailVM", "Erro ao cancelar estágio", exception)
                    _detailState.value = _detailState.value.copy(
                        isActingOnInternship = false,
                        internshipActionError = exception.message ?: "Erro ao cancelar estágio."
                    )
                }
        }
    }

    fun finishInternship(applicationId: String) {
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(
                isActingOnInternship = true,
                internshipActionSuccess = null,
                internshipActionError = null
            )
            internshipActionsRepository.finishInternship(applicationId)
                .onSuccess {
                    Log.d("InternshipAction", "Operation successful")
                    _detailState.value = _detailState.value.copy(
                        isActingOnInternship = false,
                        internshipActionSuccess = "Estágio concluído com sucesso."
                    )
                }
                .onFailure { exception ->
                    Log.e("TeacherDetailVM", "Erro ao concluir estágio", exception)
                    _detailState.value = _detailState.value.copy(
                        isActingOnInternship = false,
                        internshipActionError = exception.message ?: "Erro ao concluir estágio."
                    )
                }
        }
    }

    fun clearInternshipActionMessages() {
        _detailState.value = _detailState.value.copy(
            internshipActionSuccess = null,
            internshipActionError = null
        )
    }

    fun openDocument(path: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            Log.d("TeacherStudentDetailVM", "Opening document with exact path=$path")
            requestsRepository.getDocumentUrl(path)
                .onSuccess { url ->
                    onSuccess(url)
                }
                .onFailure { exception ->
                    val msg = exception.message ?: "Erro ao abrir documento."
                    Log.e("TeacherStudentDetailVM", "Error opening document: $msg")
                    onError(msg)
                }
        }
    }
}
