package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OfferDto(
    val id: String,

    @SerialName("company_profile_id")
    val companyProfileId: String? = null,

    @SerialName("company_name")
    val companyName: String,

    val title: String,

    val description: String? = null,

    val area: String? = null,

    val location: String,

    @SerialName("work_mode")
    val workMode: String? = null,

    val duration: String? = null,

    val vacancies: Int = 1,

    val requirements: String? = null,

    @SerialName("is_active")
    val isActive: Boolean = true,

    @SerialName("archived_at")
    val archivedAt: String? = null,

    @SerialName("archived_by")
    val archivedBy: String? = null,

    @SerialName("archive_reason")
    val archiveReason: String? = null
)