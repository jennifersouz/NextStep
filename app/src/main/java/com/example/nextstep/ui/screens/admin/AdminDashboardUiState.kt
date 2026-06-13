package com.example.nextstep.ui.screens.admin

import com.example.nextstep.data.model.ProfileDto

data class AdminDashboardUiState(
    val isLoading: Boolean = false,
    val totalCompaniesCount: Int = 0,
    val activeCompaniesCount: Int = 0,
    val activeInternshipsCount: Int = 0,
    val applicationsCount: Int = 0,
    val completedEvaluationsCount: Int = 0,
    val usersCount: Int = 0,
    val adminName: String = "",
    val adminEmail: String = "",
    val recentProfiles: List<ProfileDto> = emptyList(),
    val errorMessage: String? = null
)
