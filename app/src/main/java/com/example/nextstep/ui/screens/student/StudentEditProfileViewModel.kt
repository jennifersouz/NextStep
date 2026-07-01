package com.example.nextstep.ui.screens.student

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.AdminUsersRepository
import com.example.nextstep.data.repository.StudentProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentEditProfileViewModel : ViewModel() {

    private val studentRepository = StudentProfileRepository()
    private val adminRepository = AdminUsersRepository()

    private val _uiState = MutableStateFlow(StudentEditProfileUiState())
    val uiState: StateFlow<StudentEditProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadInstitutions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingInstitutions = true)
            val result = adminRepository.getInstitutions()
            result.fold(
                onSuccess = { institutions ->
                    Log.d("StudentEditProfileVM", "Institutions loaded: ${institutions.size}")
                    val currentName = _uiState.value.educationInstitution
                    val match = if (currentName.isNotBlank()) {
                        institutions.find { it.displayName.equals(currentName, ignoreCase = true) }
                    } else null
                    _uiState.value = _uiState.value.copy(
                        availableInstitutions = institutions,
                        isLoadingInstitutions = false,
                        selectedInstitutionId = match?.id ?: "",
                        selectedInstitutionName = match?.displayName ?: currentName
                    )
                },
                onFailure = { e ->
                    Log.e("StudentEditProfileVM", "Error loading institutions", e)
                    _uiState.value = _uiState.value.copy(
                        availableInstitutions = emptyList(),
                        isLoadingInstitutions = false
                    )
                }
            )
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null,
                successMessageRes = null
            )

            val result = studentRepository.getCurrentStudentProfile()

            _uiState.value = if (result.isSuccess) {
                val profile = result.getOrNull()
                val institution = profile?.educationInstitution.orEmpty()

                _uiState.value.copy(
                    email = profile?.email.orEmpty(),
                    firstName = profile?.firstName.orEmpty(),
                    lastName = profile?.lastName.orEmpty(),
                    educationInstitution = institution,
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.student_profile_load_error
                )
            }

            // Load institutions dropdown
            loadInstitutions()
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

    fun onInstitutionSelected(id: String, name: String) {
        _uiState.value = _uiState.value.copy(
            selectedInstitutionId = id,
            selectedInstitutionName = name,
            educationInstitution = name,
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

            val institutionName = state.selectedInstitutionName.takeIf { it.isNotBlank() }
                ?: state.educationInstitution

            val result = studentRepository.updateCurrentStudentProfile(
                firstName = state.firstName,
                lastName = state.lastName,
                educationInstitution = institutionName
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
            state.selectedInstitutionId.isBlank() -> R.string.error_required_field
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