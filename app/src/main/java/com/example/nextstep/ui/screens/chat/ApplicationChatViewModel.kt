package com.example.nextstep.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.ApplicationChatRepository
import com.example.nextstep.data.remote.SupabaseClientProvider
import com.example.nextstep.data.model.ApplicationMessageDto
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApplicationChatViewModel : ViewModel() {

    private val repository = ApplicationChatRepository()
    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    private val _uiState = MutableStateFlow(ApplicationChatUiState())
    val uiState: StateFlow<ApplicationChatUiState> = _uiState.asStateFlow()

    private var currentApplicationId: String? = null

    init {
        val currentUser = auth.currentUserOrNull()
        _uiState.value = _uiState.value.copy(
            currentUserId = currentUser?.id.orEmpty()
        )
    }

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
                val messages = result.getOrDefault(emptyList())
                val participantName = extractParticipantName(messages)
                val internshipTitle = extractInternshipTitle(messages)
                _uiState.value.copy(
                    messages = messages,
                    isLoading = false,
                    errorMessageRes = null,
                    participantName = participantName,
                    internshipTitle = internshipTitle
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

    private fun extractParticipantName(messages: List<ApplicationMessageDto>): String {
        val currentUserId = _uiState.value.currentUserId
        val otherMessage = messages.firstOrNull { it.senderProfileId != currentUserId }
            ?: messages.firstOrNull()
            ?: return ""

        return otherMessage.senderEmail
            ?.substringBefore("@")
            ?.replace(".", " ")
            ?.replace("_", " ")
            ?.split(" ")
            ?.filter { it.isNotBlank() }
            ?.joinToString(" ") { word -> word.replaceFirstChar { c -> c.uppercase() } }
            ?.takeIf { it.isNotBlank() }
            ?: ""
    }

    private fun extractInternshipTitle(messages: List<ApplicationMessageDto>): String {
        return ""
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
