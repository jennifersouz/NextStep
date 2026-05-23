package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateOfferDto(
    @SerialName("company_profile_id")
    val companyProfileId: String,

    @SerialName("company_name")
    val companyName: String,

    val title: String,

    val description: String,

    val area: String,

    val location: String,

    @SerialName("work_mode")
    val workMode: String,

    val duration: String,

    val vacancies: Int,

    val requirements: String,

    @SerialName("is_active")
    val isActive: Boolean = true
)