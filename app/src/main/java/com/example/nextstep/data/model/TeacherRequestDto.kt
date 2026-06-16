package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeacherRequestDto(
    val id: String,

    @SerialName("application_id")
    val applicationId: String? = null,

    @SerialName("teacher_profile_id")
    val teacherProfileId: String? = null,

    val status: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)
