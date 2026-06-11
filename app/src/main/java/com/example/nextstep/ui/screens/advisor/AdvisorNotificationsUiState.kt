package com.example.nextstep.ui.screens.advisor

import com.example.nextstep.data.model.NotificationDto

data class AdvisorNotificationsUiState(
    val isLoading: Boolean = false,
    val notifications: List<NotificationDto> = emptyList(),
    val unreadCount: Int = 0,
    val errorMessage: String? = null
)
