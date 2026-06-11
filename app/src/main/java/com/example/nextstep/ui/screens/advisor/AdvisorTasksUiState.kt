package com.example.nextstep.ui.screens.advisor

import com.example.nextstep.data.model.AdvisorTaskListItemDto

data class AdvisorTasksUiState(
    val isLoading: Boolean = false,
    val tasks: List<AdvisorTaskListItemDto> = emptyList(),
    val selectedFilter: AdvisorTaskFilter = AdvisorTaskFilter.ALL,
    val errorMessage: String? = null,
    val isUpdating: Boolean = false
)
