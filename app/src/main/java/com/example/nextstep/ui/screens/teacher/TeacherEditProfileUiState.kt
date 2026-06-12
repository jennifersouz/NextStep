package com.example.nextstep.ui.screens.teacher

import androidx.annotation.StringRes

data class TeacherEditProfileUiState(
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val department: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val nameError: String? = null,
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val successMessageRes: Int? = null
) {
    /** Combined display name for backwards compatibility */
    val name: String get() = "${firstName} ${lastName}".trim()
}