package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentAssignedAdvisorDto(
    @SerialName("application_id")
    val applicationId: String,

    @SerialName("student_profile_id")
    val studentProfileId: String,

    @SerialName("company_profile_id")
    val companyProfileId: String,

    @SerialName("advisor_profile_id")
    val advisorProfileId: String? = null,

    @SerialName("application_status")
    val applicationStatus: String? = null,

    @SerialName("advisor_name")
    val advisorName: String? = null,

    @SerialName("advisor_first_name")
    val advisorFirstName: String? = null,

    @SerialName("advisor_last_name")
    val advisorLastName: String? = null,

    @SerialName("advisor_email")
    val advisorEmail: String? = null,

    @SerialName("advisor_phone")
    val advisorPhone: String? = null,

    @SerialName("advisor_department")
    val advisorDepartment: String? = null,

    @SerialName("offer_title")
    val offerTitle: String? = null
) {
    val formattedAdvisorName: String
        get() = listOfNotNull(advisorFirstName, advisorLastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .takeIf { it.isNotBlank() }
            ?: advisorName.takeIf { !it.isNullOrBlank() }
            ?: "Orientador"
}