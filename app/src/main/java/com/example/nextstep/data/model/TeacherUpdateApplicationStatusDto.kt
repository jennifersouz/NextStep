package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeacherUpdateApplicationStatusDto(
    @SerialName("teacher_status")
    val teacherStatus: String
)
