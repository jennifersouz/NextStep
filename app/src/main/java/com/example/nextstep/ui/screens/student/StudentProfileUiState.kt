package com.example.nextstep.ui.screens.student

import androidx.annotation.StringRes
import com.example.nextstep.data.model.StudentProfile

data class StudentProfileUiState(
    val profile: StudentProfile? = null,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
)