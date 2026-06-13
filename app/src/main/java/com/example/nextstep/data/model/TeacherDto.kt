package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeacherDto(
    @SerialName("profile_id")
    val profileId: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("institution_profile_id")
    val institutionProfileId: String? = null,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val department: String? = null,
    val name: String? = null
) {
    val displayFullName: String
        get() = when {
            !name.isNullOrBlank() -> name
            !firstName.isNullOrBlank() || !lastName.isNullOrBlank() -> 
                listOfNotNull(firstName, lastName).joinToString(" ").trim()
            else -> email ?: "Docente"
        }

    val safeProfileId: String
        get() = profileId ?: id ?: ""
}
