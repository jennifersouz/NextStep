package com.example.nextstep.ui.screens.advisor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.repository.AdvisorMessagesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdvisorMessagesViewModel : ViewModel() {

    private val repository = AdvisorMessagesRepository()

    private val _uiState = MutableStateFlow(AdvisorMessagesUiState(isLoading = true))
    val uiState: StateFlow<AdvisorMessagesUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
    }

    fun loadConversations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository.getAdvisorConversations()
                .onSuccess { conversations ->
                    val sorted = conversations.sortedByDescending { it.lastMessageAt.orEmpty() }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        conversations = sorted
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
        }
    }
}