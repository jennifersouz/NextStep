package com.example.nextstep.ui.screens.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.ApplicationChatRepository
import com.example.nextstep.data.repository.AdvisorAssignedApplicationsRepository
import com.example.nextstep.data.remote.SupabaseClientProvider
import com.example.nextstep.data.model.ApplicationMessageDto
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApplicationChatViewModel : ViewModel() {

    private val repository = ApplicationChatRepository()
    private val assignedAppsRepository = AdvisorAssignedApplicationsRepository()
    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    private val _uiState = MutableStateFlow(ApplicationChatUiState())
    val uiState: StateFlow<ApplicationChatUiState> = _uiState.asStateFlow()

    private var currentApplicationId: String? = null

    init {
        refreshCurrentUserId()
    }

    private fun refreshCurrentUserId() {
        val currentUser = auth.currentUserOrNull()
        _uiState.value = _uiState.value.copy(
            currentUserId = currentUser?.id.orEmpty()
        )
    }

    fun start(applicationId: String, participantName: String? = null) {
        if (applicationId.isBlank()) return
        
        refreshCurrentUserId()
        
        val isNewConversation = currentApplicationId != applicationId
        
        if (isNewConversation) {
            currentApplicationId = applicationId
            _uiState.value = _uiState.value.copy(
                messages = emptyList(),
                participantName = if (participantName.isNullOrBlank() || participantName == "Chat") "" else participantName,
                isLoading = true,
                errorMessageRes = null
            )
        } else if (!participantName.isNullOrBlank() && participantName != "Chat") {
            _uiState.value = _uiState.value.copy(participantName = participantName)
        }

        // Se o nome ainda é nulo ou genérico, tenta buscar de forma robusta no repositório
        if (_uiState.value.participantName.isBlank()) {
            fetchParticipantDetails(applicationId)
        }
        
        loadMessages(applicationId, showLoading = isNewConversation)
        
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

    private fun fetchParticipantDetails(applicationId: String) {
        viewModelScope.launch {
            try {
                assignedAppsRepository.getAssignedApplications().onSuccess { apps ->
                    val app = apps.find { it.applicationId == applicationId }
                    if (app != null) {
                        _uiState.value = _uiState.value.copy(
                            participantName = app.studentFullName
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error fetching participant details", e)
            }
        }
    }

    fun loadMessages(
        applicationId: String,
        showLoading: Boolean = true
    ) {
        viewModelScope.launch {
            if (showLoading && _uiState.value.messages.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessageRes = null
                )
            }

            val result = repository.getMessages(applicationId)

            if (result.isSuccess) {
                val messages = result.getOrDefault(emptyList())
                
                // Só extrai o nome das mensagens se ainda não tivermos um nome real (não vazio e não "Chat"/"Aluno")
                val currentName = _uiState.value.participantName
                val finalName = if (currentName.isBlank() || currentName == "Chat" || currentName == "Aluno") {
                    extractParticipantName(messages)
                } else {
                    currentName
                }
                
                _uiState.value = _uiState.value.copy(
                    messages = messages,
                    isLoading = false,
                    errorMessageRes = null,
                    participantName = finalName
                )
            } else if (showLoading) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.chat_load_error
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun extractParticipantName(messages: List<ApplicationMessageDto>): String {
        // Garantir que temos o ID do usuário atual para filtrar corretamente
        val currentUserId = auth.currentUserOrNull()?.id ?: _uiState.value.currentUserId
        val currentUserEmail = auth.currentUserOrNull()?.email
        
        if (currentUserId.isBlank() && (currentUserEmail == null || currentUserEmail.isBlank())) {
            return "Chat"
        }

        // Tenta encontrar a primeira mensagem enviada por OUTRA pessoa
        val otherMessage = messages.firstOrNull { 
            it.senderProfileId != currentUserId && 
            (currentUserEmail == null || it.senderEmail != currentUserEmail) 
        }
        
        val emailToParse = if (otherMessage != null) {
            otherMessage.senderEmail
        } else {
            // Se todas as mensagens forem minhas, o outro participante é o receptor
            val myMessage = messages.firstOrNull()
            if (myMessage != null && (myMessage.senderProfileId == currentUserId || myMessage.senderEmail == currentUserEmail)) {
                myMessage.receiverEmail
            } else {
                null
            }
        }

        if (emailToParse.isNullOrBlank()) return "Aluno"

        // Formata o email (ex: joana.silva@email.com -> Joana Silva)
        return emailToParse.substringBefore("@")
            .replace(".", " ")
            .replace("_", " ")
            .split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { word -> word.replaceFirstChar { c -> c.uppercase() } }
            .takeIf { it.isNotBlank() }
            ?: "Aluno"
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
