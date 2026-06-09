package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeacherDto(
    @SerialName("profile_id")
    val profileId: String,
    @SerialName("institution_profile_id")
    val institutionProfileId: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val email: String,
    val phone: String? = null,
    val department: String? = null
)
