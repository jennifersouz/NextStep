package com.example.nextstep.ui.screens.advisor

import com.example.nextstep.data.model.AdvisorEvaluationDto
import com.example.nextstep.data.model.AdvisorStudentDetailDto

data class AdvisorStudentDetailUiState(
    // ── Detalhe do aluno ──────────────────────────────
    val isLoading: Boolean = false,
    val detail: AdvisorStudentDetailDto? = null,
    val errorMessage: String? = null,

    // ── Avaliação ─────────────────────────────────────
    val isLoadingEvaluation: Boolean = false,
    val isSavingEvaluation: Boolean = false,
    val evaluation: AdvisorEvaluationDto? = null,

    // Campos do formulário
    val grade: String = "",
    val qualitativeFeedback: String = "",
    val strengths: String = "",
    val improvements: String = "",

    // Erros por campo
    val gradeError: String? = null,
    val qualitativeFeedbackError: String? = null,

    // Feedback geral
    val evaluationSuccessMessage: String? = null,
    val evaluationErrorMessage: String? = null,

    // ── Acções de estágio ──────────────────────────────
    val isActingOnInternship: Boolean = false,
    val internshipActionSuccess: String? = null,
    val internshipActionError: String? = null
)
