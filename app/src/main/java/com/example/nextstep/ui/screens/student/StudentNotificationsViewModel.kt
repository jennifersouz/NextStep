package com.example.nextstep.ui.screens.student

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.model.NotificationDto
import com.example.nextstep.data.model.StudentNotificationItem
import com.example.nextstep.data.repository.NotificationsRepository
import com.example.nextstep.data.repository.StudentNotificationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentNotificationsViewModel : ViewModel() {

    private val viewRepository = StudentNotificationsRepository()
    private val tableRepository = NotificationsRepository()

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

            val viewResult = viewRepository.getStudentNotifications()
            val tableResult = tableRepository.getNotifications()

            val merged = mutableListOf<StudentNotificationItem>()

            if (viewResult.isSuccess) {
                viewResult.getOrDefault(emptyList()).forEach { dto ->
                    merged.add(StudentNotificationItem.ViewBased(dto))
                }
            }

            if (tableResult.isSuccess) {
                tableResult.getOrDefault(emptyList()).forEach { dto ->
                    if (dto.type in listOf("message", "evaluation", "teacher_assigned")) {
                        merged.add(StudentNotificationItem.TableBased(dto))
                    }
                }
            }

            merged.sortByDescending { it.sortDate }

            Log.d(
                "NOTIF_DEBUG",
                "loadNotifications: ${merged.size} notificações (view+table), não lidas = ${merged.count { it.isUnread }}"
            )

            _uiState.value = _uiState.value.copy(
                notifications = merged,
                isLoading = false,
                errorMessageRes = null
            )
        }
    }

    fun markAsSeen(
        notification: StudentNotificationItem,
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
                when (item) {
                    is StudentNotificationItem.ViewBased -> item.copy(
                        notification = item.notification.copy(
                            isSeen = true,
                            studentStatusSeen = true
                        )
                    )
                    is StudentNotificationItem.TableBased -> item.copy(
                        notification = item.notification.copy(isRead = true)
                    )
                }
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
            val result = when (notification) {
                is StudentNotificationItem.ViewBased -> {
                    when (notification.type) {
                        "advisor_assigned" -> viewRepository.markAdvisorAssignmentAsSeen(notification.id)
                        else -> viewRepository.markNotificationAsSeen(notification.id)
                    }
                }
                is StudentNotificationItem.TableBased -> {
                    tableRepository.markAsRead(notification.id)
                }
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
