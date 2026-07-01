package com.example.nextstep.ui.screens.admin

import com.example.nextstep.data.model.AdminProfileDto

data class AdminUsersUiState(
    val isLoading: Boolean = false,
    val users: List<AdminProfileDto> = emptyList(),
    val filteredUsers: List<AdminProfileDto> = emptyList(),
    val searchQuery: String = "",
    val selectedTypeFilter: UserTypeFilter = UserTypeFilter.ALL,
    val selectedStatusFilter: UserStatusFilter = UserStatusFilter.ALL,
    val selectedUser: AdminProfileDto? = null,
    val errorMessage: String? = null,
    val errorMessageRes: Int? = null,
    val successMessage: String? = null,
    val successMessageRes: Int? = null
)
