package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.ApplicationSeenCheckDto
import com.example.nextstep.data.model.StudentNotificationDto
import com.example.nextstep.data.model.UpdateAdvisorAssignmentSeenDto
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
                    notification.sortDate
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
                "A marcar estado como lida. applicationId=$applicationId studentProfileId=$studentProfileId"
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

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e(
                "NOTIF_DEBUG",
                "Erro ao marcar notificação de estado como lida",
                exception
            )
            Result.failure(exception)
        }
    }

    suspend fun markAdvisorAssignmentAsSeen(
        applicationId: String
    ): Result<Unit> {
        return try {
            val studentProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador não autenticado.")

            Log.d(
                "NOTIF_DEBUG",
                "A marcar orientador como visto. applicationId=$applicationId"
            )

            supabase
                .from("applications")
                .update(
                    UpdateAdvisorAssignmentSeenDto(
                        advisorAssignmentSeen = true
                    )
                ) {
                    filter {
                        eq("id", applicationId)
                        eq("student_profile_id", studentProfileId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e(
                "NOTIF_DEBUG",
                "Erro ao marcar notificação de orientador como lida",
                exception
            )
            Result.failure(exception)
        }
    }

    suspend fun markAllNotificationsAsSeen(): Result<Unit> {
        return try {
            val studentProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador não autenticado.")

            supabase
                .from("applications")
                .update(
                    UpdateStudentNotificationSeenDto(
                        studentStatusSeen = true
                    )
                ) {
                    filter {
                        eq("student_profile_id", studentProfileId)
                        eq("student_status_seen", false)
                    }
                }

            supabase
                .from("applications")
                .update(
                    UpdateAdvisorAssignmentSeenDto(
                        advisorAssignmentSeen = true
                    )
                ) {
                    filter {
                        eq("student_profile_id", studentProfileId)
                        eq("advisor_assignment_seen", false)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e(
                "StudentNotificationsRepo",
                "Erro ao marcar todas como lidas",
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
                notification.isUnread
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
