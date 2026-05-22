package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentDto(
    @SerialName("profile_id")
    val profileId: String,

    @SerialName("first_name")
    val firstName: String,

    @SerialName("last_name")
    val lastName: String,

    @SerialName("student_number")
    val studentNumber: String,

    val course: String,

    @SerialName("academic_year")
    val academicYear: Int
)