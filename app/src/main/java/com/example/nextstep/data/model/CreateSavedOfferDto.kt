package com.example.nextstep.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateSavedOfferDto(
    @SerialName("student_profile_id")
    val studentProfileId: String,

    @SerialName("offer_id")
    val offerId: String
)