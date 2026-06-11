package com.example.nextstep.ui.screens.chat

import androidx.annotation.StringRes
import com.example.nextstep.data.model.ApplicationMessageDto

data class ApplicationChatUiState(
    val isLoading: Boolean = true,
    val isSending: Boolean = false,
    val messages: List<ApplicationMessageDto> = emptyList(),
    val messageText: String = "",
    val currentUserId: String = "",
    val participantName: String = "",
    val internshipTitle: String = "",
    val errorMessageRes: Int? = null
)
