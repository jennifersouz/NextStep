package com.example.nextstep.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.AdminDashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminEditProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val name: String = "",
    val nameErrorRes: Int? = null,
    val email: String = "",
    val successMessageRes: Int? = null,
    val errorMessageRes: Int? = null
)

class AdminEditProfileViewModel : ViewModel() {

    private val repository = AdminDashboardRepository()

    private val _uiState = MutableStateFlow(AdminEditProfileUiState())
    val uiState: StateFlow<AdminEditProfileUiState> = _uiState.asStateFlow()

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
                name = nameResult.getOrDefault(""),
                email = emailResult.getOrDefault("")
            )
        }
    }

    fun onNameChange(value: String) {
        val sanitizedName = value.filter { it.isLetter() || it.isWhitespace() }
        _uiState.value = _uiState.value.copy(
            name = sanitizedName,
            nameErrorRes = null,
            successMessageRes = null,
            errorMessageRes = null
        )
    }

    fun saveProfile(onSuccess: () -> Unit) {
        if (!validate()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessageRes = null,
                successMessageRes = null
            )

            val result = repository.updateAdminProfileName(_uiState.value.name)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successMessageRes = R.string.profile_updated_success
                )
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessageRes = R.string.teacher_profile_update_error
                )
            }
        }
    }

    private fun validate(): Boolean {
        val state = _uiState.value

        val error = when {
            state.name.trim().isBlank() -> R.string.error_required_field
            state.name.trim().length < 2 -> R.string.error_name_too_short
            !state.name.trim().all { it.isLetter() || it.isWhitespace() } -> R.string.error_only_letters
            else -> null
        }

        _uiState.value = state.copy(nameErrorRes = error)

        return error == null
    }
}
