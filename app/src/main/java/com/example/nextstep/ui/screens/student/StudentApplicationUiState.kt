package com.example.nextstep.ui.screens.student

import android.net.Uri
import androidx.annotation.StringRes
import com.example.nextstep.data.model.OfferDto

data class StudentApplicationUiState(
    val offer: OfferDto? = null,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,

    val motivationLetterUri: Uri? = null,
    val motivationLetterName: String = "",

    val cvUri: Uri? = null,
    val cvName: String = "",

    val isSubmitting: Boolean = false,
    @StringRes val submitErrorRes: Int? = null,
    @StringRes val submitSuccessRes: Int? = null
)