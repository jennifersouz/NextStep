package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentSubmittedApplicationDto(
    @SerialName("id")
    val id: String,

    @SerialName("offer_id")
    val offerId: String,

    @SerialName("student_profile_id")
    val studentProfileId: String,

    @SerialName("company_profile_id")
    val companyProfileId: String,

    val status: String,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("motivation_letter_path")
    val motivationLetterPath: String? = null,

    @SerialName("cv_path")
    val cvPath: String? = null,

    @SerialName("report_path")
    val reportPath: String? = null,

    @SerialName("student_presence_confirmed")
    val studentPresenceConfirmed: Boolean = false,

    @SerialName("student_presence_confirmed_at")
    val studentPresenceConfirmedAt: String? = null,

    @SerialName("offer_title")
    val offerTitle: String,

    @SerialName("company_name")
    val companyName: String,

    @SerialName("first_name")
    val firstName: String,

    @SerialName("last_name")
    val lastName: String,

    @SerialName("advisor_profile_id")
    val advisorProfileId: String? = null,

    @SerialName("advisor_name")
    val advisorName: String? = null,

    @SerialName("advisor_first_name")
    val advisorFirstName: String? = null,

    @SerialName("advisor_last_name")
    val advisorLastName: String? = null,

    @SerialName("advisor_email")
    val advisorEmail: String? = null,

    @SerialName("advisor_phone")
    val advisorPhone: String? = null,

    @SerialName("advisor_department")
    val advisorDepartment: String? = null,

    @SerialName("teacher_profile_id")
    val teacherProfileId: String? = null,

    @SerialName("teacher_status")
    val teacherStatus: String? = null,

    @SerialName("teacher_name")
    val teacherName: String? = null,

    @SerialName("teacher_email")
    val teacherEmail: String? = null,

    @SerialName("teacher_department")
    val teacherDepartment: String? = null,

    @SerialName("teacher_first_name")
    val teacherFirstName: String? = null,

    @SerialName("teacher_last_name")
    val teacherLastName: String? = null,

    @SerialName("institution_name")
    val institutionName: String? = null,

    @SerialName("company_advisor_grade")
    val companyAdvisorGrade: String? = null,

    @SerialName("academic_advisor_grade")
    val academicAdvisorGrade: String? = null
) {
    val studentFullName: String
        get() = "$firstName $lastName"

    val formattedAdvisorName: String
        get() = listOfNotNull(advisorFirstName, advisorLastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .takeIf { it.isNotBlank() }
            ?: advisorName.takeIf { !it.isNullOrBlank() }
            ?: "Orientador"

    val formattedTeacherName: String
        get() = listOfNotNull(teacherFirstName, teacherLastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .takeIf { it.isNotBlank() }
            ?: teacherName.takeIf { !it.isNullOrBlank() }
            ?: "Docente"
}