package com.example.nextstep.ui.screens.advisor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.repository.AdvisorNotificationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdvisorNotificationsViewModel : ViewModel() {

    private val repository = AdvisorNotificationsRepository()

    private val _uiState = MutableStateFlow(AdvisorNotificationsUiState())
    val uiState: StateFlow<AdvisorNotificationsUiState> = _uiState.asStateFlow()

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
                            unreadCount = notifications.count { n -> !it.isRead(n) }
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

    private fun AdvisorNotificationsUiState.isRead(notification: com.example.nextstep.data.model.NotificationDto): Boolean {
        return notification.isRead
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
