package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateStudentPresenceDto(
    @SerialName("student_presence_confirmed")
    val studentPresenceConfirmed: Boolean = true
)