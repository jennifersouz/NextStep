package com.example.nextstep.ui.screens.admin

import com.example.nextstep.data.model.AdminProfileDto

data class AdminUserDetailUiState(
    val isLoading: Boolean = false,
    val isActionLoading: Boolean = false,
    val profile: AdminProfileDto? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showDeactivateDialog: Boolean = false,
    val showReactivateDialog: Boolean = false,
    val showArchiveDialog: Boolean = false
)
