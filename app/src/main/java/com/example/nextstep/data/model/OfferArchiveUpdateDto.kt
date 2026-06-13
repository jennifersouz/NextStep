package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OfferArchiveUpdateDto(
    @SerialName("is_active")
    val isActive: Boolean = false,

    @SerialName("archived_at")
    val archivedAt: String,

    @SerialName("archived_by")
    val archivedBy: String? = null,

    @SerialName("archive_reason")
    val archiveReason: String? = null
)