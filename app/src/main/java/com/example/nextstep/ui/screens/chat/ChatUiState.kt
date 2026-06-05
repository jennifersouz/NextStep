package com.example.nextstep.ui.screens.chat

import androidx.annotation.StringRes
import com.example.nextstep.data.model.ChatMessageDto

data class ChatUiState(
    val messages: List<ChatMessageDto> = emptyList(),
    val newMessage: String = "",
    val currentUserId: String = "",
    val isLoading: Boolean = true,
    val isSending: Boolean = false,
    @StringRes val errorMessageRes: Int? = null
)