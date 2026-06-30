package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SentAdvisorRequestDto(
    val id: String,

    @SerialName("student_profile_id")
    val studentProfileId: String? = null,

    @SerialName("teacher_profile_id")
    val teacherProfileId: String? = null,

    @SerialName("teacher_status")
    val teacherStatus: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    // This field might need to be filled manually or from a join/view
    @SerialName("teacher_name")
    val teacherName: String? = null,

    @SerialName("offer_id")
    val offerId: String? = null,

    @SerialName("offer_title")
    val offerTitle: String? = null
)
