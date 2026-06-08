package com.example.nextstep.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.ApplicationChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApplicationChatViewModel : ViewModel() {

    private val repository = ApplicationChatRepository()

    private val _uiState = MutableStateFlow(ApplicationChatUiState())
    val uiState: StateFlow<ApplicationChatUiState> = _uiState.asStateFlow()

    private var currentApplicationId: String? = null

    fun start(applicationId: String) {
        if (applicationId.isBlank()) return
        if (currentApplicationId == applicationId) return

        currentApplicationId = applicationId
        loadMessages(applicationId, showLoading = true)
        viewModelScope.launch {
            repository.markMessagesAsRead(applicationId)
        }
        repository.subscribeToMessages(
            applicationId = applicationId,
            scope = viewModelScope,
            onMessageReceived = {
                loadMessages(applicationId, showLoading = false)
                viewModelScope.launch {
                    repository.markMessagesAsRead(applicationId)
                }
            }
        )
    }

    fun loadMessages(
        applicationId: String,
        showLoading: Boolean = true
    ) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessageRes = null
                )
            }

            val result = repository.getMessages(applicationId)

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    messages = result.getOrDefault(emptyList()),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else if (showLoading) {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.chat_load_error
                )
            } else {
                _uiState.value
            }
        }
    }

    fun onMessageChanged(text: String) {
        _uiState.value = _uiState.value.copy(
            messageText = text,
            errorMessageRes = null
        )
    }

    fun sendMessage() {
        val applicationId = currentApplicationId ?: return
        val content = _uiState.value.messageText.trim()
        if (content.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSending = true,
                errorMessageRes = null
            )

            val result = repository.sendMessage(applicationId, content)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    messageText = "",
                    isSending = false
                )
                loadMessages(applicationId, showLoading = false)
            } else {
                _uiState.value = _uiState.value.copy(
                    isSending = false,
                    errorMessageRes = R.string.message_send_error
                )
            }
        }
    }

    fun stopRealtime() {
        repository.unsubscribeFromMessages()
        currentApplicationId = null
    }

    override fun onCleared() {
        repository.unsubscribeFromMessages()
        super.onCleared()
    }
}
