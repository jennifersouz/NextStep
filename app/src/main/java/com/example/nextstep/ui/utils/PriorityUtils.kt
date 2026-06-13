package com.example.nextstep.ui.utils

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.nextstep.R

/**
 * Returns the localized string for the given task priority.
 *
 * DB values ("high", "medium", "low") are mapped to
 * the appropriate string resource so the UI adapts
 * to the current locale without touching the database.
 */
@Composable
fun localizedPriority(priority: String?): String {
    @StringRes val resId = when (priority?.lowercase()?.trim()) {
        "high", "alta" -> R.string.priority_high
        "medium", "media", "média" -> R.string.priority_medium
        "low", "baixa" -> R.string.priority_low
        else -> R.string.priority_none
    }
    return stringResource(resId)
}