package com.example.nextstep.ui.screens.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.TeacherProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeacherEditProfileViewModel : ViewModel() {

    private val repository = TeacherProfileRepository()

    private val _uiState = MutableStateFlow(TeacherEditProfileUiState())
    val uiState: StateFlow<TeacherEditProfileUiState> = _uiState.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.getTeacherProfile()

            if (result.isSuccess) {
                val profile = result.getOrNull()
                _uiState.value = _uiState.value.copy(
                    firstName = profile?.firstName.orEmpty(),
                    lastName = profile?.lastName.orEmpty(),
                    phone = profile?.phone.orEmpty(),
                    department = profile?.department.orEmpty(),
                    email = profile?.email.orEmpty(),
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.teacher_profile_load_error
                )
            }
        }
    }

    fun onNameChange(name: String) {
        // Parse full name into first/last on space
        val parts = name.trim().split(" ", limit = 2)
        _uiState.value = _uiState.value.copy(
            firstName = parts.getOrElse(0) { "" },
            lastName = parts.getOrElse(1) { "" },
            nameError = null
        )
    }
    

    fun onPhoneChange(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone)
    }

    fun onDepartmentChange(department: String) {
        _uiState.value = _uiState.value.copy(department = department)
    }

    fun saveProfile(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.name.isBlank()) {
            _uiState.value = _uiState.value.copy(
                nameError = "O nome é obrigatório."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            val result = repository.updateTeacherProfile(
                firstName = state.firstName,
                lastName = state.lastName,
                phone = state.phone,
                department = state.department
            )

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successMessageRes = R.string.teacher_profile_update_success
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
}