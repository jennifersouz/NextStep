package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstitutionArchiveUpdateDto(
    @SerialName("institution_archived_at")
    val institutionArchivedAt: String,

    @SerialName("institution_archived_by")
    val institutionArchivedBy: String? = null,

    @SerialName("institution_archive_reason")
    val institutionArchiveReason: String? = null
)
