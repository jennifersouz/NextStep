package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.CompanyProfileRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyOwnProfileViewModel : ViewModel() {

    private val repository = CompanyProfileRepository()

    private val _uiState = MutableStateFlow(CompanyOwnProfileUiState())
    val uiState: StateFlow<CompanyOwnProfileUiState> = _uiState.asStateFlow()

    init {
        loadCompanyProfile()
    }

    fun loadCompanyProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val companyResultDeferred = async {
                repository.getCurrentCompanyProfile()
            }

            val offersResultDeferred = async {
                repository.getCurrentCompanyOffers()
            }

            val companyResult = companyResultDeferred.await()
            val offersResult = offersResultDeferred.await()

            if (companyResult.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    company = companyResult.getOrNull(),
                    offers = offersResult.getOrDefault(emptyList()),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.company_profile_load_error
                )
            }
        }
    }
}