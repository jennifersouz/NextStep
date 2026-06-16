package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyEvaluationDto(
    val id: String? = null,

    @SerialName("application_id")
    val applicationId: String? = null,

    @SerialName("company_profile_id")
    val companyProfileId: String? = null,

    @SerialName("student_profile_id")
    val studentProfileId: String? = null,

    val grade: Double? = null,

    @SerialName("qualitative_feedback")
    val qualitativeFeedback: String? = null,

    val strengths: String? = null,
    val improvements: String? = null,
    val recommendation: String? = null,
    val status: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class CompanyEvaluationUpsertDto(
    @SerialName("application_id")
    val applicationId: String,

    @SerialName("company_profile_id")
    val companyProfileId: String,

    @SerialName("student_profile_id")
    val studentProfileId: String,

    val grade: Double,

    @SerialName("qualitative_feedback")
    val qualitativeFeedback: String,

    val strengths: String? = null,
    val improvements: String? = null,
    val recommendation: String? = null,
    val status: String = "submitted"
)
