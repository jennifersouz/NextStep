package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateStudentAdvisorRequestDto(
    @SerialName("student_profile_id")
    val studentProfileId: String,

    @SerialName("advisor_profile_id")
    val advisorProfileId: String,

    val status: String = "pending"
)