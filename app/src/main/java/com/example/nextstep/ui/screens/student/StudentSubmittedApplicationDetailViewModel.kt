package com.example.nextstep.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.StudentApplicationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentSubmittedApplicationDetailViewModel : ViewModel() {

    private val repository = StudentApplicationsRepository()

    private val _uiState = MutableStateFlow(StudentSubmittedApplicationDetailUiState())
    val uiState: StateFlow<StudentSubmittedApplicationDetailUiState> = _uiState.asStateFlow()

    fun loadApplication(applicationId: String) {
        if (applicationId.isBlank()) {
            _uiState.value = StudentSubmittedApplicationDetailUiState(
                isLoading = false,
                errorMessageRes = R.string.student_application_detail_not_found
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null,
                confirmPresenceErrorRes = null,
                documentErrorRes = null
            )

            val result = repository.getSubmittedApplicationById(applicationId)

            _uiState.value = if (result.isSuccess) {
                StudentSubmittedApplicationDetailUiState(
                    application = result.getOrNull(),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                StudentSubmittedApplicationDetailUiState(
                    application = null,
                    isLoading = false,
                    errorMessageRes = R.string.student_application_detail_load_error
                )
            }
        }
    }

    fun confirmPresence() {
        val application = _uiState.value.application ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isConfirmingPresence = true,
                confirmPresenceErrorRes = null
            )

            val result = repository.confirmInternshipAcceptance(application.id)

            if (result.isSuccess) {
                loadApplication(application.id)
            } else {
                _uiState.value = _uiState.value.copy(
                    isConfirmingPresence = false,
                    confirmPresenceErrorRes = R.string.accept_internship_error
                )
            }
        }
    }

    fun openMotivationLetter() {
        val path = _uiState.value.application?.motivationLetterPath

        if (path.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                documentErrorRes = R.string.student_application_document_missing
            )
            return
        }

        createSignedUrl(path)
    }

    fun openCv() {
        val path = _uiState.value.application?.cvPath

        if (path.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                documentErrorRes = R.string.student_application_document_missing
            )
            return
        }

        createSignedUrl(path)
    }

    fun consumeDocumentUrl() {
        _uiState.value = _uiState.value.copy(
            documentUrlToOpen = null
        )
    }

    fun onDocumentOpenFailed() {
        _uiState.value = _uiState.value.copy(
            documentUrlToOpen = null,
            documentErrorRes = R.string.student_application_document_open_error
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
                    documentErrorRes = R.string.student_application_document_open_error
                )
            }
        }
    }
}