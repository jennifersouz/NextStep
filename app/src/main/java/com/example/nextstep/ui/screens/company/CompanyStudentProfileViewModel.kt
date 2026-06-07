package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.CompanyStudentProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyStudentProfileViewModel : ViewModel() {

    private val repository = CompanyStudentProfileRepository()

    private val _uiState = MutableStateFlow(CompanyStudentProfileUiState())
    val uiState: StateFlow<CompanyStudentProfileUiState> = _uiState.asStateFlow()

    fun loadStudentProfile(applicationId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = repository.getStudentProfile(applicationId)

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    profile = result.getOrNull(),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.company_student_profile_load_error
                )
            }
        }
    }
}