package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.R

enum class ApplicationStatusFilter {
    ALL,
    TO_REVIEW,
    PENDING,
    ACCEPTED,
    REJECTED,
    WAITING_STUDENT,
    WITH_ADVISOR;

    @StringRes
    fun labelRes(): Int {
        return when (this) {
            ALL -> R.string.filter_all
            TO_REVIEW -> R.string.application_filter_to_review
            PENDING -> R.string.application_status_pending
            ACCEPTED -> R.string.application_status_accepted
            REJECTED -> R.string.application_status_rejected
            WAITING_STUDENT -> R.string.application_filter_waiting_student
            WITH_ADVISOR -> R.string.application_filter_with_advisor
        }
    }
}
