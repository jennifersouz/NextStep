package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateApplicationViewedDto(
    @SerialName("viewed_by_company")
    val viewedByCompany: Boolean = true
)