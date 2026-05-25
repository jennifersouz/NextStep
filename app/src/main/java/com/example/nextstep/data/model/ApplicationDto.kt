package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationDto(
    val id: String,

    @SerialName("offer_id")
    val offerId: String,

    @SerialName("student_profile_id")
    val studentProfileId: String,

    @SerialName("company_profile_id")
    val companyProfileId: String,

    @SerialName("motivation_letter_path")
    val motivationLetterPath: String? = null,

    @SerialName("cv_path")
    val cvPath: String? = null,

    val status: String
)