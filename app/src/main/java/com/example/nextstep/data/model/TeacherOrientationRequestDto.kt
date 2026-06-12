package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeacherOrientationRequestDto(
    val id: String,
    @SerialName("student_profile_id")
    val studentProfileId: String,
    @SerialName("student_name")
    val studentName: String,
    @SerialName("student_email")
    val studentEmail: String,
    @SerialName("application_id")
    val applicationId: String,
    @SerialName("offer_id")
    val offerId: String,
    @SerialName("offer_title")
    val offerTitle: String,
    @SerialName("company_name")
    val companyName: String,
    val status: String,
    @SerialName("created_at")
    val createdAt: String,
    
    // Additional fields for detail
    val course: String? = null,
    val city: String? = null,
    val country: String? = null,
    val location: String? = null,
    @SerialName("work_mode")
    val workMode: String? = null,
    val duration: String? = null,
    val description: String? = null,
    @SerialName("cv_path")
    val cvPath: String? = null,
    @SerialName("motivation_letter_path")
    val motivationLetterPath: String? = null
)
