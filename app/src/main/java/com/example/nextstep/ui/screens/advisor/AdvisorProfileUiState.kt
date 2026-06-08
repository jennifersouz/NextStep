package com.example.nextstep.ui.screens.advisor

import androidx.annotation.StringRes
import com.example.nextstep.data.model.AdvisorProfileDto

data class AdvisorProfileUiState(
    val profile: AdvisorProfileDto? = null,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
)