package com.example.nextstep.ui.screens.institution

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.InstitutionUsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddInstitutionUserViewModel : ViewModel() {

    private val institutionUsersRepository = InstitutionUsersRepository()

    private val _uiState = MutableStateFlow(AddInstitutionUserUiState())
    val uiState: StateFlow<AddInstitutionUserUiState> = _uiState.asStateFlow()

    fun onTypeSelected(type: UserType) {
        _uiState.value = _uiState.value.copy(
            selectedType = type,
            emailError = null,
            errorMessage = null
        )
    }

    fun onEmailChange(value: String) {
        val cleanedValue = value.trim()
        _uiState.value = _uiState.value.copy(
            email = cleanedValue,
            emailError = validateEmail(cleanedValue),
            errorMessage = null
        )
    }

    fun createInvite() {
        val state = _uiState.value

        val emailError = validateEmail(state.email)

        _uiState.value = state.copy(
            emailError = emailError,
            errorMessage = if (emailError != null) "Corrija os campos assinalados antes de continuar." else null
        )

        if (emailError != null) return

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)

            val result = institutionUsersRepository.createInvite(
                targetRole = if (state.selectedType == UserType.STUDENT) "student" else "teacher",
                email = state.email
            )

            _uiState.value = state.copy(isLoading = false)

            if (result.isSuccess) {
                _uiState.value = state.copy(isSuccess = true, errorMessage = null)
            } else {
                _uiState.value = state.copy(
                    errorMessage = "Não foi possível criar o convite. Tente novamente.",
                    isSuccess = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun validateEmail(value: String): Int? {
        return when {
            value.isBlank() -> R.string.error_required_field
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> R.string.error_invalid_email
            else -> null
        }
    }
}

data class AddInstitutionUserUiState(
    val selectedType: UserType = UserType.STUDENT,
    val email: String = "",
    val emailError: Int? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)

enum class UserType {
    STUDENT,
    TEACHER
}