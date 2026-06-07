package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyStudentProfileDto(
    @SerialName("application_id")
    val applicationId: String,

    @SerialName("company_profile_id")
    val companyProfileId: String,

    @SerialName("student_profile_id")
    val studentProfileId: String,

    @SerialName("application_status")
    val applicationStatus: String? = null,

    @SerialName("application_created_at")
    val applicationCreatedAt: String? = null,

    @SerialName("student_email")
    val studentEmail: String? = null,

    @SerialName("first_name")
    val firstName: String? = null,

    @SerialName("last_name")
    val lastName: String? = null,

    @SerialName("student_number")
    val studentNumber: String? = null,

    val course: String? = null,

    @SerialName("academic_year")
    val academicYear: Int? = null,

    @SerialName("education_institution")
    val educationInstitution: String? = null,

    @SerialName("offer_title")
    val offerTitle: String? = null
)