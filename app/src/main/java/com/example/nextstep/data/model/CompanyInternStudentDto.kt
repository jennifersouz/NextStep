package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyInternStudentDto(
    @SerialName("application_id")
    val applicationId: String,

    @SerialName("student_profile_id")
    val studentProfileId: String,

    @SerialName("offer_id")
    val offerId: String? = null,

    @SerialName("student_name")
    val studentName: String? = null,

    @SerialName("student_email")
    val studentEmail: String? = null,

    @SerialName("student_phone")
    val studentPhone: String? = null,

    @SerialName("student_number")
    val studentNumber: String? = null,

    val course: String? = null,

    @SerialName("academic_year")
    val academicYear: Int? = null,

    @SerialName("education_institution")
    val educationInstitution: String? = null,

    @SerialName("offer_title")
    val offerTitle: String? = null,

    @SerialName("offer_area")
    val offerArea: String? = null,

    @SerialName("offer_location")
    val offerLocation: String? = null,

    @SerialName("internship_status")
    val internshipStatus: String? = null,

    @SerialName("application_created_at")
    val applicationCreatedAt: String? = null,

    @SerialName("status_updated_at")
    val statusUpdatedAt: String? = null,

    @SerialName("advisor_profile_id")
    val advisorProfileId: String? = null,

    @SerialName("advisor_name")
    val advisorName: String? = null,

    @SerialName("advisor_email")
    val advisorEmail: String? = null,

    @SerialName("advisor_phone")
    val advisorPhone: String? = null,

    @SerialName("advisor_assigned_at")
    val advisorAssignedAt: String? = null,

    @SerialName("has_advisor")
    val hasAdvisor: Boolean? = null
)