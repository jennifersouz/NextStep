package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeacherRequestCreateDto(
    @SerialName("application_id")
    val applicationId: String,

    @SerialName("teacher_profile_id")
    val teacherProfileId: String,

    val status: String = "pending"
)
