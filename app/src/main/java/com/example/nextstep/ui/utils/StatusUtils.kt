package com.example.nextstep.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.nextstep.R

/**
 * Application/Internship status constants used throughout the UI.
 * These match the values stored in the database (always English lowercase).
 */
object AppStatus {
    const val PENDING = "pending"
    const val ACCEPTED = "accepted"
    const val REJECTED = "rejected"
    const val ACTIVE = "active"
    const val SUBMITTED = "submitted"
    const val CANCELLED = "cancelled"
}

/**
 * Task status constants used throughout the UI.
 */
object TaskStatus {
    const val PENDING = "pending"
    const val IN_PROGRESS = "in_progress"
    const val COMPLETED = "completed"
}

/**
 * Translates a technical application/internship status value from the database
 * into a localized display string for the current locale.
 *
 * DB values are always stored in English lowercase (pending, accepted, rejected,
 * active, in_progress, completed, submitted). This function normalises the input
 * and maps it to the correct string resource so that the UI always shows the text
 * in the user's language without modifying any database value.
 *
 * Usage (in a Composable):
 *   Text(text = applicationStatusToDisplay(student.status))
 */
@Composable
fun applicationStatusToDisplay(status: String?): String {
    return when (status?.trim()?.lowercase()) {
        "pending", "pendente"                       -> stringResource(R.string.status_pending)
        "accepted", "aceite"                        -> stringResource(R.string.status_accepted)
        "rejected", "recusada", "recusado",
        "rejeitada", "rejeitado"                    -> stringResource(R.string.status_rejected)
        "active", "ativo", "ativa"                  -> stringResource(R.string.status_active)
        "in_progress", "em_progresso",
        "em progresso", "em_curso", "em curso"      -> stringResource(R.string.status_in_progress)
        "completed", "concluido", "concluído",
        "concluida", "concluída"                    -> stringResource(R.string.status_completed)
        "submitted", "submetido", "submetida"       -> stringResource(R.string.status_submitted)
        else -> status?.replaceFirstChar { it.uppercase() } ?: "-"
    }
}

/**
 * Returns the colour associated with a given application/internship status,
 * suitable for use as text colour or indicator colour in the UI.
 */
fun applicationStatusToColor(status: String?): Color {
    return when (status?.trim()?.lowercase()) {
        "accepted", "aceite",
        "active", "ativo", "ativa"                  -> Color(0xFF2E7D32)
        "rejected", "recusada", "recusado",
        "rejeitada", "rejeitado"                    -> Color(0xFFB00020)
        "in_progress", "em_progresso",
        "em progresso", "em_curso", "em curso"      -> Color(0xFF1565C0)
        "completed", "concluido", "concluído",
        "concluida", "concluída"                    -> Color(0xFF1565C0)
        "pending", "pendente"                       -> Color(0xFF8D6E00)
        "submitted", "submetido", "submetida"       -> Color(0xFF555555)
        else                                        -> Color(0xFF555555)
    }
}
