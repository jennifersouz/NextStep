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

    @SerialName("institution_name")
    val institutionName: String? = null,

    @SerialName("company_advisor_grade")
    val companyAdvisorGrade: String? = null,

    @SerialName("academic_advisor_grade")
    val academicAdvisorGrade: String? = null
) {
    val studentFullName: String
        get() = "$firstName $lastName"
}
