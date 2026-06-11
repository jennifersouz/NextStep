package com.example.nextstep.ui.screens.advisor

import com.example.nextstep.data.model.AdvisorConversationDto

data class AdvisorMessagesUiState(
    val isLoading: Boolean = false,
    val conversations: List<AdvisorConversationDto> = emptyList(),
    val errorMessage: String? = null
)