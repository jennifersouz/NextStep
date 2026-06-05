package com.example.nextstep.data.model

data class CompanyAdvisorDto(
    val id: String,
    val profileId: String? = null,
    val name: String,
    val email: String,
    val phone: String? = null,
    val department: String? = null,
    val status: String
)