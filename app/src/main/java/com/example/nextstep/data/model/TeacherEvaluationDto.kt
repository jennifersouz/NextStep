package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for reading an evaluation from the teacher_evaluations table.
 * Fields use @SerialName to map to snake_case columns in Supabase.
 */
@Serializable
data class TeacherEvaluationDto(
    @SerialName("id")
    val id: String? = null,

    @SerialName("application_id")
    val applicationId: String? = null,

    @SerialName("teacher_profile_id")
    val teacherProfileId: String? = null,

    @SerialName("student_profile_id")
    val studentProfileId: String? = null,

    @SerialName("grade")
    val grade: String? = null,

    @SerialName("qualitative_feedback")
    val qualitativeFeedback: String? = null,

    @SerialName("strengths")
    val strengths: String? = null,

    @SerialName("improvements")
    val improvements: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)

/**
 * DTO for inserting a new evaluation into the teacher_evaluations table.
 */
@Serializable
data class TeacherEvaluationInsertDto(
    @SerialName("application_id")
    val applicationId: String,

    @SerialName("teacher_profile_id")
    val teacherProfileId: String,

    @SerialName("student_profile_id")
    val studentProfileId: String,

    @SerialName("grade")
    val grade: Double,

    @SerialName("qualitative_feedback")
    val qualitativeFeedback: String,

    @SerialName("strengths")
    val strengths: String? = null,

    @SerialName("improvements")
    val improvements: String? = null
)

/**
 * Non-serializable UI-friendly representation of an evaluation.
 */
data class TeacherEvaluationUi(
    val id: String? = null,
    val grade: String? = null,
    val qualitativeFeedback: String? = null,
    val strengths: String? = null,
    val improvements: String? = null
)