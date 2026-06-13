package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstitutionUserDto(
    @SerialName("invite_id")
    val inviteId: String,

    @SerialName("profile_id")
    val profileId: String? = null,

    val email: String,

    @SerialName("first_name")
    val firstName: String? = null,

    @SerialName("last_name")
    val lastName: String? = null,

    @SerialName("target_role")
    val targetRole: String,

    @SerialName("accepted_at")
    val acceptedAt: String? = null,

    @SerialName("invite_status")
    val inviteStatus: String? = null,

    @SerialName("student_profile_id")
    val studentProfileId: String? = null,

    @SerialName("teacher_profile_id")
    val teacherProfileId: String? = null,

    @SerialName("student_number")
    val studentNumber: String? = null,

    val course: String? = null,

    @SerialName("academic_year")
    val academicYear: Int? = null,

    val department: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("student_institution_archived_at")
    val studentInstitutionArchivedAt: String? = null,

    @SerialName("teacher_institution_archived_at")
    val teacherInstitutionArchivedAt: String? = null
)
