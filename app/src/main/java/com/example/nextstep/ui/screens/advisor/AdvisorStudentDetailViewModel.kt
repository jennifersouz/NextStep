package com.example.nextstep.ui.screens.advisor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.model.AdvisorEvaluationDto
import com.example.nextstep.data.repository.AdvisorEvaluationRepository
import com.example.nextstep.data.repository.AdvisorStudentDetailRepository
import com.example.nextstep.data.repository.AdvisorTasksRepository
import com.example.nextstep.data.repository.InternshipActionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdvisorStudentDetailViewModel : ViewModel() {

    private val repository = AdvisorStudentDetailRepository()
    private val tasksRepository = AdvisorTasksRepository()
    private val evaluationRepository = AdvisorEvaluationRepository()
    private val internshipActionsRepository = InternshipActionsRepository()

    private val _uiState = MutableStateFlow(AdvisorStudentDetailUiState())
    val uiState: StateFlow<AdvisorStudentDetailUiState> = _uiState.asStateFlow()

    // ── Detalhe do aluno ──────────────────────────────────────────────────────

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

            // Carregar avaliação em paralelo (não bloqueia o detalhe)
            loadEvaluation(applicationId)
        }
    }

    fun updateTaskStatus(taskId: String, status: String, applicationId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
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

    // ── Avaliação ─────────────────────────────────────────────────────────────

    fun loadEvaluation(applicationId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingEvaluation = true,
                evaluationErrorMessage = null
            )

            evaluationRepository.getEvaluation(applicationId)
                .onSuccess { evaluation ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingEvaluation = false,
                        evaluation = evaluation
                    )
                    // Pré-preencher formulário se já existir avaliação
                    if (evaluation != null) {
                        _uiState.value = _uiState.value.copy(
                            grade = evaluation.grade.let {
                                // Formatar sem decimais desnecessários (ex: 15.0 -> "15")
                                if (it == it.toLong().toDouble()) it.toLong().toString()
                                else it.toString()
                            },
                            qualitativeFeedback = evaluation.qualitativeFeedback,
                            strengths = evaluation.strengths ?: "",
                            improvements = evaluation.improvements ?: ""
                        )
                    }
                }
                .onFailure { exception ->
                    Log.e("AdvisorDetailVM", "Erro ao carregar avaliação", exception)
                    _uiState.value = _uiState.value.copy(
                        isLoadingEvaluation = false,
                        evaluationErrorMessage = "Não foi possível carregar a avaliação."
                    )
                }
        }
    }

    fun onGradeChange(value: String) {
        _uiState.value = _uiState.value.copy(
            grade = value,
            gradeError = null,
            evaluationErrorMessage = null,
            evaluationSuccessMessage = null
        )
    }

    fun onQualitativeFeedbackChange(value: String) {
        _uiState.value = _uiState.value.copy(
            qualitativeFeedback = value,
            qualitativeFeedbackError = null,
            evaluationErrorMessage = null,
            evaluationSuccessMessage = null
        )
    }

    fun onStrengthsChange(value: String) {
        _uiState.value = _uiState.value.copy(
            strengths = value,
            evaluationSuccessMessage = null
        )
    }

    fun onImprovementsChange(value: String) {
        _uiState.value = _uiState.value.copy(
            improvements = value,
            evaluationSuccessMessage = null
        )
    }

    fun saveEvaluation(applicationId: String) {
        if (!validateEvaluation()) return

        val state = _uiState.value
        val gradeValue = state.grade.trim().replace(",", ".").toDouble()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSavingEvaluation = true,
                evaluationErrorMessage = null,
                evaluationSuccessMessage = null
            )

            evaluationRepository.saveEvaluation(
                applicationId = applicationId,
                grade = gradeValue,
                qualitativeFeedback = state.qualitativeFeedback.trim(),
                strengths = state.strengths.takeIf { it.isNotBlank() }?.trim(),
                improvements = state.improvements.takeIf { it.isNotBlank() }?.trim()
            ).onSuccess { saved ->
                val btnText = if (_uiState.value.evaluation != null) "Atualizar avaliação" else "Guardar avaliação"
                _uiState.value = _uiState.value.copy(
                    isSavingEvaluation = false,
                    evaluation = saved,
                    evaluationSuccessMessage = "Avaliação guardada com sucesso."
                )
            }.onFailure { exception ->
                // Log technical error, show user-friendly message
                Log.e("AdvisorDetailVM", "Erro ao guardar avaliação", exception)
                _uiState.value = _uiState.value.copy(
                    isSavingEvaluation = false,
                    evaluationErrorMessage = "Não foi possível guardar a avaliação."
                )
            }
        }
    }

    fun clearEvaluationMessages() {
        _uiState.value = _uiState.value.copy(
            evaluationSuccessMessage = null,
            evaluationErrorMessage = null
        )
    }

    // ── Acções de estágio ─────────────────────────────────────────────────────

    fun cancelInternship(applicationId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isActingOnInternship = true,
                internshipActionSuccess = null,
                internshipActionError = null
            )
            internshipActionsRepository.cancelInternship(applicationId)
                .onSuccess {
                    Log.d("InternshipAction", "Operation successful")
                    _uiState.value = _uiState.value.copy(
                        isActingOnInternship = false,
                        internshipActionSuccess = "Estágio cancelado com sucesso."
                    )
                }
                .onFailure { exception ->
                    Log.e("AdvisorDetailVM", "Erro ao cancelar estágio", exception)
                    _uiState.value = _uiState.value.copy(
                        isActingOnInternship = false,
                        internshipActionError = exception.message ?: "Erro ao cancelar estágio."
                    )
                }
        }
    }

    fun finishInternship(applicationId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isActingOnInternship = true,
                internshipActionSuccess = null,
                internshipActionError = null
            )
            internshipActionsRepository.finishInternship(applicationId)
                .onSuccess {
                    Log.d("InternshipAction", "Operation successful")
                    _uiState.value = _uiState.value.copy(
                        isActingOnInternship = false,
                        internshipActionSuccess = "Estágio concluído com sucesso."
                    )
                }
                .onFailure { exception ->
                    Log.e("AdvisorDetailVM", "Erro ao concluir estágio", exception)
                    _uiState.value = _uiState.value.copy(
                        isActingOnInternship = false,
                        internshipActionError = exception.message ?: "Erro ao concluir estágio."
                    )
                }
        }
    }

    fun clearInternshipActionMessages() {
        _uiState.value = _uiState.value.copy(
            internshipActionSuccess = null,
            internshipActionError = null
        )
    }

    private fun validateEvaluation(): Boolean {
        val state = _uiState.value
        var hasError = false

        // Validar nota
        val trimmedGrade = state.grade.trim().replace(",", ".")
        if (trimmedGrade.isBlank()) {
            _uiState.value = _uiState.value.copy(
                gradeError = "Insere uma nota válida entre 0 e 20."
            )
            hasError = true
        } else {
            val gradeValue = trimmedGrade.toDoubleOrNull()
            if (gradeValue == null || gradeValue < 0.0 || gradeValue > 20.0) {
                _uiState.value = _uiState.value.copy(
                    gradeError = "Insere uma nota válida entre 0 e 20."
                )
                hasError = true
            } else {
                _uiState.value = _uiState.value.copy(gradeError = null)
            }
        }

        // Validar comentário qualitativo (obrigatório)
        if (state.qualitativeFeedback.isBlank()) {
            _uiState.value = _uiState.value.copy(
                qualitativeFeedbackError = "Escreve uma apreciação qualitativa."
            )
            hasError = true
        } else {
            _uiState.value = _uiState.value.copy(qualitativeFeedbackError = null)
        }

        return !hasError
    }
}
