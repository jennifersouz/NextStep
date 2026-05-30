package com.example.nextstep.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.StudentProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentEditProfileViewModel : ViewModel() {

    private val repository = StudentProfileRepository()

    private val _uiState = MutableStateFlow(StudentEditProfileUiState())
    val uiState: StateFlow<StudentEditProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null,
                successMessageRes = null
            )

            val result = repository.getCurrentStudentProfile()

            _uiState.value = if (result.isSuccess) {
                val profile = result.getOrNull()

                _uiState.value.copy(
                    email = profile?.email.orEmpty(),
                    firstName = profile?.firstName.orEmpty(),
                    lastName = profile?.lastName.orEmpty(),
                    educationInstitution = profile?.educationInstitution.orEmpty(),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.student_profile_load_error
                )
            }
        }
    }

    fun onFirstNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            firstName = value,
            firstNameErrorRes = null,
            successMessageRes = null
        )
    }

    fun onLastNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            lastName = value,
            lastNameErrorRes = null,
            successMessageRes = null
        )
    }

    fun onEducationInstitutionChange(value: String) {
        _uiState.value = _uiState.value.copy(
            educationInstitution = value,
            educationInstitutionErrorRes = null,
            successMessageRes = null
        )
    }

    fun saveProfile(
        onSuccess: () -> Unit
    ) {
        if (!validate()) return

        viewModelScope.launch {
            val state = _uiState.value

            _uiState.value = state.copy(
                isSaving = true,
                errorMessageRes = null,
                successMessageRes = null
            )

            val result = repository.updateCurrentStudentProfile(
                firstName = state.firstName,
                lastName = state.lastName,
                educationInstitution = state.educationInstitution
            )

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isSaving = false,
                    successMessageRes = R.string.student_profile_update_success
                )
            } else {
                _uiState.value.copy(
                    isSaving = false,
                    errorMessageRes = R.string.student_profile_update_error
                )
            }

            if (result.isSuccess) {
                onSuccess()
            }
        }
    }

    private fun validate(): Boolean {
        val state = _uiState.value

        val firstNameError = when {
            state.firstName.trim().isBlank() -> R.string.error_required_field
            !state.firstName.trim().matches(Regex("^[A-Za-zÀ-ÿ\\s]+$")) -> R.string.error_only_letters
            else -> null
        }

        val lastNameError = when {
            state.lastName.trim().isBlank() -> R.string.error_required_field
            !state.lastName.trim().matches(Regex("^[A-Za-zÀ-ÿ\\s]+$")) -> R.string.error_only_letters
            else -> null
        }

        val institutionError = when {
            state.educationInstitution.trim().isBlank() -> R.string.error_required_field
            else -> null
        }

        _uiState.value = state.copy(
            firstNameErrorRes = firstNameError,
            lastNameErrorRes = lastNameError,
            educationInstitutionErrorRes = institutionError
        )

        return firstNameError == null &&
                lastNameError == null &&
                institutionError == null
    }
}