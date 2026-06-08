package com.example.nextstep.ui.screens.student

import androidx.annotation.StringRes
import com.example.nextstep.data.model.StudentNotificationDto

data class StudentNotificationsUiState(
    val notifications: List<StudentNotificationDto> = emptyList(),
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
) {
    val unreadCount: Int
        get() = notifications.count { notification ->
            notification.isUnread
        }
}
