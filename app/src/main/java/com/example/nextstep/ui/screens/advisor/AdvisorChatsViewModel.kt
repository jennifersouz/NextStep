package com.example.nextstep.ui.screens.advisor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.AdvisorAssignedApplicationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdvisorChatsViewModel : ViewModel() {

    private val repository = AdvisorAssignedApplicationsRepository()

    private val _uiState = MutableStateFlow(AdvisorChatsUiState())
    val uiState: StateFlow<AdvisorChatsUiState> = _uiState.asStateFlow()

    fun loadChats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = repository.getAssignedApplications()

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    conversations = result.getOrDefault(emptyList()),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.advisor_chats_load_error
                )
            }
        }
    }
}
