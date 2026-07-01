package com.example.nextstep.ui.screens.admin

import androidx.annotation.StringRes
import com.example.nextstep.R

enum class RecentActivityType {
    STUDENT_CREATED,
    TEACHER_CREATED,
    ADVISOR_CREATED,
    COMPANY_CREATED,
    INSTITUTION_CREATED;

    @StringRes
    fun titleRes(): Int = when (this) {
        STUDENT_CREATED -> R.string.recent_student_created
        TEACHER_CREATED -> R.string.recent_teacher_created
        ADVISOR_CREATED -> R.string.recent_advisor_created
        COMPANY_CREATED -> R.string.recent_company_created
        INSTITUTION_CREATED -> R.string.recent_institution_created
    }
}

data class RecentActivityUiModel(
    val id: String,
    val type: RecentActivityType,
    val name: String,
    val email: String,
    val roleName: String,
    val createdAt: String?
)
