package com.example.nextstep.ui.screens.admin

import com.example.nextstep.data.model.ProfileDto

data class AdminUsersUiState(
    val isLoading: Boolean = false,
    val users: List<ProfileDto> = emptyList(),
    val filteredUsers: List<ProfileDto> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: AdminUsersFilter = AdminUsersFilter.ALL,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

enum class AdminUsersFilter(val label: String) {
    ALL("Todos"),
    STUDENTS("Alunos"),
    TEACHERS("Docentes"),
    COMPANIES("Empresas"),
    ADMINS("Admins"),
    ACTIVE("Ativos"),
    INACTIVE("Inativos")
}