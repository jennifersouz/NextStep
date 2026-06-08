package com.example.nextstep.ui.screens.student

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.model.StudentNotificationDto
import com.example.nextstep.data.repository.StudentNotificationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentNotificationsViewModel : ViewModel() {

    private val repository = StudentNotificationsRepository()

    private val _uiState = MutableStateFlow(StudentNotificationsUiState())
    val uiState: StateFlow<StudentNotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            val alreadyHasData = _uiState.value.notifications.isNotEmpty()

            _uiState.value = _uiState.value.copy(
                isLoading = !alreadyHasData,
                errorMessageRes = null
            )

            val result = repository.getStudentNotifications()

            _uiState.value = if (result.isSuccess) {
                val notifications = result.getOrDefault(emptyList())
                Log.d(
                    "NOTIF_DEBUG",
                    "loadNotifications: ${notifications.size} notificações, não lidas = ${notifications.count { it.isUnread }}"
                )
                _uiState.value.copy(
                    notifications = notifications,
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    notifications = emptyList(),
                    isLoading = false,
                    errorMessageRes = R.string.student_notifications_load_error
                )
            }
        }
    }

    fun markAsSeen(
        notification: StudentNotificationDto,
        onLocalStateChanged: (Int) -> Unit = {},
        onSuccess: () -> Unit = {}
    ) {
        Log.d(
            "NOTIF_DEBUG",
            "markAsSeen clicado id=${notification.id} type=${notification.type}"
        )
        val previousNotifications = _uiState.value.notifications

        val updatedNotifications = previousNotifications.map { item ->
            if (item.id == notification.id && item.type == notification.type) {
                item.copy(
                    isSeen = true,
                    studentStatusSeen = true
                )
            } else {
                item
            }
        }

        _uiState.value = _uiState.value.copy(
            notifications = updatedNotifications
        )

        val newUnreadCount = updatedNotifications.count { item ->
            item.isUnread
        }

        Log.d("NOTIF_DEBUG", "markAsSeen estado local atualizado — não lidas = $newUnreadCount")
        onLocalStateChanged(newUnreadCount)

        viewModelScope.launch {
            val result = when (notification.type) {
                "advisor_assigned" -> repository.markAdvisorAssignmentAsSeen(notification.id)
                else -> repository.markNotificationAsSeen(notification.id)
            }

            if (result.isSuccess) {
                Log.d("NOTIF_DEBUG", "markAsSeen Supabase OK id=${notification.id}")
                onSuccess()
            } else {
                Log.d(
                    "NOTIF_DEBUG",
                    "markAsSeen Supabase FAILED id=${notification.id} — a reverter estado local"
                )
                _uiState.value = _uiState.value.copy(
                    notifications = previousNotifications,
                    errorMessageRes = R.string.student_notification_mark_seen_error
                )

                val previousUnreadCount = previousNotifications.count { item ->
                    item.isUnread
                }

                onLocalStateChanged(previousUnreadCount)
            }
        }
    }
}
