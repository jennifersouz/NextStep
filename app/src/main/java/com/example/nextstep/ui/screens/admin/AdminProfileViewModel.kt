package com.example.nextstep.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.repository.AdminDashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminProfileUiState(
    val isLoading: Boolean = false,
    val adminName: String = "",
    val adminEmail: String = ""
)

class AdminProfileViewModel : ViewModel() {

    private val repository = AdminDashboardRepository()

    private val _uiState = MutableStateFlow(AdminProfileUiState(isLoading = true))
    val uiState: StateFlow<AdminProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val nameResult = repository.getAdminProfileName()
            val emailResult = repository.getAdminProfileEmail()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                adminName = nameResult.getOrDefault(""),
                adminEmail = emailResult.getOrDefault("")
            )
        }
    }
}
