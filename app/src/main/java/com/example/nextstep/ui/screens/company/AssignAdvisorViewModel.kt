package com.example.nextstep.ui.screens.company

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.CompanyAssignAdvisorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AssignAdvisorViewModel : ViewModel() {

    private val repository = CompanyAssignAdvisorRepository()

    private val _uiState = MutableStateFlow(AssignAdvisorUiState())
    val uiState: StateFlow<AssignAdvisorUiState> = _uiState.asStateFlow()

    init {
        loadAdvisors()
    }

    fun loadAdvisors() {
        _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }
        viewModelScope.launch {
            repository.getActiveAdvisors()
                .onSuccess { advisors ->
                    _uiState.update { it.copy(advisors = advisors, isLoading = false) }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessageRes = R.string.error_loading_advisors)
                    }
                }
        }
    }

    fun onSearchChange(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
    }

    fun assignAdvisor(
        applicationId: String,
        advisorProfileId: String,
        onSuccess: () -> Unit
    ) {
        if (applicationId.isBlank()) return

        Log.d("AssignAdvisor", "applicationId=$applicationId")
        Log.d("AssignAdvisor", "advisorProfileId=$advisorProfileId")

        _uiState.value = _uiState.value.copy(
            isSaving = true,
            selectedAdvisorProfileId = advisorProfileId,
            errorMessageRes = null
        )

        viewModelScope.launch {
            val result = repository.assignAdvisor(
                applicationId = applicationId,
                advisorProfileId = advisorProfileId
            )

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    selectedAdvisorProfileId = null
                )
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    selectedAdvisorProfileId = null,
                    errorMessageRes = R.string.advisor_assign_error
                )
            }
        }
    }

    fun removeAdvisor(applicationId: String, onSuccess: () -> Unit) {
        if (applicationId.isBlank()) return

        _uiState.value = _uiState.value.copy(isSaving = true, errorMessageRes = null)
        viewModelScope.launch {
            val result = repository.removeAdvisor(applicationId)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isSaving = false)
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessageRes = R.string.advisor_assign_error
                )
            }
        }
    }
}
