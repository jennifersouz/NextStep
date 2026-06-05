package com.example.nextstep.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.remote.SupabaseClientProvider
import com.example.nextstep.data.repository.ChatRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val repository = ChatRepository()
    private val auth = SupabaseClientProvider.client.auth

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun loadMessages(applicationId: String) {
        viewModelScope.launch {
            val currentUserId = auth.currentUserOrNull()?.id.orEmpty()

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                currentUserId = currentUserId,
                errorMessageRes = null
            )

            val result = repository.getMessages(applicationId)

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    messages = result.getOrDefault(emptyList()),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.chat_load_error
                )
            }
        }
    }

    fun onMessageChange(value: String) {
        _uiState.value = _uiState.value.copy(
            newMessage = value,
            errorMessageRes = null
        )
    }

    fun sendMessage(applicationId: String) {
        val message = _uiState.value.newMessage.trim()

        if (message.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSending = true,
                errorMessageRes = null
            )

            val result = repository.sendMessage(
                applicationId = applicationId,
                content = message
            )

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    newMessage = "",
                    isSending = false
                )

                loadMessages(applicationId)
            } else {
                _uiState.value = _uiState.value.copy(
                    isSending = false,
                    errorMessageRes = R.string.chat_send_error
                )
            }
        }
    }
}