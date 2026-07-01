package com.example.nextstep.ui.screens.admin

data class AdminDashboardUiState(
    val isLoading: Boolean = false,
    val totalCompaniesCount: Int = 0,
    val activeCompaniesCount: Int = 0,
    val activeInternshipsCount: Int = 0,
    val applicationsCount: Int = 0,
    val publishedOffersCount: Int = 0,
    val usersCount: Int = 0,
    val adminName: String = "",
    val adminEmail: String = "",
    val recentActivities: List<RecentActivityUiModel> = emptyList(),
    val recentActivitiesLoading: Boolean = false,
    val errorMessage: String? = null
)
