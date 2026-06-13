package com.example.nextstep.ui.screens.admin

import com.example.nextstep.data.model.AdminProfileDto

data class AdminUsersUiState(
    val isLoading: Boolean = false,
    val users: List<AdminProfileDto> = emptyList(),
    val filteredUsers: List<AdminProfileDto> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: AdminUsersFilter = AdminUsersFilter.ALL,
    val selectedUser: AdminProfileDto? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

enum class AdminUsersFilter(val labelRes: Int) {
    ALL(com.example.nextstep.R.string.filter_all),
    STUDENTS(com.example.nextstep.R.string.tab_students),
    TEACHERS(com.example.nextstep.R.string.tab_teachers),
    COMPANIES(com.example.nextstep.R.string.companies_label),
    ADMINS(com.example.nextstep.R.string.role_admin),
    ACTIVE(com.example.nextstep.R.string.filter_active),
    INACTIVE(com.example.nextstep.R.string.inactive_feminine),
    ARCHIVED(com.example.nextstep.R.string.filter_archived)
}