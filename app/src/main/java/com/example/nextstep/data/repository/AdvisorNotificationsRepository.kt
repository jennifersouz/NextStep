package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.NotificationDto
import com.example.nextstep.data.model.UpdateNotificationReadDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AdvisorNotificationsRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    private var currentChannel: RealtimeChannel? = null
    private var subscriptionJob: Job? = null

    suspend fun getNotifications(): Result<List<NotificationDto>> {
        return try {
            val userId = auth.currentUserOrNull()?.id ?: throw IllegalStateException("Não autenticado")

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
            Log.e("AdvisorNotifRepo", "Erro ao carregar notificações", exception)
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
            Log.e("AdvisorNotifRepo", "Erro ao marcar como lida", exception)
            Result.failure(exception)
        }
    }

    suspend fun markAllAsRead(): Result<Unit> {
        return try {
            val userId = auth.currentUserOrNull()?.id ?: throw IllegalStateException("Não autenticado")
            
            supabase.from("notifications")
                .update(UpdateNotificationReadDto(isRead = true)) {
                    filter {
                        eq("receiver_profile_id", userId)
                        eq("is_read", false)
                    }
                }
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdvisorNotifRepo", "Erro ao marcar todas como lidas", exception)
            Result.failure(exception)
        }
    }

    fun subscribeToNotifications(
        scope: CoroutineScope,
        onNotificationReceived: () -> Unit
    ) {
        unsubscribeFromNotifications()

        val user = auth.currentUserOrNull()
        if (user == null) {
            Log.w("AdvisorNotifRepo", "Sem utilizador autenticado. Realtime não iniciado.")
            return
        }

        val userId = user.id

        // Criar canal novo com nome único para evitar conflitos
        val channel = supabase.realtime.channel("advisor_notifications_${userId}_${System.currentTimeMillis()}")

        // IMPORTANTE: postgresChangeFlow deve ser configurado ANTES de channel.subscribe()
        val changeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "notifications"
            filter(column = "receiver_profile_id", operator = FilterOperator.EQ, value = userId)
        }

        subscriptionJob = changeFlow
            .onEach {
                Log.d("AdvisorNotifRepo", "Nova notificação recebida")
                onNotificationReceived()
            }
            .catch { exception ->
                Log.e("AdvisorNotifRepo", "Erro no realtime de notificações", exception)
            }
            .launchIn(scope)

        currentChannel = channel

        scope.launch {
            runCatching {
                channel.subscribe()
            }.onFailure { exception ->
                Log.e("AdvisorNotifRepo", "Erro ao subscrever canal de notificações", exception)
            }
        }
    }

    fun unsubscribeFromNotifications() {
        subscriptionJob?.cancel()
        subscriptionJob = null

        val channel = currentChannel
        currentChannel = null

        if (channel != null) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    channel.unsubscribe()
                }.onFailure { exception ->
                    Log.e("AdvisorNotifRepo", "Erro ao cancelar canal de notificações", exception)
                }
            }
        }
    }
}
