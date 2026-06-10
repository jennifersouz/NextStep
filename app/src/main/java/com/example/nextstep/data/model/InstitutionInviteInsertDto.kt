package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstitutionInviteInsertDto(
    @SerialName("institution_profile_id")
    val institutionProfileId: String,
    @SerialName("target_role")
    val targetRole: String,
    val email: String
)