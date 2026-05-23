package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.OffersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateOfferViewModel : ViewModel() {

    private val offersRepository = OffersRepository()

    private val _uiState = MutableStateFlow(CreateOfferUiState())
    val uiState: StateFlow<CreateOfferUiState> = _uiState.asStateFlow()

    private fun mapCreateOfferError(exception: Throwable?): Int {
        val message = exception?.message.orEmpty().lowercase()

        return when {
            "row-level security" in message ||
            "violates row-level security" in message ||
            "permission denied" in message ->
                R.string.create_offer_error_permission

            "companies" in message ||
            "no rows" in message ||
            "perfil_empresa_nao_encontrado" in message ->
                R.string.create_offer_error_company_profile

            "not null" in message ||
            "null value" in message ->
                R.string.create_offer_error_missing_database_field

            "network" in message ||
            "unable to resolve host" in message ||
            "timeout" in message ->
                R.string.error_network

            "utilizador não autenticado" in message ->
                R.string.create_offer_error_unauthenticated

            else ->
                R.string.create_offer_error
        }
    }

    fun onTitleChange(value: String) {
        _uiState.value = _uiState.value.copy(
            title = value,
            titleError = validateRequiredText(value),
            generalError = null,
            successMessage = null
        )
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(
            description = value,
            descriptionError = validateRequiredText(value),
            generalError = null,
            successMessage = null
        )
    }

    fun onAreaChange(area: OfferArea) {
        _uiState.value = _uiState.value.copy(
            selectedArea = area,
            areaError = null,
            generalError = null,
            successMessage = null
        )
    }

    fun onLocationChange(value: String) {
        _uiState.value = _uiState.value.copy(
            location = value,
            locationError = validateRequiredText(value),
            generalError = null,
            successMessage = null
        )
    }

    fun onWorkModeChange(workMode: WorkMode) {
        _uiState.value = _uiState.value.copy(
            selectedWorkMode = workMode,
            workModeError = null,
            generalError = null,
            successMessage = null
        )
    }

    fun onDurationChange(value: String) {
        _uiState.value = _uiState.value.copy(
            duration = value,
            durationError = validateRequiredText(value),
            generalError = null,
            successMessage = null
        )
    }

    fun onVacanciesChange(value: String) {
        val filteredValue = value.filter { it.isDigit() }.take(2)

        _uiState.value = _uiState.value.copy(
            vacancies = filteredValue,
            vacanciesError = validateVacancies(filteredValue),
            generalError = null,
            successMessage = null
        )
    }

    fun onRequirementsChange(value: String) {
        _uiState.value = _uiState.value.copy(
            requirements = value,
            requirementsError = validateRequiredText(value),
            generalError = null,
            successMessage = null
        )
    }

    fun createOffer(onSuccess: () -> Unit = {}) {
        if (!validateForm()) return

        val state = _uiState.value

        viewModelScope.launch {
            _uiState.value = state.copy(
                isLoading = true,
                generalError = null,
                successMessage = null
            )

            val result = offersRepository.createOffer(
                title = state.title.trim(),
                description = state.description.trim(),
                area = state.selectedArea!!.dbValue,
                location = state.location.trim(),
                workMode = state.selectedWorkMode!!.dbValue,
                duration = state.duration.trim(),
                vacancies = state.vacancies.toInt(),
                requirements = state.requirements.trim()
            )

            if (result.isSuccess) {
                _uiState.value = CreateOfferUiState(
                    successMessage = R.string.create_offer_success
                )
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    generalError = mapCreateOfferError(result.exceptionOrNull())
                )
            }
        }
    }

    private fun validateForm(): Boolean {
        val state = _uiState.value

        val titleError = validateRequiredText(state.title)
        val descriptionError = validateRequiredText(state.description)
        val areaError = if (state.selectedArea == null) R.string.error_required_field else null
        val locationError = validateRequiredText(state.location)
        val workModeError = if (state.selectedWorkMode == null) R.string.error_required_field else null
        val durationError = validateRequiredText(state.duration)
        val vacanciesError = validateVacancies(state.vacancies)
        val requirementsError = validateRequiredText(state.requirements)

        val hasErrors = listOf(
            titleError,
            descriptionError,
            areaError,
            locationError,
            workModeError,
            durationError,
            vacanciesError,
            requirementsError
        ).any { it != null }

        _uiState.value = state.copy(
            titleError = titleError,
            descriptionError = descriptionError,
            areaError = areaError,
            locationError = locationError,
            workModeError = workModeError,
            durationError = durationError,
            vacanciesError = vacanciesError,
            requirementsError = requirementsError,
            generalError = if (hasErrors) R.string.form_has_errors else null,
            successMessage = null
        )

        return !hasErrors
    }

    private fun validateRequiredText(value: String): Int? {
        return if (value.isBlank()) R.string.error_required_field else null
    }

    private fun validateVacancies(value: String): Int? {
        val number = value.toIntOrNull()

        return when {
            value.isBlank() -> R.string.error_required_field
            number == null -> R.string.error_vacancies_number
            number < 1 -> R.string.error_vacancies_min
            number > 99 -> R.string.error_vacancies_max
            else -> null
        }
    }
}