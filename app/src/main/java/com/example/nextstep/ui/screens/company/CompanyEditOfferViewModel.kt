package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.model.CompanyOfferUpdateDto
import com.example.nextstep.data.repository.CompanyOffersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyEditOfferViewModel : ViewModel() {

    private val repository = CompanyOffersRepository()

    private val _uiState = MutableStateFlow(CompanyEditOfferUiState())
    val uiState: StateFlow<CompanyEditOfferUiState> = _uiState.asStateFlow()

    private fun workModeToLabel(value: String?): String {
        return when (value?.trim()?.lowercase()) {
            "onsite", "presencial" -> "Presencial"
            "remote", "remoto" -> "Remoto"
            "hybrid", "híbrido", "hibrido" -> "Híbrido"
            else -> ""
        }
    }

    private fun labelToWorkMode(label: String): String {
        return when (label.trim().lowercase()) {
            "presencial" -> "onsite"
            "remoto" -> "remote"
            "híbrido", "hibrido" -> "hybrid"
            else -> label
        }
    }

    fun loadOffer(offerId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = repository.getOfferById(offerId)

            _uiState.value = if (result.isSuccess) {
                val offer = result.getOrNull()
                val areaString = offer?.area.orEmpty()

                _uiState.value.copy(
                    offerId = offerId,
                    title = offer?.title.orEmpty(),
                    description = offer?.description.orEmpty(),
                    selectedArea = OfferArea.entries.firstOrNull { it.dbValue.equals(areaString, ignoreCase = true) },
                    location = offer?.location.orEmpty(),
                    workMode = workModeToLabel(offer?.workMode),
                    duration = offer?.duration.orEmpty(),
                    vacancies = offer?.vacancies?.toString().orEmpty(),
                    requirements = offer?.requirements.orEmpty(),
                    isActive = offer?.isActive ?: false,
                    isLoading = false,
                    errorMessage = null
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
        _uiState.value = _uiState.value.copy(title = value, titleError = null)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value, descriptionError = null)
    }

    fun onAreaChange(area: OfferArea) {
        _uiState.value = _uiState.value.copy(selectedArea = area, areaError = null)
    }

    fun onLocationChange(value: String) {
        _uiState.value = _uiState.value.copy(location = value, locationError = null)
    }

    fun onWorkModeChange(value: String) {
        _uiState.value = _uiState.value.copy(workMode = value, workModeError = null)
    }

    fun onDurationChange(value: String) {
        _uiState.value = _uiState.value.copy(duration = value, durationError = null)
    }

    fun onVacanciesChange(value: String) {
        if (value.isEmpty() || value.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(
                vacancies = value,
                vacanciesError = null
            )
        }
    }

    fun onRequirementsChange(value: String) {
        _uiState.value = _uiState.value.copy(requirements = value)
    }

    fun onActiveChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(isActive = value)
    }

    private fun validate(): Boolean {
        val state = _uiState.value
        var hasError = false

        val titleError = if (state.title.isBlank()) {
            hasError = true
            R.string.error_offer_title_required
        } else null

        val descriptionError = if (state.description.isBlank()) {
            hasError = true
            R.string.error_offer_description_required
        } else null

        val areaError = if (state.selectedArea == null) {
            hasError = true
            R.string.error_offer_area_required
        } else null

        val locationError = if (state.location.isBlank()) {
            hasError = true
            R.string.error_offer_location_required
        } else null

        val workModeError = if (state.workMode.isBlank()) {
            hasError = true
            R.string.error_offer_work_mode_required
        } else null

        val durationError = if (state.duration.isBlank()) {
            hasError = true
            R.string.error_offer_duration_required
        } else null

        val vacanciesError = when {
            state.vacancies.isBlank() -> {
                hasError = true
                R.string.error_offer_vacancies_required
            }
            state.vacancies.toIntOrNull() == null -> {
                hasError = true
                R.string.error_offer_vacancies_invalid
            }
            state.vacancies.toInt() <= 0 -> {
                hasError = true
                R.string.error_offer_vacancies_positive
            }
            else -> null
        }

        _uiState.value = state.copy(
            titleError = titleError,
            descriptionError = descriptionError,
            areaError = areaError,
            locationError = locationError,
            workModeError = workModeError,
            durationError = durationError,
            vacanciesError = vacanciesError
        )

        return !hasError
    }

    fun saveOffer() {
        val state = _uiState.value

        if (!validate()) return

        val validVacancies = state.vacancies.toInt()

        val request = CompanyOfferUpdateDto(
            title = state.title.trim(),
            description = state.description.trim(),
            area = state.selectedArea!!.dbValue,
            location = state.location.trim(),
            workMode = labelToWorkMode(state.workMode),
            duration = state.duration.trim(),
            vacancies = validVacancies,
            requirements = state.requirements.trim(),
            isActive = state.isActive
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessage = null,
                successMessage = null
            )

            val result = repository.updateOffer(
                offerId = state.offerId,
                request = request
            )

            if (result.isSuccess) {
                val updatedOffer = result.getOrNull()
                val updatedArea = updatedOffer?.area.orEmpty()
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successMessageRes = R.string.company_offer_update_success,
                    title = updatedOffer?.title.orEmpty(),
                    description = updatedOffer?.description.orEmpty(),
                    selectedArea = OfferArea.entries.firstOrNull { it.dbValue.equals(updatedArea, ignoreCase = true) },
                    location = updatedOffer?.location.orEmpty(),
                    workMode = workModeToLabel(updatedOffer?.workMode),
                    duration = updatedOffer?.duration.orEmpty(),
                    vacancies = updatedOffer?.vacancies?.toString().orEmpty(),
                    requirements = updatedOffer?.requirements.orEmpty(),
                    isActive = updatedOffer?.isActive ?: true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessageRes = R.string.company_offer_update_error
                )
            }
        }
    }
}