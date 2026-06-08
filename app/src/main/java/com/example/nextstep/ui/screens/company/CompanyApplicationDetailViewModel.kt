package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.CompanyApplicationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyApplicationDetailViewModel : ViewModel() {

    private val repository = CompanyApplicationsRepository()

    private val _uiState = MutableStateFlow(CompanyApplicationDetailUiState())
    val uiState: StateFlow<CompanyApplicationDetailUiState> = _uiState.asStateFlow()

    fun loadApplication(
        applicationId: String,
        showLoading: Boolean = true
    ) {
        if (applicationId.isBlank()) {
            _uiState.value = CompanyApplicationDetailUiState(
                isLoading = false,
                errorMessageRes = R.string.company_application_detail_not_found
            )
            return
        }

        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessageRes = null,
                    statusErrorRes = null,
                    documentErrorRes = null
                )
            }

            val result = repository.getCompanyApplicationById(applicationId)

            if (result.isSuccess) {
                val application = result.getOrNull()

                if (showLoading) {
                    _uiState.value = CompanyApplicationDetailUiState(
                        application = application,
                        isLoading = false,
                        errorMessageRes = null
                    )

                    if (application != null && !application.viewedByCompany) {
                        val markResult = repository.markApplicationAsViewed(application.id)

                        if (markResult.isSuccess) {
                            _uiState.value = _uiState.value.copy(
                                application = application.copy(
                                    viewedByCompany = true
                                )
                            )
                        }
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        application = application,
                        errorMessageRes = null
                    )
                }
            } else if (showLoading) {
                _uiState.value = CompanyApplicationDetailUiState(
                    application = null,
                    isLoading = false,
                    errorMessageRes = R.string.company_application_detail_load_error
                )
            }
        }
    }

    fun updateStatus(status: ApplicationDecisionStatus) {
        val application = _uiState.value.application ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUpdatingStatus = true,
                statusErrorRes = null
            )

            val result = repository.updateApplicationStatus(
                applicationId = application.id,
                status = status.dbValue
            )

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    application = application.copy(
                        status = status.dbValue
                    ),
                    isUpdatingStatus = false,
                    statusErrorRes = null
                )
            } else {
                _uiState.value.copy(
                    isUpdatingStatus = false,
                    statusErrorRes = R.string.company_application_status_update_error
                )
            }
        }
    }

    fun openMotivationLetter() {
        val path = _uiState.value.application?.motivationLetterPath

        if (path.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                documentErrorRes = R.string.company_application_document_missing
            )
            return
        }

        createSignedUrl(path)
    }

    fun openCv() {
        val path = _uiState.value.application?.cvPath

        if (path.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                documentErrorRes = R.string.company_application_document_missing
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