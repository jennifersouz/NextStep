package com.example.nextstep.ui.utils

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Formatters {
    fun formatWorkMode(value: String?): String {
        return when (value?.trim()?.lowercase()) {
            "onsite", "presencial" -> "Presencial"
            "remote", "remoto" -> "Remoto"
            "hybrid", "hibrido", "híbrido" -> "Híbrido"
            null, "" -> "Não indicado"
            else -> value
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
