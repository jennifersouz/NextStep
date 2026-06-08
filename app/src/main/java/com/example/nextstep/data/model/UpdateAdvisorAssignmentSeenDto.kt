package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateAdvisorAssignmentSeenDto(
    @SerialName("advisor_assignment_seen")
    val advisorAssignmentSeen: Boolean
)
