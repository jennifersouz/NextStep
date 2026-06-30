package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyEmployeeInviteInsertDto(
    @SerialName("company_profile_id")
    val companyProfileId: String,
    val email: String
)
