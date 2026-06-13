package com.example.nextstep.ui.screens.teacher

import com.example.nextstep.data.model.TeacherStudentDto

data class TeacherStudentsUiState(
    val isLoading: Boolean = false,
    val students: List<TeacherStudentDto> = emptyList(),
    val selectedFilter: TeacherStudentsFilter = TeacherStudentsFilter.ALL,
    val searchQuery: String = "",
    val errorMessage: String? = null
)