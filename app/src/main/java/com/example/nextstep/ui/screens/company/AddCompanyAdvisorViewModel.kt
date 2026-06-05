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

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            name = value,
            nameErrorRes = null,
            errorMessageRes = null
        )
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(
            email = value,
            emailErrorRes = null,
            errorMessageRes = null
        )
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(
            phone = value,
            errorMessageRes = null
        )
    }

    fun onDepartmentChange(value: String) {
        _uiState.value = _uiState.value.copy(
            department = value,
            errorMessageRes = null
        )
    }

    fun createAdvisor(
        onSuccess: () -> Unit
    ) {
        val state = _uiState.value

        val nameError = if (state.name.isBlank()) {
            R.string.error_required_field
        } else {
            null
        }

        val emailError = if (state.email.isBlank()) {
            R.string.error_required_field
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches()) {
            R.string.error_invalid_email
        } else {
            null
        }

        if (nameError != null || emailError != null) {
            _uiState.value = state.copy(
                nameErrorRes = nameError,
                emailErrorRes = emailError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessageRes = null
            )

            val result = repository.createAdvisor(
                name = state.name,
                email = state.email,
                phone = state.phone,
                department = state.department
            )

            if (result.isSuccess) {
                _uiState.value = AddCompanyAdvisorUiState()
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessageRes = R.string.advisor_create_error
                )
            }
        }
    }
}