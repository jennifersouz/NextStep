package com.example.nextstep.ui.screens.company

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.CompanyApplicationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyApplicationsViewModel : ViewModel() {

    private val repository = CompanyApplicationsRepository()

    private val _uiState = MutableStateFlow(CompanyApplicationsUiState())
    val uiState: StateFlow<CompanyApplicationsUiState> = _uiState.asStateFlow()

    init {
        loadApplications()
    }

    fun selectFilter(filter: CompanyApplicationFilter) {
        _uiState.value = _uiState.value.copy(
            selectedFilter = filter
        )
    }

    fun loadApplications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = repository.getCompanyApplications()

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    applications = result.getOrDefault(emptyList()),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    applications = emptyList(),
                    isLoading = false,
                    errorMessageRes = R.string.company_applications_load_error
                )
            }
        }
    }

    fun markAsViewed(applicationId: String) {
        viewModelScope.launch {
            Log.d("MARK_VIEWED_DEBUG", "ViewModel markAsViewed chamado id=$applicationId")

            val result = repository.markApplicationAsViewed(applicationId)

            if (result.isSuccess) {
                Log.d("MARK_VIEWED_DEBUG", "markAsViewed success id=$applicationId")

                _uiState.value = _uiState.value.copy(
                    applications = _uiState.value.applications.map { application ->
                        if (application.id == applicationId) {
                            application.copy(viewedByCompany = true)
                        } else {
                            application
                        }
                    }
                )

                loadApplications()
            } else {
                Log.e(
                    "MARK_VIEWED_DEBUG",
                    "markAsViewed failure",
                    result.exceptionOrNull()
                )

                _uiState.value = _uiState.value.copy(
                    errorMessageRes = R.string.company_application_mark_viewed_error
                )
            }
        }
    }
}