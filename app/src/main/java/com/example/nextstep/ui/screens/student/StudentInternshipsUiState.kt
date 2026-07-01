package com.example.nextstep.ui.screens.student

import androidx.annotation.StringRes

data class StudentInternshipsUiState(
    val internships: List<InternshipCardUi> = emptyList(),
    val isLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null
)
