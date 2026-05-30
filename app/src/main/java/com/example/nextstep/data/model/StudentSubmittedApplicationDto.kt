package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentSubmittedApplicationDto(
    val id: String,

    @SerialName("offer_id")
    val offerId: String,

    @SerialName("student_profile_id")
    val studentProfileId: String,

    @SerialName("company_profile_id")
    val companyProfileId: String,

    val status: String,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("offer_title")
    val offerTitle: String,

    @SerialName("company_name")
    val companyName: String
)