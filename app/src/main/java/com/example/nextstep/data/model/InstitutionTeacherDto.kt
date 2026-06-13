package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstitutionTeacherDto(
    @SerialName("profile_id")
    val profileId: String,
    @SerialName("first_name")
    val firstName: String = "",
    @SerialName("last_name")
    val lastName: String = "",
    val email: String = "",
    val phone: String? = null,
    val department: String? = null,
    @SerialName("institution_profile_id")
    val institutionProfileId: String = "",
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("institution_archived_at")
    val institutionArchivedAt: String? = null,
    @SerialName("institution_archived_by")
    val institutionArchivedBy: String? = null,
    @SerialName("institution_archive_reason")
    val institutionArchiveReason: String? = null
)
