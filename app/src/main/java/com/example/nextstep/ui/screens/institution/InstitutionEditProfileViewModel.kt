package com.example.nextstep.ui.screens.institution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.InstitutionProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InstitutionEditProfileViewModel : ViewModel() {

    private val repository = InstitutionProfileRepository()

    private val _uiState = MutableStateFlow(InstitutionEditProfileUiState())
    val uiState: StateFlow<InstitutionEditProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = repository.getInstitutionProfile()

            _uiState.value = if (result.isSuccess) {
                val profile = result.getOrNull()

                _uiState.value.copy(
                    name = profile?.name.orEmpty(),
                    nif = profile?.nif.orEmpty(),
                    locality = profile?.locality.orEmpty(),
                    address = profile?.address.orEmpty(),
                    phone = profile?.phone.orEmpty(),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.institution_profile_load_error
                )
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            name = value,
            nameErrorRes = null,
            errorMessageRes = null,
            successMessageRes = null
        )
    }

    fun onNifChange(value: String) {
        _uiState.value = _uiState.value.copy(
            nif = value,
            nifErrorRes = null,
            errorMessageRes = null,
            successMessageRes = null
        )
    }

    fun onLocalityChange(value: String) {
        _uiState.value = _uiState.value.copy(
            locality = value,
            errorMessageRes = null,
            successMessageRes = null
        )
    }

    fun onAddressChange(value: String) {
        _uiState.value = _uiState.value.copy(
            address = value,
            errorMessageRes = null,
            successMessageRes = null
        )
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(
            phone = value,
            errorMessageRes = null,
            successMessageRes = null
        )
    }

    fun saveProfile(
        onSuccess: () -> Unit
    ) {
        val state = _uiState.value

        val nameError = if (state.name.isBlank()) {
            R.string.error_required_field
        } else {
            null
        }

        val nifError = if (
            state.nif.isNotBlank() &&
            !state.nif.matches(Regex("^\\d{9}$"))
        ) {
            R.string.error_nif_digits
        } else {
            null
        }

        if (nameError != null || nifError != null) {
            _uiState.value = state.copy(
                nameErrorRes = nameError,
                nifErrorRes = nifError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessageRes = null,
                successMessageRes = null
            )

            val result = repository.updateInstitutionProfile(
                name = state.name,
                nif = state.nif.takeIf { it.isNotBlank() },
                locality = state.locality.takeIf { it.isNotBlank() },
                address = state.address.takeIf { it.isNotBlank() },
                phone = state.phone.takeIf { it.isNotBlank() }
            )

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successMessageRes = R.string.institution_profile_update_success
                )

                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessageRes = R.string.institution_profile_update_error
                )
            }
        }
    }
}