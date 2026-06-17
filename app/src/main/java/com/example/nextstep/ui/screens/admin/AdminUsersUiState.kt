package com.example.nextstep.ui.screens.admin

import com.example.nextstep.data.model.AdminProfileDto

data class AdminUsersUiState(
    val isLoading: Boolean = false,
    val users: List<AdminProfileDto> = emptyList(),
    val filteredUsers: List<AdminProfileDto> = emptyList(),
    val searchQuery: String = "",
    val selectedTypeFilter: String = "Todos",
    val selectedStatusFilter: String = "Todos",
    val selectedUser: AdminProfileDto? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)