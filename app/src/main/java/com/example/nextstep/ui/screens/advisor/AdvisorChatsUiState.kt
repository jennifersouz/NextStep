package com.example.nextstep.ui.screens.advisor

import androidx.annotation.StringRes
import com.example.nextstep.data.model.AdvisorAssignedApplicationDto

data class AdvisorChatsUiState(
    val conversations: List<AdvisorAssignedApplicationDto> = emptyList(),
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
)
