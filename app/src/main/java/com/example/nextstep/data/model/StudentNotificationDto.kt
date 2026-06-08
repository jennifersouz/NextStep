package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentNotificationDto(
    val id: String,

    val type: String = "application_status",

    @SerialName("offer_id")
    val offerId: String? = null,

    @SerialName("student_profile_id")
    val studentProfileId: String? = null,

    @SerialName("company_profile_id")
    val companyProfileId: String? = null,

    val status: String? = null,

    @SerialName("student_status_seen")
    val studentStatusSeen: Boolean = false,

    @SerialName("is_seen")
    val isSeen: Boolean? = null,

    @SerialName("status_updated_at")
    val statusUpdatedAt: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("offer_title")
    val offerTitle: String? = null,

    @SerialName("company_name")
    val companyName: String? = null,

    @SerialName("advisor_name")
    val advisorName: String? = null
) {
    val isUnread: Boolean
        get() = !(isSeen ?: studentStatusSeen)

    val sortDate: String
        get() = createdAt ?: statusUpdatedAt.orEmpty()
}
