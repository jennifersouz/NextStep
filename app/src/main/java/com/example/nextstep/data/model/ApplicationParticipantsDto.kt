package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationParticipantsDto(
    val id: String,

    @SerialName("student_profile_id")
    val studentProfileId: String,

    @SerialName("company_profile_id")
    val companyProfileId: String
)