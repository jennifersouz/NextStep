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

    @SerialName("motivation_letter_path")
    val motivationLetterPath: String? = null,

    @SerialName("cv_path")
    val cvPath: String? = null,

    @SerialName("student_presence_confirmed")
    val studentPresenceConfirmed: Boolean = false,

    @SerialName("offer_title")
    val offerTitle: String,

    @SerialName("company_name")
    val companyName: String,

    @SerialName("first_name")
    val firstName: String,

    @SerialName("last_name")
    val lastName: String
) {
    val studentFullName: String
        get() = "$firstName $lastName"
}