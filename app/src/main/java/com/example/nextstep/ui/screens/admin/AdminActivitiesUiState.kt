package com.example.nextstep.ui.screens.admin

data class AdminActivitiesUiState(
    val isLoading: Boolean = false,
    val activities: List<RecentActivityUiModel> = emptyList(),
    val errorMessage: String? = null
)
