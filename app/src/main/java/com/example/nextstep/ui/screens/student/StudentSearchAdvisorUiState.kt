package com.example.nextstep.ui.screens.student

import androidx.annotation.StringRes
import com.example.nextstep.data.model.TeacherDto

data class StudentSearchAdvisorUiState(
    val isLoading: Boolean = false,
    val teachers: List<TeacherDto> = emptyList(),
    val searchQuery: String = "",
    val isSendingRequest: Boolean = false,
    val sendingTeacherId: String? = null,
    @StringRes val errorMessageRes: Int? = null,
    val isRequestSent: Boolean = false
) {
    val filteredTeachers: List<TeacherDto>
        get() = if (searchQuery.isBlank()) {
            teachers
        } else {
            teachers.filter {
                (it.firstName ?: "").contains(searchQuery, ignoreCase = true) ||
                (it.lastName ?: "").contains(searchQuery, ignoreCase = true) ||
                (it.name ?: "").contains(searchQuery, ignoreCase = true) ||
                (it.email ?: "").contains(searchQuery, ignoreCase = true) ||
                (it.department ?: "").contains(searchQuery, ignoreCase = true)
            }
        }
}
