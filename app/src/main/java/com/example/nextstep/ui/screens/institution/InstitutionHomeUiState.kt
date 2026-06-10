package com.example.nextstep.ui.screens.institution

import com.example.nextstep.data.model.InstitutionUserDto

data class InstitutionHomeUiState(
    val institutionName: String = "",
    val users: List<InstitutionUserDto> = emptyList(),
    val totalStudents: Int = 0,
    val totalTeachers: Int = 0,
    val pendingInvites: Int = 0,
    val acceptedInvites: Int = 0,
    val latestInvites: List<InstitutionUserDto> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessageRes: Int? = null
)