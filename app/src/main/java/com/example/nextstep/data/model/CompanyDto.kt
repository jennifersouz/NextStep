package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyDto(
    val id: String? = null,
    @SerialName("profile_id")
    val profileId: String? = null,
    @SerialName("company_name")
    val companyName: String,
    val nif: String? = null,
    @SerialName("business_area")
    val businessArea: String? = null,
    val location: String? = null,
    val description: String? = null,
    val phone: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("offers_count")
    val offersCount: Int? = null
)

@Serializable
data class UpdateCompanyDto(
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

@Serializable
data class CreateCompanyDto(
    @SerialName("profile_id")
    val profileId: String? = null,
    @SerialName("company_name")
    val companyName: String,
    val nif: String? = null,
    @SerialName("business_area")
    val businessArea: String? = null,
    val location: String? = null,
    val description: String? = null,
    val phone: String? = null,
    @SerialName("is_active")
    val isActive: Boolean = true
)