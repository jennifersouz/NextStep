package com.example.nextstep.ui.screens.teacher

import com.example.nextstep.data.model.TeacherConversationDto

data class TeacherMessagesUiState(
    val isLoading: Boolean = false,
    val conversations: List<TeacherConversationDto> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null
)