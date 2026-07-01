package com.example.nextstep.ui.screens.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.model.AdminCompanyDto
import com.example.nextstep.data.repository.AdminCompaniesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminCompanyDetailViewModel : ViewModel() {

    private val repository = AdminCompaniesRepository()

    private val _uiState = MutableStateFlow(AdminCompanyDetailUiState())
    val uiState: StateFlow<AdminCompanyDetailUiState> = _uiState.asStateFlow()

    /**
     * Inicializa a empresa apenas se ainda não foi carregada (evita sobrescrever
     * dados mais recentes recarregados após deactivate/reactivate/archive).
     */
    fun setCompany(company: AdminCompanyDto) {
        if (_uiState.value.company?.id != company.id) {
            _uiState.value = _uiState.value.copy(company = company)
        }
    }

    /**
     * Força a substituição da empresa independentemente do estado atual.
     * Usado quando o utilizador navega para uma empresa diferente.
     */
    fun resetCompany(company: AdminCompanyDto) {
        _uiState.value = AdminCompanyDetailUiState(company = company)
    }

    fun loadCompany(companyId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = repository.getCompanyById(companyId)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    company = result.getOrNull()
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.error_failed_to_load_companies
                )
            }
        }
    }

    // Dialogs
    fun showDeactivateDialog() {
        _uiState.value = _uiState.value.copy(showDeactivateDialog = true)
    }

    fun showReactivateDialog() {
        _uiState.value = _uiState.value.copy(showReactivateDialog = true)
    }

    fun showArchiveDialog() {
        _uiState.value = _uiState.value.copy(showArchiveDialog = true)
    }

    fun dismissDialogs() {
        _uiState.value = _uiState.value.copy(
            showDeactivateDialog = false,
            showReactivateDialog = false,
            showArchiveDialog = false
        )
    }

    private suspend fun reloadCompany(companyId: String): AdminCompanyDto? {
        val result = repository.getCompanyById(companyId)
        return if (result.isSuccess) result.getOrNull() else null
    }

    // Actions
    fun deactivate() {
        val company = _uiState.value.company ?: return
        val profileId = company.profileId
        if (profileId.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessageRes = R.string.error_could_not_change_company_status
            )
            Log.e("AdminCompanyDetailVM", "deactivate: profileId está vazio para empresa id=${company.id}")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActionLoading = true, errorMessage = null, successMessage = null, successMessageRes = null)
            dismissDialogs()

            val result = repository.deactivateCompany(company.id, profileId)
            if (result.isSuccess) {
                val updated = reloadCompany(company.id)
                _uiState.value = _uiState.value.copy(
                    isActionLoading = false,
                    company = updated ?: _uiState.value.company,
                    successMessageRes = R.string.company_access_deactivated_success
                )
            } else {
                val ex = result.exceptionOrNull()
                Log.e("AdminCompanyDetailVM", "Erro ao desativar empresa id=${company.id}", ex)
                _uiState.value = _uiState.value.copy(
                    isActionLoading = false,
                    errorMessageRes = R.string.error_could_not_change_company_status
                )
            }
        }
    }

    fun reactivate() {
        val company = _uiState.value.company ?: return
        val profileId = company.profileId
        if (profileId.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessageRes = R.string.error_could_not_change_company_status
            )
            Log.e("AdminCompanyDetailVM", "reactivate: profileId está vazio para empresa id=${company.id}")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActionLoading = true, errorMessage = null, successMessage = null, successMessageRes = null)
            dismissDialogs()

            val result = repository.reactivateCompany(company.id, profileId)
            if (result.isSuccess) {
                val updated = reloadCompany(company.id)
                _uiState.value = _uiState.value.copy(
                    isActionLoading = false,
                    company = updated ?: _uiState.value.company,
                    successMessageRes = R.string.company_access_reactivated_success
                )
            } else {
                val ex = result.exceptionOrNull()
                Log.e("AdminCompanyDetailVM", "Erro ao reativar empresa id=${company.id}", ex)
                _uiState.value = _uiState.value.copy(
                    isActionLoading = false,
                    errorMessageRes = R.string.error_could_not_change_company_status
                )
            }
        }
    }

    fun archive(reason: String? = null) {
        val company = _uiState.value.company ?: return
        val profileId = company.profileId
        if (profileId.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessageRes = R.string.error_could_not_remove_company_from_platform
            )
            Log.e("AdminCompanyDetailVM", "archive: profileId está vazio para empresa id=${company.id}")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActionLoading = true, errorMessage = null, successMessage = null, successMessageRes = null)
            dismissDialogs()

            // adminId obtido internamente no Repository — não passar "" como UUID
            val result = repository.archiveCompany(
                companyId = company.id,
                companyProfileId = profileId,
                reason = reason
            )
            if (result.isSuccess) {
                val updated = reloadCompany(company.id)
                _uiState.value = _uiState.value.copy(
                    isActionLoading = false,
                    company = updated ?: _uiState.value.company,
                    successMessageRes = R.string.company_removed_success
                )
            } else {
                val ex = result.exceptionOrNull()
                Log.e("AdminCompanyDetailVM", "Erro ao arquivar empresa id=${company.id}", ex)
                _uiState.value = _uiState.value.copy(
                    isActionLoading = false,
                    errorMessageRes = R.string.error_could_not_remove_company_from_platform
                )
            }
        }
    }

    // Permanent delete and deletion impact were intentionally removed.
    // The app does not support permanent deletion.

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            successMessageRes = null,
            errorMessage = null,
            errorMessageRes = null
        )
    }
}