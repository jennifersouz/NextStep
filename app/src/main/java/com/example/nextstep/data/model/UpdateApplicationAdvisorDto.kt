package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateApplicationAdvisorDto(
    @SerialName("advisor_profile_id")
    val advisorProfileId: String?
)
