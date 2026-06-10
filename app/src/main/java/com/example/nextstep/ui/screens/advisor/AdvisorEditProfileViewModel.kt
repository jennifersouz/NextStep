package com.example.nextstep.ui.screens.advisor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.AdvisorProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdvisorEditProfileUiState(
    val name: String = "",
    val phone: String = "",
    val department: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val nameError: String? = null,
    @androidx.annotation.StringRes val errorMessageRes: Int? = null,
    @androidx.annotation.StringRes val successMessageRes: Int? = null
)

class AdvisorEditProfileViewModel : ViewModel() {

    private val repository = AdvisorProfileRepository()

    private val _uiState = MutableStateFlow(AdvisorEditProfileUiState())
    val uiState: StateFlow<AdvisorEditProfileUiState> = _uiState.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.getAdvisorProfile()

            if (result.isSuccess) {
                val profile = result.getOrNull()
                _uiState.value = _uiState.value.copy(
                    name = profile?.name.orEmpty(),
                    phone = profile?.phone.orEmpty(),
                    department = profile?.department.orEmpty(),
                    email = profile?.email.orEmpty(),
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.advisor_profile_load_error
                )
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(
            name = name,
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

            val result = repository.updateAdvisorProfile(
                name = state.name,
                phone = state.phone,
                department = state.department
            )

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successMessageRes = R.string.advisor_profile_update_success
                )
                onSuccess()
            } else {
                val errorMessage = result.exceptionOrNull()?.message.orEmpty()

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessageRes = if (errorMessage.contains("row-level security") ||
                        errorMessage.contains("permission") ||
                        errorMessage.contains("policy")
                    ) {
                        R.string.advisor_profile_update_error
                    } else {
                        R.string.advisor_profile_update_error
                    }
                )
            }
        }
    }
}