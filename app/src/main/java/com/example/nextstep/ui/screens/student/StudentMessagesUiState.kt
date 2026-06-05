package com.example.nextstep.ui.screens.student

import androidx.annotation.StringRes
import com.example.nextstep.data.model.StudentChatConversationDto

data class StudentMessagesUiState(
    val conversations: List<StudentChatConversationDto> = emptyList(),
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
)