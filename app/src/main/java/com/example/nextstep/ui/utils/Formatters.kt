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
      * Formats offer business area according to the current locale.
      * Supports controlled codes (management, automotive, etc.) and legacy PT values.
      * For unmapped free-text values, returns the original text unchanged.
      */
    @Composable
    fun formatOfferArea(value: String?): String {
        return when (value?.trim()?.lowercase()) {
            "consulting", "consultoria" -> stringResource(R.string.area_consulting)
            "automotive", "automotiva", "automóvel", "automovel" -> stringResource(R.string.area_automotive)
            "food", "alimentos", "alimentação", "alimentacao" -> stringResource(R.string.area_food)
            "energy", "energia", "combustível", "combustivel" -> stringResource(R.string.area_energy)
            "technology", "tecnologia" -> stringResource(R.string.area_technology)
            "design" -> stringResource(R.string.offer_area_design)
            "management", "gestão", "gestao" -> stringResource(R.string.offer_area_management)
            "data", "dados" -> stringResource(R.string.offer_area_data)
            "health", "saúde", "saude" -> stringResource(R.string.area_health)
            "education", "educação", "educacao" -> stringResource(R.string.area_education)
            "finance", "finanças", "financas" -> stringResource(R.string.area_finance)
            "mobile" -> stringResource(R.string.offer_area_mobile)
            "web" -> stringResource(R.string.offer_area_web)
            "ai" -> stringResource(R.string.offer_area_ai)
            "cybersecurity" -> stringResource(R.string.offer_area_cybersecurity)
            "marketing" -> stringResource(R.string.area_marketing)
            "software" -> stringResource(R.string.area_it)
            null, "" -> ""
            else -> value // Free text, return as-is
        }
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

    fun formatInternshipStatus(value: String?): String {
        return when (value?.trim()?.lowercase()) {
            "pending", "pendente" -> "Pendente"
            "accepted", "aceite" -> "Aceite"
            "active", "ativo" -> "Ativo"
            "inactive", "inativo" -> "Inativo"
            "in_progress", "em progresso" -> "Em progresso"
            "completed", "concluido", "concluído" -> "Concluído"
            "rejected", "recusado", "recusada" -> "Recusado"
            "cancelled", "cancelado", "cancelada" -> "Cancelado"
            null, "" -> "Não indicado"
            else -> value
        }
    }

    fun formatDateTime(value: String?): String {
        if (value.isNullOrBlank()) return "Não indicada"

        return try {
            // Normalize fractional seconds to 3 digits for Instant.parse if necessary
            val normalized = value.replace(Regex("\\.(\\d{3})\\d+"), ".$1")
            val instant = Instant.parse(
                normalized.replace("+00:00", "Z")
            )

            val formatter = DateTimeFormatter
                .ofPattern("dd/MM/yyyy 'às' HH:mm")
                .withZone(ZoneId.systemDefault())

            formatter.format(instant)
        } catch (e: Exception) {
            try {
                val offsetDateTime = OffsetDateTime.parse(value)
                val formatter = DateTimeFormatter
                    .ofPattern("dd/MM/yyyy 'às' HH:mm")

                offsetDateTime.format(formatter)
            } catch (e2: Exception) {
                value
            }
        }
    }
}