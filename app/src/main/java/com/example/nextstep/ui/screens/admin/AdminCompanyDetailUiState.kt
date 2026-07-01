package com.example.nextstep.ui.screens.admin

import com.example.nextstep.data.model.AdminCompanyDto

data class AdminCompanyDetailUiState(
    val isLoading: Boolean = false,
    val isActionLoading: Boolean = false,
    val company: AdminCompanyDto? = null,
    val errorMessage: String? = null,
    val errorMessageRes: Int? = null,
    val successMessage: String? = null,
    val successMessageRes: Int? = null,
    val showDeactivateDialog: Boolean = false,
    val showReactivateDialog: Boolean = false,
    val showArchiveDialog: Boolean = false
)
