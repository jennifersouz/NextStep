package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.CompanyProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyEditProfileViewModel : ViewModel() {

    private val repository = CompanyProfileRepository()

    private val _uiState = MutableStateFlow(CompanyEditProfileUiState())
    val uiState: StateFlow<CompanyEditProfileUiState> = _uiState.asStateFlow()

    init {
        loadCompanyProfile()
    }

    fun loadCompanyProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = repository.getCurrentCompanyProfile()

            _uiState.value = if (result.isSuccess) {
                val company = result.getOrNull()

                _uiState.value.copy(
                    companyName = company?.companyName.orEmpty(),
                    businessArea = company?.businessArea.orEmpty(),
                    location = company?.location.orEmpty(),
                    description = company?.description.orEmpty(),
                    phone = company?.phone.orEmpty(),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.company_profile_load_error
                )
            }
        }
    }

    fun onCompanyNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            companyName = value,
            companyNameErrorRes = null,
            errorMessageRes = null,
            successMessageRes = null
        )
    }

    fun onBusinessAreaChange(value: String) {
        _uiState.value = _uiState.value.copy(
            businessArea = value,
            errorMessageRes = null,
            successMessageRes = null
        )
    }

    fun onLocationChange(value: String) {
        _uiState.value = _uiState.value.copy(
            location = value,
            errorMessageRes = null,
            successMessageRes = null
        )
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(
            description = value,
            errorMessageRes = null,
            successMessageRes = null
        )
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(
            phone = value,
            phoneErrorRes = null,
            errorMessageRes = null,
            successMessageRes = null
        )
    }

    fun saveProfile(
        onSuccess: () -> Unit
    ) {
        val state = _uiState.value

        val companyNameError = if (state.companyName.isBlank()) {
            R.string.error_required_field
        } else {
            null
        }

        val phoneError = if (
            state.phone.isNotBlank() &&
            !state.phone.matches(Regex("^[0-9 +()\\-]{6,20}$"))
        ) {
            R.string.company_profile_invalid_phone
        } else {
            null
        }

        if (companyNameError != null || phoneError != null) {
            _uiState.value = state.copy(
                companyNameErrorRes = companyNameError,
                phoneErrorRes = phoneError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessageRes = null,
                successMessageRes = null
            )

            val result = repository.updateCurrentCompanyProfile(
                companyName = state.companyName,
                businessArea = state.businessArea,
                location = state.location,
                description = state.description,
                phone = state.phone
            )

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successMessageRes = R.string.company_profile_update_success
                )

                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessageRes = R.string.company_profile_update_error
                )
            }
        }
    }
}