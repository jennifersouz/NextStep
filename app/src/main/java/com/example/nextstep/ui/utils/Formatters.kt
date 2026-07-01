package com.example.nextstep.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.nextstep.R
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Formatters {

    /**
     * Formats a role value according to the current locale.
     * Accepts both technical DB values (student, company) and legacy PT values (Aluno, Empresa).
     * Uses stringResource for automatic locale-based translation.
     */
    @Composable
    fun formatRole(value: String?): String {
        return when (value?.trim()?.lowercase()) {
            "student", "aluno" -> stringResource(R.string.role_student)
            "company", "empresa" -> stringResource(R.string.role_company)
            "advisor", "orientador" -> stringResource(R.string.role_advisor)
            "employee", "funcionário", "funcionario" -> stringResource(R.string.role_employee)
            "teacher", "docente" -> stringResource(R.string.role_teacher)
            "institution", "instituição", "instituicao" -> stringResource(R.string.role_institution)
            "admin", "administrador" -> stringResource(R.string.role_admin)
            else -> value.orEmpty()
        }
    }

    /**
     * Formats account status (active/inactive/archived) according to the current locale.
     */
    @Composable
    fun formatAccountStatus(value: String?, isActive: Boolean?, isArchived: Boolean): String {
        if (isArchived) return stringResource(R.string.archived_status)
        if (isActive == true) return stringResource(R.string.active_status)
        return stringResource(R.string.inactive_status)
    }

    /**
     * Formats account status as a full label like "Account status: Active" / "Estado da conta: Ativo".
     */
    @Composable
    fun formatAccountStatusLabel(isActive: Boolean?, isArchived: Boolean): String {
        val status = formatAccountStatus(null, isActive, isArchived)
        return stringResource(R.string.account_status_format, status)
    }

    /**
     * Formats a company business area according to the current locale.
     * Supports controlled codes (consulting, automotive, etc.) and legacy PT values.
     * For unmapped free-text values, returns the original text unchanged.
     */
    @Composable
    fun formatCompanyArea(value: String?): String {
        return when (value?.trim()?.lowercase()) {
            "consulting", "consultoria" -> stringResource(R.string.area_consulting)
            "automotive", "automotiva", "automóvel", "automovel" -> stringResource(R.string.area_automotive)
            "food", "alimentos", "alimentação", "alimentacao" -> stringResource(R.string.area_food)
            "energy", "energia", "combustível", "combustivel" -> stringResource(R.string.area_energy)
            "technology", "tecnologia" -> stringResource(R.string.area_technology)
            "design" -> stringResource(R.string.area_design)
            "management", "gestão", "gestao" -> stringResource(R.string.area_management)
            "data", "dados" -> stringResource(R.string.area_data)
            "health", "saúde", "saude" -> stringResource(R.string.area_health)
            "education", "educação", "educacao" -> stringResource(R.string.area_education)
            "finance", "finanças", "financas" -> stringResource(R.string.area_finance)
            "it", "informática", "informatica" -> stringResource(R.string.area_it)
            "marketing" -> stringResource(R.string.area_marketing)
            "other", "outro", "outra" -> stringResource(R.string.area_other)
            null, "" -> ""
            else -> value // Free text, return as-is
        }
    }

    /**
      * Returns the string resource ID for an offer area technical value.
      * Use with stringResource(formatOfferAreaRes(...)) for Composable contexts.
      */
    fun formatOfferAreaRes(area: String?): Int {
        return when (area?.trim()?.lowercase()) {
            "mobile" -> R.string.area_mobile
            "software" -> R.string.area_software
            "management", "gestão", "gestao" -> R.string.area_management
            "design" -> R.string.area_design
            "marketing" -> R.string.area_marketing
            "data", "dados" -> R.string.area_data
            "finance", "finanças", "financas" -> R.string.area_finance
            "other", "outro", "outra" -> R.string.area_other
            else -> R.string.area_not_defined
        }
    }

    /**
      * Formats offer business area according to the current locale.
      * Supports controlled codes (management, design, etc.) and legacy PT values.
      */
    @Composable
    fun formatOfferArea(value: String?): String {
        return stringResource(formatOfferAreaRes(value))
    }

    /**
      * Formats offer work mode according to the current locale.
      */
    @Composable
    fun formatWorkMode(value: String?): String {
        return when (value?.trim()?.lowercase()) {
            "onsite", "presencial" -> stringResource(R.string.work_mode_onsite)
            "remote", "remoto" -> stringResource(R.string.work_mode_remote)
            "hybrid", "hibrido", "híbrido" -> stringResource(R.string.work_mode_hybrid)
            null, "" -> stringResource(R.string.not_specified)
            else -> value ?: ""
        }
    }

    @Composable
    fun formatInternshipStatus(value: String?): String {
        return when (value?.trim()?.lowercase()) {
            "pending", "pendente" -> stringResource(R.string.pending)
            "accepted", "aceite" -> stringResource(R.string.accepted)
            "active", "ativo" -> stringResource(R.string.status_active)
            "inactive", "inativo" -> stringResource(R.string.inactive_status_label)
            "in_progress", "em progresso" -> stringResource(R.string.in_progress)
            "completed", "concluido", "concluído" -> stringResource(R.string.completed_label)
            "rejected", "recusado", "recusada" -> stringResource(R.string.rejected)
            "cancelled", "cancelado", "cancelada" -> stringResource(R.string.internship_status_cancelled)
            null, "" -> stringResource(R.string.not_specified)
            else -> value ?: ""
        }
    }

    @Composable
    fun formatDateTime(value: String?): String {
        if (value.isNullOrBlank()) return stringResource(R.string.not_available)

        val separator = stringResource(R.string.date_at)
        val pattern = "dd/MM/yyyy '$separator' HH:mm"

        return try {
            val normalized = value.replace(Regex("\\.(\\d{3})\\d+"), ".$1")
            val instant = Instant.parse(
                normalized.replace("+00:00", "Z")
            )

            val formatter = DateTimeFormatter
                .ofPattern(pattern)
                .withZone(ZoneId.systemDefault())

            formatter.format(instant)
        } catch (e: Exception) {
            try {
                val offsetDateTime = OffsetDateTime.parse(value)
                val formatter = DateTimeFormatter.ofPattern(pattern)
                offsetDateTime.format(formatter)
            } catch (e2: Exception) {
                value
            }
        }
    }
}