package com.example.nextstep.data.model

data class StudentProfile(
    val profileId: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val studentNumber: String,
    val course: String,
    val academicYear: Int,
    val educationInstitution: String
) {
    val fullName: String
        get() = "$firstName $lastName"
}