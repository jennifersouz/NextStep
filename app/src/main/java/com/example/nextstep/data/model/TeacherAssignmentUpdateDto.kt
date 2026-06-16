package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeacherAssignmentUpdateDto(
    @SerialName("teacher_profile_id")
    val teacherProfileId: String,

    @SerialName("teacher_status")
    val teacherStatus: String
)
