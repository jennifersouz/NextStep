package com.example.nextstep.ui.screens.teacher

import androidx.annotation.StringRes
import com.example.nextstep.data.model.TeacherProfileDto

data class TeacherProfileUiState(
    val profile: TeacherProfileDto? = null,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
)