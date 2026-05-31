package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.ApplicationSeenCheckDto
import com.example.nextstep.data.model.StudentNotificationDto
import com.example.nextstep.data.model.UpdateStudentNotificationSeenDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class StudentNotificationsRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getStudentNotifications(): Result<List<StudentNotificationDto>> {
        return try {
            auth.currentUserOrNull()
                ?: throw IllegalStateException("Utilizador não autenticado.")

            val notifications = supabase
                .from("student_application_notifications_view")
                .select()
                .decodeList<StudentNotificationDto>()
                .sortedByDescending { notification ->
                    notification.statusUpdatedAt.orEmpty()
                }

            Result.success(notifications)
        } catch (exception: Exception) {
            Log.e(
                "StudentNotificationsRepo",
                "Erro ao carregar notificações do aluno",
                exception
            )
            Result.failure(exception)
        }
    }

    suspend fun markNotificationAsSeen(
        applicationId: String
    ): Result<Unit> {
        return try {
            val studentProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador não autenticado.")

            Log.d(
                "NOTIF_DEBUG",
                "A marcar como lida. applicationId=$applicationId studentProfileId=$studentProfileId"
            )

            val before = supabase
                .from("applications")
                .select {
                    filter {
                        eq("id", applicationId)
                        eq("student_profile_id", studentProfileId)
                    }
                }
                .decodeSingle<ApplicationSeenCheckDto>()

            Log.d(
                "NOTIF_DEBUG",
                "ANTES update: studentStatusSeen=${before.studentStatusSeen}"
            )

            supabase
                .from("applications")
                .update(
                    UpdateStudentNotificationSeenDto(
                        studentStatusSeen = true
                    )
                ) {
                    filter {
                        eq("id", applicationId)
                        eq("student_profile_id", studentProfileId)
                    }
                }

            val after = supabase
                .from("applications")
                .select {
                    filter {
                        eq("id", applicationId)
                        eq("student_profile_id", studentProfileId)
                    }
                }
                .decodeSingle<ApplicationSeenCheckDto>()

            Log.d(
                "NOTIF_DEBUG",
                "DEPOIS update: studentStatusSeen=${after.studentStatusSeen}"
            )

            if (!after.studentStatusSeen) {
                throw IllegalStateException(
                    "O update correu, mas student_status_seen continuou false. Possível payload vazio ou trigger a repor o valor."
                )
            }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e(
                "NOTIF_DEBUG",
                "Erro ao marcar notificação como lida",
                exception
            )
            Result.failure(exception)
        }
    }

    suspend fun getUnreadNotificationsCount(): Result<Int> {
        return try {
            auth.currentUserOrNull()
                ?: throw IllegalStateException("Utilizador não autenticado.")

            val notifications = supabase
                .from("student_application_notifications_view")
                .select()
                .decodeList<StudentNotificationDto>()

            val unreadCount = notifications.count { notification ->
                !notification.studentStatusSeen
            }

            Result.success(unreadCount)
        } catch (exception: Exception) {
            Log.e(
                "StudentNotificationsRepo",
                "Erro ao carregar número de notificações não lidas",
                exception
            )
            Result.failure(exception)
        }
    }
}