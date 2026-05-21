package com.example.nextstep.ui.screens.auth

import androidx.annotation.StringRes

data class LoginUiState(
    val email: String = "",
    val password: String = "",

    @StringRes val emailError: Int? = null,
    @StringRes val passwordError: Int? = null,
    @StringRes val generalError: Int? = null,

    val isLoading: Boolean = false
)