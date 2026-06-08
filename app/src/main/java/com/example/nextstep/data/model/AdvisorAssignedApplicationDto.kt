package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdvisorAssignedApplicationDto(
    @SerialName("application_id")
    val applicationId: String,

    @SerialName("student_profile_id")
    val studentProfileId: String,

    @SerialName("advisor_profile_id")
    val advisorProfileId: String,

    val status: String,

    @SerialName("student_presence_confirmed")
    val studentPresenceConfirmed: Boolean = false,

    @SerialName("first_name")
    val firstName: String? = null,

    @SerialName("last_name")
    val lastName: String? = null,

    val course: String? = null,

    @SerialName("student_number")
    val studentNumber: String? = null,

    @SerialName("student_email")
    val studentEmail: String? = null,

    @SerialName("offer_title")
    val offerTitle: String? = null,

    @SerialName("last_message")
    val lastMessage: String? = null,

    @SerialName("last_message_at")
    val lastMessageAt: String? = null,

    @SerialName("unread_count")
    val unreadCount: Int = 0
) {
    val studentFullName: String
        get() = listOfNotNull(firstName, lastName)
            .filter { part -> part.isNotBlank() }
            .joinToString(" ")
            .ifBlank { studentEmail.orEmpty() }
}
