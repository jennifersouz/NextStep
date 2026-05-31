package com.example.nextstep.ui.screens.student

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
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
                Log.d("NOTIF_DEBUG", "loadNotifications: ${notifications.size} notificações, não lidas = ${notifications.count { !it.studentStatusSeen }}")
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
        applicationId: String,
        onLocalStateChanged: (Int) -> Unit = {},
        onSuccess: () -> Unit = {}
    ) {
        Log.d("NOTIF_DEBUG", "markAsSeen clicado id=$applicationId")
        val previousNotifications = _uiState.value.notifications

        val updatedNotifications = previousNotifications.map { notification ->
            if (notification.id == applicationId) {
                notification.copy(studentStatusSeen = true)
            } else {
                notification
            }
        }

        _uiState.value = _uiState.value.copy(
            notifications = updatedNotifications
        )

        val newUnreadCount = updatedNotifications.count { notification ->
            !notification.studentStatusSeen
        }

        Log.d("NOTIF_DEBUG", "markAsSeen estado local atualizado — não lidas = $newUnreadCount")
        onLocalStateChanged(newUnreadCount)

        viewModelScope.launch {
            val result = repository.markNotificationAsSeen(applicationId)

            if (result.isSuccess) {
                Log.d("NOTIF_DEBUG", "markAsSeen Supabase OK id=$applicationId")
                onSuccess()
                // Não recarregar aqui: o estado local já está correto e um reload
                // assíncrono pode sobrescrever com dados antigos (race condition).
            } else {
                Log.d("NOTIF_DEBUG", "markAsSeen Supabase FAILED id=$applicationId — a reverter estado local")
                _uiState.value = _uiState.value.copy(
                    notifications = previousNotifications,
                    errorMessageRes = R.string.student_notification_mark_seen_error
                )

                val previousUnreadCount = previousNotifications.count { notification ->
                    !notification.studentStatusSeen
                }

                onLocalStateChanged(previousUnreadCount)
            }
        }
    }
}