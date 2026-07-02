package com.example.nextstep.data.model

data class CompanyEmployeeInviteDisplayDto(
    val id: String,
    val profileId: String? = null,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val department: String? = null,
    val status: String,
    val acceptedAt: String? = null
) {
    val displayName: String
        get() {
            val fullName = "${firstName.orEmpty()} ${lastName.orEmpty()}".trim()
            return if (fullName.isNotBlank()) fullName else email
        }
}
