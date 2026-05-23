package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyInternshipDto(
    val id: String,

    @SerialName("company_profile_id")
    val companyProfileId: String,

    val title: String,

    @SerialName("student_name")
    val studentName: String,

    val status: String
)