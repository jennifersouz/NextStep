package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstitutionUserDetailDto(
    @SerialName("profile_id")
    val profileId: String? = null,

    val email: String = "",

    @SerialName("first_name")
    val firstName: String? = null,

    @SerialName("last_name")
    val lastName: String? = null,

    val phone: String? = null,

    @SerialName("target_role")
    val targetRole: String = "",

    @SerialName("invite_status")
    val inviteStatus: String? = null,

    @SerialName("accepted_at")
    val acceptedAt: String? = null,

    @SerialName("is_active")
    val isActive: Boolean = true,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("institution_archived_at")
    val institutionArchivedAt: String? = null,

    @SerialName("student_number")
    val studentNumber: String? = null,

    val course: String? = null,

    @SerialName("academic_year")
    val academicYear: Int? = null,

    @SerialName("education_institution")
    val educationInstitution: String? = null,

    val department: String? = null
)
