package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun loadOffer(offerId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = repository.getOfferById(offerId)

            _uiState.value = if (result.isSuccess) {
                val offer = result.getOrNull()

                _uiState.value.copy(
                    offerId = offerId,
                    title = offer?.title.orEmpty(),
                    description = offer?.description.orEmpty(),
                    area = offer?.area.orEmpty(),
                    location = offer?.location.orEmpty(),
                    workMode = offer?.workMode.orEmpty(),
                    duration = offer?.duration.orEmpty(),
                    vacancies = offer?.vacancies?.toString().orEmpty(),
                    requirements = offer?.requirements.orEmpty(),
                    isActive = offer?.isActive ?: true,
                    isLoading = false,
                    errorMessage = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Não foi possível carregar a oferta."
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

    fun onAreaChange(value: String) {
        _uiState.value = _uiState.value.copy(area = value, areaError = null)
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
            "Título obrigatório."
        } else null

        val descriptionError = if (state.description.isBlank()) {
            hasError = true
            "Descrição obrigatória."
        } else null

        val areaError = if (state.area.isBlank()) {
            hasError = true
            "Área obrigatória."
        } else null

        val locationError = if (state.location.isBlank()) {
            hasError = true
            "Localização obrigatória."
        } else null

        val workModeError = if (state.workMode.isBlank()) {
            hasError = true
            "Regime obrigatório."
        } else null

        val durationError = if (state.duration.isBlank()) {
            hasError = true
            "Duração obrigatória."
        } else null

        val vacanciesError = when {
            state.vacancies.isBlank() -> {
                hasError = true
                "Número de vagas obrigatório."
            }
            state.vacancies.toIntOrNull() == null -> {
                hasError = true
                "Insere um número de vagas válido."
            }
            state.vacancies.toInt() <= 0 -> {
                hasError = true
                "O número de vagas deve ser superior a zero."
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
            area = state.area.trim(),
            location = state.location.trim(),
            workMode = state.workMode.trim(),
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
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successMessage = "Oferta atualizada com sucesso.",
                    title = updatedOffer?.title.orEmpty(),
                    description = updatedOffer?.description.orEmpty(),
                    area = updatedOffer?.area.orEmpty(),
                    location = updatedOffer?.location.orEmpty(),
                    workMode = updatedOffer?.workMode.orEmpty(),
                    duration = updatedOffer?.duration.orEmpty(),
                    vacancies = updatedOffer?.vacancies?.toString().orEmpty(),
                    requirements = updatedOffer?.requirements.orEmpty(),
                    isActive = updatedOffer?.isActive ?: true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = "Não foi possível atualizar a oferta."
                )
            }
        }
    }
}