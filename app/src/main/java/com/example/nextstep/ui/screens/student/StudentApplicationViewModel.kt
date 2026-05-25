package com.example.nextstep.ui.screens.student

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.ApplicationsRepository
import com.example.nextstep.data.repository.OffersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentApplicationViewModel : ViewModel() {

    private val offersRepository = OffersRepository()
    private val applicationsRepository = ApplicationsRepository()

    private val _uiState = MutableStateFlow(StudentApplicationUiState())
    val uiState: StateFlow<StudentApplicationUiState> = _uiState.asStateFlow()

    fun loadOffer(offerId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = offersRepository.getOfferById(offerId)

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    offer = result.getOrNull(),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    offer = null,
                    isLoading = false,
                    errorMessageRes = R.string.offer_detail_load_error
                )
            }
        }
    }

    fun onMotivationLetterSelected(uri: Uri, fileName: String) {
        _uiState.value = _uiState.value.copy(
            motivationLetterUri = uri,
            motivationLetterName = fileName,
            submitErrorRes = null,
            submitSuccessRes = null
        )
    }

    fun onCvSelected(uri: Uri, fileName: String) {
        _uiState.value = _uiState.value.copy(
            cvUri = uri,
            cvName = fileName,
            submitErrorRes = null,
            submitSuccessRes = null
        )
    }

    fun submitApplication(context: Context) {
        val state = _uiState.value
        val offer = state.offer ?: return

        val companyProfileId = offer.companyProfileId

        if (companyProfileId.isNullOrBlank()) {
            _uiState.value = state.copy(
                submitErrorRes = R.string.application_company_not_found
            )
            return
        }

        if (state.motivationLetterUri == null || state.cvUri == null) {
            _uiState.value = state.copy(
                submitErrorRes = R.string.application_documents_required
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSubmitting = true,
                submitErrorRes = null,
                submitSuccessRes = null
            )

            val motivationBytes = readUriBytes(context, state.motivationLetterUri)
            val cvBytes = readUriBytes(context, state.cvUri)

            if (motivationBytes == null || cvBytes == null) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    submitErrorRes = R.string.application_documents_read_error
                )
                return@launch
            }

            val result = applicationsRepository.submitApplication(
                offerId = offer.id,
                companyProfileId = companyProfileId,
                motivationLetterFileName = state.motivationLetterName,
                motivationLetterBytes = motivationBytes,
                cvFileName = state.cvName,
                cvBytes = cvBytes
            )

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isSubmitting = false,
                    submitSuccessRes = R.string.application_success,
                    submitErrorRes = null
                )
            } else {
                _uiState.value.copy(
                    isSubmitting = false,
                    submitErrorRes = mapApplicationError(result.exceptionOrNull()),
                    submitSuccessRes = null
                )
            }
        }
    }

    private fun readUriBytes(context: Context, uri: Uri): ByteArray? {
        return context.contentResolver.openInputStream(uri)?.use {
            it.readBytes()
        }
    }

    private fun mapApplicationError(exception: Throwable?): Int {
        val message = exception?.message.orEmpty().lowercase()

        return when {
            "already_applied" in message ||
                    "duplicate key" in message ||
                    "unique" in message ->
                R.string.application_already_exists

            "row-level security" in message ||
                    "violates row-level security" in message ||
                    "permission denied" in message ->
                R.string.application_permission_error

            "network" in message ||
                    "unable to resolve host" in message ||
                    "timeout" in message ->
                R.string.error_network

            else ->
                R.string.application_generic_error
        }
    }
}