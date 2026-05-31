package com.example.nextstep.ui.screens.auth

import androidx.annotation.StringRes

data class RegisterUiState(
    val selectedRole: UserRole = UserRole.STUDENT,

    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    val studentNumber: String = "",
    val course: String = "",
    val year: String = "",

    val companyName: String = "",
    val nif: String = "",
    val area: String = "",
    val location: String = "",

    @StringRes val nameError: Int? = null,
    @StringRes val lastNameError: Int? = null,
    @StringRes val emailError: Int? = null,
    @StringRes val passwordError: Int? = null,
    @StringRes val confirmPasswordError: Int? = null,

    @StringRes val studentNumberError: Int? = null,
    @StringRes val courseError: Int? = null,
    @StringRes val yearError: Int? = null,

    @StringRes val companyNameError: Int? = null,
    @StringRes val nifError: Int? = null,
    @StringRes val areaError: Int? = null,
    @StringRes val locationError: Int? = null,

    @StringRes val generalError: Int? = null,
    val generalErrorText: String? = null,
    val isRegisterSuccess: Boolean = false
)