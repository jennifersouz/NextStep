package com.example.nextstep.ui.screens.student

data class StudentInternshipsUiState(
    val internships: List<InternshipCardUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
