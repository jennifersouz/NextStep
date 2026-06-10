package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstitutionUserDto(
    @SerialName("invite_id")
    val inviteId: String,
    @SerialName("target_role")
    val targetRole: String,
    val email: String,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    val phone: String? = null,
    @SerialName("student_number")
    val studentNumber: String? = null,
    val course: String? = null,
    @SerialName("academic_year")
    val academicYear: Int? = null,
    val department: String? = null,
    @SerialName("accepted_at")
    val acceptedAt: String? = null,
    @SerialName("invite_status")
    val inviteStatus: String
)