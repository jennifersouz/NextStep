package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.CompanyOffersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyEditOfferViewModel : ViewModel() {

    private val repository = CompanyOffersRepository()

    private val _uiState = MutableStateFlow(CompanyEditOfferUiState())
    val uiState: StateFlow<CompanyEditOfferUiState> = _uiState.asStateFlow()

    fun loadOffer(offerId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = repository.getCompanyOfferById(offerId)

            _uiState.value = if (result.isSuccess) {
                val offer = result.getOrNull()

                _uiState.value.copy(
                    title = offer?.title.orEmpty(),
                    description = offer?.description.orEmpty(),
                    area = offer?.area.orEmpty(),
                    location = offer?.location.orEmpty(),
                    workMode = offer?.workMode.orEmpty(),
                    duration = offer?.duration.orEmpty(),
                    vacancies = offer?.vacancies?.toString().orEmpty(),
                    requirements = offer?.requirements.orEmpty(),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.company_offer_load_error
                )
            }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.value = _uiState.value.copy(title = value, titleErrorRes = null)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun onAreaChange(value: String) {
        _uiState.value = _uiState.value.copy(area = value)
    }

    fun onLocationChange(value: String) {
        _uiState.value = _uiState.value.copy(location = value)
    }

    fun onWorkModeChange(value: String) {
        _uiState.value = _uiState.value.copy(workMode = value)
    }

    fun onDurationChange(value: String) {
        _uiState.value = _uiState.value.copy(duration = value)
    }

    fun onVacanciesChange(value: String) {
        if (value.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(
                vacancies = value,
                vacanciesErrorRes = null
            )
        }
    }

    fun onRequirementsChange(value: String) {
        _uiState.value = _uiState.value.copy(requirements = value)
    }

    fun saveOffer(
        offerId: String,
        onSuccess: () -> Unit
    ) {
        val state = _uiState.value

        val titleError = if (state.title.isBlank()) {
            R.string.error_required_field
        } else {
            null
        }

        val vacanciesInt = state.vacancies.toIntOrNull()

        val vacanciesError = if (vacanciesInt == null || vacanciesInt <= 0) {
            R.string.company_offer_invalid_vacancies
        } else {
            null
        }

        if (titleError != null || vacanciesError != null) {
            _uiState.value = state.copy(
                titleErrorRes = titleError,
                vacanciesErrorRes = vacanciesError
            )
            return
        }

        val validVacancies: Int = state.vacancies.toInt()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessageRes = null
            )

            val result = repository.updateOffer(
                offerId = offerId,
                title = state.title,
                description = state.description,
                area = state.area,
                location = state.location,
                workMode = state.workMode,
                duration = state.duration,
                vacancies = validVacancies,
                requirements = state.requirements
            )

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isSaving = false)
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessageRes = R.string.company_offer_update_error
                )
            }
        }
    }
}