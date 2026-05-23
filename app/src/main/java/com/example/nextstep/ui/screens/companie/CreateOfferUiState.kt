package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.R

enum class OfferArea(
    val dbValue: String,
    @StringRes val labelRes: Int
) {
    MOBILE("mobile", R.string.offer_area_mobile),
    WEB("web", R.string.offer_area_web),
    AI("ai", R.string.offer_area_ai),
    CYBERSECURITY("cybersecurity", R.string.offer_area_cybersecurity),
    DATA("data", R.string.offer_area_data),
    DESIGN("design", R.string.offer_area_design),
    MANAGEMENT("management", R.string.offer_area_management),
    OTHER("other", R.string.offer_area_other)
}

enum class WorkMode(
    val dbValue: String,
    @StringRes val labelRes: Int
) {
    REMOTE("remote", R.string.work_mode_remote),
    ONSITE("onsite", R.string.work_mode_onsite),
    HYBRID("hybrid", R.string.work_mode_hybrid)
}

data class CreateOfferUiState(
    val title: String = "",
    val description: String = "",
    val selectedArea: OfferArea? = null,
    val location: String = "",
    val selectedWorkMode: WorkMode? = null,
    val duration: String = "",
    val vacancies: String = "",
    val requirements: String = "",

    @StringRes val titleError: Int? = null,
    @StringRes val descriptionError: Int? = null,
    @StringRes val areaError: Int? = null,
    @StringRes val locationError: Int? = null,
    @StringRes val workModeError: Int? = null,
    @StringRes val durationError: Int? = null,
    @StringRes val vacanciesError: Int? = null,
    @StringRes val requirementsError: Int? = null,

    val isLoading: Boolean = false,
    @StringRes val generalError: Int? = null,
    @StringRes val successMessage: Int? = null
)