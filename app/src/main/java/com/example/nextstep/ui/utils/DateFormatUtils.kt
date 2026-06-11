package com.example.nextstep.ui.utils

import android.os.Build
import android.util.Log
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object DateFormatUtils {

    fun formatDateTimeForUi(rawDate: String?): String {
        if (rawDate.isNullOrBlank()) return ""
        
        return try {
            val dateTime = parseIsoDate(rawDate)
            val now = LocalDateTime.now()
            
            when {
                dateTime.toLocalDate().isEqual(now.toLocalDate()) -> {
                    "Hoje às ${dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
                }
                dateTime.toLocalDate().isEqual(now.toLocalDate().minusDays(1)) -> {
                    "Ontem às ${dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
                }
                else -> {
                    dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"))
                }
            }
        } catch (e: Exception) {
            Log.e("DateFormatUtils", "Error parsing date: $rawDate", e)
            rawDate // Fallback to raw string if parsing fails
        }
    }

    fun formatDateForUi(rawDate: String?): String {
        if (rawDate.isNullOrBlank()) return ""
        
        return try {
            val dateTime = parseIsoDate(rawDate)
            dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (e: Exception) {
            rawDate
        }
    }

    fun formatMessageListTime(rawDate: String?): String {
        if (rawDate.isNullOrBlank()) return ""
        
        return try {
            val dateTime = parseIsoDate(rawDate)
            val now = LocalDateTime.now()
            
            when {
                dateTime.toLocalDate().isEqual(now.toLocalDate()) -> {
                    dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                }
                dateTime.toLocalDate().isEqual(now.toLocalDate().minusDays(1)) -> {
                    "Ontem"
                }
                dateTime.year == now.year -> {
                    dateTime.format(DateTimeFormatter.ofPattern("dd/MM"))
                }
                else -> {
                    dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                }
            }
        } catch (e: Exception) {
            rawDate
        }
    }

    private fun parseIsoDate(rawDate: String): LocalDateTime {
        return try {
            // Try OffsetDateTime first (Supabase usually returns this)
            OffsetDateTime.parse(rawDate)
                .atZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()
        } catch (e: DateTimeParseException) {
            try {
                // Try basic ISO if OffsetDateTime fails
                LocalDateTime.parse(rawDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            } catch (e2: DateTimeParseException) {
                // If it's just a date like 2026-06-11
                java.time.LocalDate.parse(rawDate).atStartOfDay()
            }
        }
    }
}
