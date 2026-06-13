package com.example.nextstep.ui.screens.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.repository.TeacherNotificationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TeacherNotificationsViewModel : ViewModel() {

    private val repository = TeacherNotificationsRepository()

    private val _uiState = MutableStateFlow(TeacherNotificationsUiState())
    val uiState: StateFlow<TeacherNotificationsUiState> = _uiState.asStateFlow()

    private var isSubscribed = false

    init {
        loadNotifications()
        subscribeToNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getNotifications()
                .onSuccess { notifications ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            notifications = notifications,
                            unreadCount = notifications.count { n -> !n.isRead }
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message
                        )
                    }
                }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            repository.markAsRead(notificationId)
                .onSuccess {
                    loadNotifications()
                }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            repository.markAllAsRead()
                .onSuccess {
                    loadNotifications()
                }
        }
    }

    fun subscribeToNotifications() {
        if (isSubscribed) return
        isSubscribed = true

        repository.subscribeToNotifications(
            scope = viewModelScope,
            onNotificationReceived = {
                loadNotifications()
            }
        )
    }

    override fun onCleared() {
        repository.unsubscribeFromNotifications()
        super.onCleared()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}