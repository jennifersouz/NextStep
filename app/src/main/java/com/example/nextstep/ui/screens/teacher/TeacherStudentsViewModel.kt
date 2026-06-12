package com.example.nextstep.ui.screens.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.model.TeacherEvaluationDto
import com.example.nextstep.data.model.TeacherStudentDetailNonSerializable
import com.example.nextstep.data.model.TeacherStudentDto
import com.example.nextstep.data.repository.TeacherEvaluationRepository
import com.example.nextstep.data.repository.TeacherStudentsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TeacherStudentDetailUiState(
    val isLoading: Boolean = false,
    val detail: TeacherStudentDetailNonSerializable? = null,
    val errorMessage: String? = null
)

data class TeacherEvaluationUiState(
    val isLoadingEvaluation: Boolean = false,
    val isSavingEvaluation: Boolean = false,
    val evaluation: TeacherEvaluationDto? = null,
    val grade: String = "",
    val qualitativeFeedback: String = "",
    val strengths: String = "",
    val improvements: String = "",
    val gradeError: String? = null,
    val feedbackError: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isEditing: Boolean = false
)

class TeacherStudentsViewModel : ViewModel() {

    private val repository = TeacherStudentsRepository()
    private val evaluationRepository = TeacherEvaluationRepository()

    private val _uiState = MutableStateFlow(TeacherStudentsUiState(isLoading = true))
    val uiState: StateFlow<TeacherStudentsUiState> = _uiState.asStateFlow()

    private val _detailUiState = MutableStateFlow(TeacherStudentDetailUiState())
    val detailUiState: StateFlow<TeacherStudentDetailUiState> = _detailUiState.asStateFlow()

    private val _evaluationUiState = MutableStateFlow(TeacherEvaluationUiState())
    val evaluationUiState: StateFlow<TeacherEvaluationUiState> = _evaluationUiState.asStateFlow()

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

    fun loadStudentDetail(applicationId: String) {
        viewModelScope.launch {
            _detailUiState.value = TeacherStudentDetailUiState(isLoading = true)

            repository.getStudentDetail(applicationId)
                .onSuccess { detail ->
                    _detailUiState.value = TeacherStudentDetailUiState(
                        isLoading = false,
                        detail = detail
                    )
                }
                .onFailure { exception ->
                    _detailUiState.value = TeacherStudentDetailUiState(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
        }
    }

    // ── Evaluation functions ──

    fun loadEvaluation(applicationId: String) {
        viewModelScope.launch {
            _evaluationUiState.value = _evaluationUiState.value.copy(
                isLoadingEvaluation = true,
                errorMessage = null,
                successMessage = null
            )

            evaluationRepository.getEvaluation(applicationId)
                .onSuccess { evaluation ->
                    if (evaluation != null) {
                        _evaluationUiState.value = _evaluationUiState.value.copy(
                            isLoadingEvaluation = false,
                            evaluation = evaluation,
                            grade = evaluation.grade ?: "",
                            qualitativeFeedback = evaluation.qualitativeFeedback ?: "",
                            strengths = evaluation.strengths ?: "",
                            improvements = evaluation.improvements ?: "",
                            isEditing = false
                        )
                    } else {
                        _evaluationUiState.value = _evaluationUiState.value.copy(
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
                .onFailure { exception ->
                    _evaluationUiState.value = _evaluationUiState.value.copy(
                        isLoadingEvaluation = false,
                        errorMessage = exception.message
                    )
                }
        }
    }

    fun onGradeChange(grade: String) {
        _evaluationUiState.value = _evaluationUiState.value.copy(
            grade = grade,
            gradeError = null
        )
    }

    fun onQualitativeFeedbackChange(feedback: String) {
        _evaluationUiState.value = _evaluationUiState.value.copy(
            qualitativeFeedback = feedback,
            feedbackError = null
        )
    }

    fun onStrengthsChange(strengths: String) {
        _evaluationUiState.value = _evaluationUiState.value.copy(strengths = strengths)
    }

    fun onImprovementsChange(improvements: String) {
        _evaluationUiState.value = _evaluationUiState.value.copy(improvements = improvements)
    }

    fun startEditing() {
        _evaluationUiState.value = _evaluationUiState.value.copy(isEditing = true)
    }

    fun cancelEditing() {
        val eval = _evaluationUiState.value.evaluation
        _evaluationUiState.value = _evaluationUiState.value.copy(
            isEditing = eval == null,
            grade = eval?.grade ?: "",
            qualitativeFeedback = eval?.qualitativeFeedback ?: "",
            strengths = eval?.strengths ?: "",
            improvements = eval?.improvements ?: "",
            gradeError = null,
            feedbackError = null,
            errorMessage = null,
            successMessage = null
        )
    }

    fun saveEvaluation(applicationId: String) {
        val state = _evaluationUiState.value

        // Validate
        var hasError = false
        var gradeError: String? = null
        var feedbackError: String? = null

        // Validate grade
        val gradeValue = state.grade.trim()
        if (gradeValue.isBlank()) {
            gradeError = "Insere uma nota."
            hasError = true
        } else {
            val parsedGrade = gradeValue.toDoubleOrNull()
            if (parsedGrade == null) {
                gradeError = "Insere uma nota válida entre 0 e 20."
                hasError = true
            } else if (parsedGrade < 0.0 || parsedGrade > 20.0) {
                gradeError = "Insere uma nota válida entre 0 e 20."
                hasError = true
            }
        }

        // Validate qualitative feedback
        if (state.qualitativeFeedback.isBlank()) {
            feedbackError = "Escreve uma apreciação qualitativa."
            hasError = true
        }

        if (hasError) {
            _evaluationUiState.value = state.copy(
                gradeError = gradeError,
                feedbackError = feedbackError
            )
            return
        }

        val gradeValueParsed = state.grade.trim().toDoubleOrNull() ?: return

        viewModelScope.launch {
            _evaluationUiState.value = state.copy(
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
                    _evaluationUiState.value = _evaluationUiState.value.copy(
                        isSavingEvaluation = false,
                        evaluation = savedEvaluation,
                        isEditing = false,
                        successMessage = "Avaliação guardada com sucesso.",
                        gradeError = null,
                        feedbackError = null
                    )
                }
                .onFailure { exception ->
                    _evaluationUiState.value = _evaluationUiState.value.copy(
                        isSavingEvaluation = false,
                        errorMessage = exception.message ?: "Erro ao guardar avaliação."
                    )
                }
        }
    }

    fun clearEvaluationError() {
        _evaluationUiState.value = _evaluationUiState.value.copy(errorMessage = null)
    }

    fun clearEvaluationSuccess() {
        _evaluationUiState.value = _evaluationUiState.value.copy(successMessage = null)
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

    fun clearDetailError() {
        _detailUiState.value = _detailUiState.value.copy(errorMessage = null)
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