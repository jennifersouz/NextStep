package com.example.nextstep.ui.screens.chat

import androidx.annotation.StringRes
import com.example.nextstep.data.model.ApplicationMessageDto

data class ApplicationChatUiState(
    val messages: List<ApplicationMessageDto> = emptyList(),
    val messageText: String = "",
    val isLoading: Boolean = true,
    val isSending: Boolean = false,
    @StringRes val errorMessageRes: Int? = null
)
