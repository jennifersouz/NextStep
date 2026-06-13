package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeacherProfileDto(
    @SerialName("profile_id")
    val profileId: String,

    @SerialName("first_name")
    val firstName: String? = null,

    @SerialName("last_name")
    val lastName: String? = null,

    val email: String? = null,

    val phone: String? = null,

    val department: String? = null,

    val institution: String? = null
) {
    val displayName: String
        get() {
            val full = listOfNotNull(firstName, lastName)
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .joinToString(" ")
            return full.ifBlank { "" }
        }
}

@Serializable
data class UpdateTeacherProfileDto(
    @SerialName("first_name")
    val firstName: String? = null,

    @SerialName("last_name")
    val lastName: String? = null,

    @SerialName("phone")
    val phone: String? = null,

    @SerialName("department")
    val department: String? = null
)