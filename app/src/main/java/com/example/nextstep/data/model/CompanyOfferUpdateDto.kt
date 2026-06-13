package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyOfferUpdateDto(
    val title: String,
    val description: String,
    val area: String,
    val location: String,

    @SerialName("work_mode")
    val workMode: String,

    val duration: String,
    val vacancies: Int,
    val requirements: String? = null,

    @SerialName("is_active")
    val isActive: Boolean
)