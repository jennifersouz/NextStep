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
import java.net.URLDecoder

class ApplicationChatViewModel : ViewModel() {

    private val repository = ApplicationChatRepository()
    private val assignedAppsRepository = AdvisorAssignedApplicationsRepository()
    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    private val _uiState = MutableStateFlow(ApplicationChatUiState())
    val uiState: StateFlow<ApplicationChatUiState> = _uiState.asStateFlow()

    private var currentApplicationId: String? = null
    private var currentChatType: String = "advisor"

    init {
        refreshCurrentUserId()
    }

    private fun refreshCurrentUserId() {
        val currentUser = auth.currentUserOrNull()
        _uiState.value = _uiState.value.copy(
            currentUserId = currentUser?.id.orEmpty()
        )
    }

    fun start(
        applicationId: String, 
        participantName: String? = null,
        offerTitle: String? = null,
        studentProfileId: String? = null,
        chatType: String = "advisor"
    ) {
        if (applicationId.isBlank()) return
        
        refreshCurrentUserId()
        
        val isNewConversation = currentApplicationId != applicationId || currentChatType != chatType
        currentChatType = chatType
        
        val normalizedName = normalizeName(participantName)
        val normalizedOffer = normalizeName(offerTitle)
        
        if (isNewConversation) {
            currentApplicationId = applicationId
            _uiState.value = _uiState.value.copy(
                messages = emptyList(),
                participantName = if (normalizedName == "Chat" || normalizedName == "Aluno") "" else normalizedName,
                internshipTitle = normalizedOffer,
                chatType = chatType,
                isLoading = true,
                errorMessageRes = null
            )
        } else {
            if (normalizedName.isNotBlank() && normalizedName != "Chat" && normalizedName != "Aluno") {
                if (_uiState.value.participantName != normalizedName) {
                    _uiState.value = _uiState.value.copy(participantName = normalizedName)
                }
            }
            if (normalizedOffer.isNotBlank() && _uiState.value.internshipTitle != normalizedOffer) {
                _uiState.value = _uiState.value.copy(internshipTitle = normalizedOffer)
            }
        }

        if (_uiState.value.participantName.isBlank()) {
            fetchParticipantDetails(applicationId, chatType)
        }
        
        loadMessages(applicationId, chatType, showLoading = isNewConversation)
        
        viewModelScope.launch {
            repository.markMessagesAsRead(applicationId, chatType)
        }
        
        viewModelScope.launch {
            repository.subscribeToMessages(
                applicationId = applicationId,
                chatType = chatType,
                scope = viewModelScope,
                onMessageReceived = {
                    loadMessages(applicationId, chatType, showLoading = false)
                    viewModelScope.launch {
                        repository.markMessagesAsRead(applicationId, chatType)
                    }
                }
            )
        }
    }

    private fun normalizeName(name: String?): String {
        if (name.isNullOrBlank()) return ""
        return try {
            URLDecoder.decode(name, "UTF-8")
                .replace("+", " ")
                .replace(Regex("\\s+"), " ")
                .trim()
        } catch (e: Exception) {
            name.replace("+", " ").trim()
        }
    }

    private fun fetchParticipantDetails(applicationId: String, chatType: String = "advisor") {
        viewModelScope.launch {
            try {
                if (chatType == "advisor") {
                    assignedAppsRepository.getAssignedApplications().onSuccess { apps ->
                        val app = apps.find { it.applicationId == applicationId }
                        if (app != null) {
                            val studentName = app.studentFullName
                            if (studentName.isNotBlank()) {
                                _uiState.value = _uiState.value.copy(
                                    participantName = normalizeName(studentName)
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error fetching participant details", e)
            }
        }
    }

    fun loadMessages(
        applicationId: String,
        chatType: String = "advisor",
        showLoading: Boolean = true
    ) {
        viewModelScope.launch {
            if (showLoading && _uiState.value.messages.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessageRes = null
                )
            }

            val result = repository.getMessages(applicationId, chatType)

            if (result.isSuccess) {
                val messages = result.getOrDefault(emptyList())
                
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
        val currentUser = auth.currentUserOrNull()
        val currentUserId = currentUser?.id ?: _uiState.value.currentUserId
        val currentUserEmail = currentUser?.email
        
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
            .replace("+", " ")
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
        val chatType = currentChatType
        val content = _uiState.value.messageText.trim()
        if (content.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSending = true,
                errorMessageRes = null
            )

            val result = repository.sendMessage(applicationId, content, chatType)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    messageText = "",
                    isSending = false
                )
                loadMessages(applicationId, chatType, showLoading = false)
            } else {
                _uiState.value = _uiState.value.copy(
                    isSending = false,
                    errorMessageRes = R.string.message_send_error
                )
            }
        }
    }

    fun stopRealtime() {
        viewModelScope.launch {
            repository.unsubscribeFromMessages()
        }
        currentApplicationId = null
    }

    override fun onCleared() {
        viewModelScope.launch {
            repository.unsubscribeFromMessages()
        }
        super.onCleared()
    }
}
