package com.example.nextstep.ui.screens.advisor

import com.example.nextstep.data.model.AdvisorStudentDetailDto

data class AdvisorStudentDetailUiState(
    val isLoading: Boolean = false,
    val detail: AdvisorStudentDetailDto? = null,
    val errorMessage: String? = null
)