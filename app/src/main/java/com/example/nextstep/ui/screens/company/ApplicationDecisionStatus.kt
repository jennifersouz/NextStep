package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.R

enum class ApplicationDecisionStatus(
    val dbValue: String,
    @StringRes val labelRes: Int
) {
    PENDING(
        dbValue = "pending",
        labelRes = R.string.application_status_pending
    ),

    ACCEPTED(
        dbValue = "accepted",
        labelRes = R.string.application_status_accepted
    ),

    REJECTED(
        dbValue = "rejected",
        labelRes = R.string.application_status_rejected
    );

    companion object {
        fun fromDbValue(value: String): ApplicationDecisionStatus {
            return entries.firstOrNull { status ->
                status.dbValue == value
            } ?: PENDING
        }
    }
}