package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.CompanyInternStudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyInternStudentProfileViewModel : ViewModel() {

    private val repository = CompanyInternStudentRepository()

    private val _uiState = MutableStateFlow(CompanyInternStudentProfileUiState())
    val uiState: StateFlow<CompanyInternStudentProfileUiState> = _uiState.asStateFlow()

    fun loadProfile(applicationId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = repository.getInternStudentProfile(applicationId)

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    profile = result.getOrNull(),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                val errorMsg = result.exceptionOrNull()?.message.orEmpty()
                val errorRes = when {
                    errorMsg.contains("PERMISSION_DENIED", ignoreCase = true) ->
                        R.string.company_intern_profile_permission_denied
                    errorMsg.contains("NOT_IN_INTERNSHIP", ignoreCase = true) ->
                        R.string.company_intern_profile_not_in_internship
                    errorMsg.contains("APPLICATION_ID_EMPTY", ignoreCase = true) ->
                        R.string.company_intern_profile_load_error
                    errorMsg.contains("EMPLOYER_NOT_AUTHENTICATED", ignoreCase = true) ->
                        R.string.company_intern_profile_load_error
                    else ->
                        R.string.company_intern_profile_load_error
                }
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = errorRes
                )
            }
        }
    }

    fun openCv() {
        val path = _uiState.value.profile?.cvPath
        if (path.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                documentErrorRes = R.string.company_application_document_missing
            )
            return
        }
        createSignedUrl(path)
    }

    fun openMotivationLetter() {
        val path = _uiState.value.profile?.motivationLetterPath
        if (path.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                documentErrorRes = R.string.company_application_document_missing
            )
            return
        }
        createSignedUrl(path)
    }

    fun consumeDocumentUrl() {
        _uiState.value = _uiState.value.copy(documentUrlToOpen = null)
    }

    fun onDocumentOpenFailed() {
        _uiState.value = _uiState.value.copy(
            documentUrlToOpen = null,
            documentErrorRes = R.string.company_application_document_open_error
        )
    }

    private fun createSignedUrl(path: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isOpeningDocument = true,
                documentErrorRes = null,
                documentUrlToOpen = null
            )

            val result = repository.createSignedDocumentUrl(path)

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isOpeningDocument = false,
                    documentUrlToOpen = result.getOrNull(),
                    documentErrorRes = null
                )
            } else {
                _uiState.value.copy(
                    isOpeningDocument = false,
                    documentUrlToOpen = null,
                    documentErrorRes = R.string.company_application_document_open_error
                )
            }
        }
    }
}