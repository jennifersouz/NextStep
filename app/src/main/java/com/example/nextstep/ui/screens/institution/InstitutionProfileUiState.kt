package com.example.nextstep.ui.screens.institution

import androidx.annotation.StringRes
import com.example.nextstep.data.model.InstitutionProfileDto

data class InstitutionProfileUiState(
    val profile: InstitutionProfileDto? = null,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
)