package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationSeenCheckDto(
    val id: String,

    @SerialName("student_status_seen")
    val studentStatusSeen: Boolean
)