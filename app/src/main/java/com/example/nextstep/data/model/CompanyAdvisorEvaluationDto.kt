package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyAdvisorEvaluationDto(
    val id: String? = null,

    @SerialName("application_id")
    val applicationId: String? = null,

    @SerialName("advisor_profile_id")
    val advisorProfileId: String? = null,

    @SerialName("student_profile_id")
    val studentProfileId: String? = null,

    val grade: Double? = null,

    @SerialName("qualitative_feedback")
    val qualitativeFeedback: String? = null,

    val strengths: String? = null,
    val improvements: String? = null,
    val status: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)