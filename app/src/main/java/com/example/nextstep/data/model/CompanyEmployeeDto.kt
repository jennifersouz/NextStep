package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyEmployeeDto(
    @SerialName("profile_id")
    val profileId: String? = null,
    @SerialName("company_profile_id")
    val companyProfileId: String = "",
    val email: String = "",
    @SerialName("first_name")
    val firstName: String = "",
    @SerialName("last_name")
    val lastName: String = "",
    val phone: String? = null,
    val department: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    val status: String = "active"
)

data class CompanyEmployeeInviteDisplayDto(
    val id: String,
    val profileId: String? = null,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val department: String? = null,
    val status: String,
    val acceptedAt: String? = null
) {
    val displayName: String
        get() {
            val fullName = "${firstName.orEmpty()} ${lastName.orEmpty()}".trim()
            return if (fullName.isNotBlank()) fullName else email
        }
}
