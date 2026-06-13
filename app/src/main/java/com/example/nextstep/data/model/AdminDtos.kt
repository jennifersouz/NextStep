package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminProfileDto(
    val id: String,
    val email: String? = null,
    val role: String? = null,

    @SerialName("first_name")
    val firstName: String? = null,

    @SerialName("last_name")
    val lastName: String? = null,

    val phone: String? = null,

    @SerialName("is_active")
    val isActive: Boolean? = true,

    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class AdminCompanyDto(
    val id: String,

    @SerialName("profile_id")
    val profileId: String? = null,

    @SerialName("company_name")
    val companyName: String? = null,

    val nif: String? = null,

    @SerialName("business_area")
    val businessArea: String? = null,

    val location: String? = null,
    val description: String? = null,
    val phone: String? = null,

    @SerialName("is_active")
    val isActive: Boolean? = true,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("offers_count")
    val offersCount: Int = 0
)

@Serializable
data class AdminOfferDto(
    val id: String,

    @SerialName("is_active")
    val isActive: Boolean? = null,

    @SerialName("company_profile_id")
    val companyProfileId: String? = null
)

@Serializable
data class AdminApplicationDto(
    val id: String
)

@Serializable
data class AdminTeacherEvaluationDto(
    val id: String,
    val status: String? = null
)

@Serializable
data class AdminProfileUpdateDto(
    @SerialName("first_name")
    val firstName: String? = null,

    @SerialName("last_name")
    val lastName: String? = null,

    val phone: String? = null,
    val role: String? = null,

    @SerialName("is_active")
    val isActive: Boolean? = null
)

@Serializable
data class AdminCompanyUpdateDto(
    @SerialName("company_name")
    val companyName: String? = null,

    val nif: String? = null,

    @SerialName("business_area")
    val businessArea: String? = null,

    val location: String? = null,
    val description: String? = null,
    val phone: String? = null,

    @SerialName("is_active")
    val isActive: Boolean? = null
)
