package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO para ler/escrever avaliação do orientador em advisor_evaluations.
 */
@Serializable
data class AdvisorEvaluationDto(
    val id: String? = null,

    @SerialName("application_id")
    val applicationId: String,

    @SerialName("advisor_profile_id")
    val advisorProfileId: String,

    @SerialName("student_profile_id")
    val studentProfileId: String,

    val grade: Double,

    @SerialName("qualitative_feedback")
    val qualitativeFeedback: String,

    val strengths: String? = null,
    val improvements: String? = null,
    val status: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)

/**
 * DTO para inserção de nova avaliação.
 */
@Serializable
data class AdvisorEvaluationInsertDto(
    @SerialName("application_id")
    val applicationId: String,

    @SerialName("advisor_profile_id")
    val advisorProfileId: String,

    @SerialName("student_profile_id")
    val studentProfileId: String,

    val grade: Double,

    @SerialName("qualitative_feedback")
    val qualitativeFeedback: String,

    val strengths: String? = null,
    val improvements: String? = null
)

/**
 * DTO para atualização de avaliação existente.
 */
@Serializable
data class AdvisorEvaluationUpdateDto(
    val grade: Double,

    @SerialName("qualitative_feedback")
    val qualitativeFeedback: String,

    val strengths: String? = null,
    val improvements: String? = null
)
