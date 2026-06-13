package com.example.nextstep.ui.screens.student

import androidx.annotation.StringRes
import com.example.nextstep.data.model.StudentNotificationItem

data class StudentNotificationsUiState(
    val notifications: List<StudentNotificationItem> = emptyList(),
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
) {
    val unreadCount: Int
        get() = notifications.count { item ->
            item.isUnread
        }
}
