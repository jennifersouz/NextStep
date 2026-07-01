package com.example.nextstep.ui.screens.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.model.TeacherConversationDto
import com.example.nextstep.data.repository.TeacherMessagesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TeacherMessagesViewModel(
    private val repository: TeacherMessagesRepository = TeacherMessagesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherMessagesUiState())
    val uiState: StateFlow<TeacherMessagesUiState> = _uiState.asStateFlow()

    fun loadConversations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            repository.getConversations()
                .onSuccess { conversations ->
                    val sorted = conversations.sortedByDescending { it.lastMessageAt ?: "" }
                    _uiState.update { it.copy(
                        isLoading = false,
                        conversations = sorted
                    ) }
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessageRes = R.string.teacher_conversations_load_error
                    ) }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun getFilteredConversations(): List<TeacherConversationDto> {
        val state = _uiState.value
        if (state.searchQuery.isBlank()) return state.conversations

        val query = state.searchQuery.lowercase().trim()
        return state.conversations.filter { conversation ->
            conversation.studentName.lowercase().contains(query) ||
            (conversation.offerTitle?.lowercase()?.contains(query) == true) ||
            (conversation.companyName?.lowercase()?.contains(query) == true)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessageRes = null) }
    }
}
