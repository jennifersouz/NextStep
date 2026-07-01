package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.CompanyAdvisorsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddCompanyAdvisorViewModel : ViewModel() {

    private val repository = CompanyAdvisorsRepository()

    private val _uiState = MutableStateFlow(AddCompanyAdvisorUiState())
    val uiState: StateFlow<AddCompanyAdvisorUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(
            email = value,
            emailErrorRes = null,
            errorMessageRes = null,
            successMessageRes = null
        )
    }

    fun sendInvite(
        onSuccess: () -> Unit
    ) {
        val state = _uiState.value

        val emailError = if (state.email.isBlank()) {
            R.string.error_advisor_email_required
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches()) {
            R.string.error_invalid_email
        } else {
            null
        }

        if (emailError != null) {
            _uiState.value = state.copy(emailErrorRes = emailError)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessageRes = null,
                successMessageRes = null
            )

            val result = repository.inviteAdvisor(email = state.email)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successMessageRes = R.string.advisor_invite_sent
                )
                onSuccess()
            } else {
                val errorMessage = result.exceptionOrNull()?.message.orEmpty()

                val errorRes = when {
                    errorMessage.contains("duplicate", ignoreCase = true) ||
                    errorMessage.contains("already exists", ignoreCase = true) ||
                    errorMessage.contains("unique", ignoreCase = true) ||
                    errorMessage.contains("pending", ignoreCase = true) -> {
                        R.string.advisor_invite_pending_exists
                    }
                    else -> R.string.advisor_create_error
                }

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessageRes = errorRes
                )
            }
        }
    }
}