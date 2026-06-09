package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstitutionInviteInsertDto(
    @SerialName("institution_profile_id")
    val institutionProfileId: String,
    @SerialName("target_role")
    val targetRole: String,
    val email: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("student_number")
    val studentNumber: String? = null,
    val course: String? = null,
    @SerialName("academic_year")
    val academicYear: Int? = null,
    val department: String? = null,
    val phone: String? = null
)
