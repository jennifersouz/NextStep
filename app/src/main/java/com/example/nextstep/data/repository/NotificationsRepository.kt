package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.NotificationDto
import com.example.nextstep.data.model.UpdateNotificationReadDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class NotificationsRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getNotifications(): Result<List<NotificationDto>> {
        return try {
            val userId = auth.currentUserOrNull()?.id
                ?: return Result.failure(IllegalStateException("Não autenticado"))

            val notifications = supabase.from("notifications")
                .select {
                    filter {
                        eq("receiver_profile_id", userId)
                    }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<NotificationDto>()

            Result.success(notifications)
        } catch (exception: Exception) {
            Log.e("NotificationsRepo", "Erro ao carregar notificações", exception)
            Result.failure(exception)
        }
    }

    suspend fun markAsRead(notificationId: String): Result<Unit> {
        return try {
            supabase.from("notifications")
                .update(UpdateNotificationReadDto(isRead = true)) {
                    filter {
                        eq("id", notificationId)
                    }
                }
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("NotificationsRepo", "Erro ao marcar como lida", exception)
            Result.failure(exception)
        }
    }

    suspend fun markAllAsRead(): Result<Unit> {
        return try {
            val userId = auth.currentUserOrNull()?.id
                ?: return Result.failure(IllegalStateException("Não autenticado"))

            supabase.from("notifications")
                .update(UpdateNotificationReadDto(isRead = true)) {
                    filter {
                        eq("receiver_profile_id", userId)
                        eq("is_read", false)
                    }
                }
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("NotificationsRepo", "Erro ao marcar todas como lidas", exception)
            Result.failure(exception)
        }
    }
}
