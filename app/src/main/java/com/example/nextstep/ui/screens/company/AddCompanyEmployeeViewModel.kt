package com.example.nextstep.ui.screens.company

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.CompanyAdvisorsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddCompanyEmployeeViewModel : ViewModel() {

    private val repository = CompanyAdvisorsRepository()

    private val _uiState = MutableStateFlow(AddCompanyEmployeeUiState())
    val uiState: StateFlow<AddCompanyEmployeeUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        val cleanedValue = value.trim()
        _uiState.value = _uiState.value.copy(
            email = cleanedValue,
            emailError = null,
            errorMessageRes = null
        )
    }

    fun createInvite(onSuccess: () -> Unit) {
        val state = _uiState.value

        val emailError = validateEmail(state.email)

        _uiState.value = state.copy(
            emailError = emailError,
            errorMessageRes = if (emailError != null) R.string.form_has_errors else null
        )

        if (emailError != null) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessageRes = null)

            val result = repository.inviteAdvisor(email = state.email)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.employee_create_error
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessageRes = null)
    }

    private fun validateEmail(value: String): Int? {
        return when {
            value.isBlank() -> R.string.error_required_field
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> R.string.error_invalid_email
            else -> null
        }
    }
}

data class AddCompanyEmployeeUiState(
    val email: String = "",
    val emailError: Int? = null,
    @StringRes val errorMessageRes: Int? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)
