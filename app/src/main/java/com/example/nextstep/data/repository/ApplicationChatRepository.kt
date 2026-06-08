package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.ApplicationMessageDto
import com.example.nextstep.data.model.UpdateMessageReadAtDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.Instant
import java.util.Objects.isNull

class ApplicationChatRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    private var currentChannel: RealtimeChannel? = null
    private var subscriptionJob: Job? = null

    private val cleanupScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    suspend fun getMessages(
        applicationId: String
    ): Result<List<ApplicationMessageDto>> {
        return try {
            auth.currentUserOrNull()
                ?: throw IllegalStateException("Utilizador não autenticado.")

            val messages = supabase
                .from("application_chat_messages_view")
                .select {
                    filter {
                        eq("application_id", applicationId)
                    }
                }
                .decodeList<ApplicationMessageDto>()
                .sortedBy { message ->
                    message.createdAt
                }

            Result.success(messages)
        } catch (exception: Exception) {
            Log.e(
                "ApplicationChatRepo",
                "Erro ao carregar mensagens",
                exception
            )

            Result.failure(exception)
        }
    }

    suspend fun sendMessage(
        applicationId: String,
        content: String
    ): Result<Unit> {
        if (applicationId.isBlank()) {
            return Result.failure(IllegalArgumentException("APPLICATION_ID_EMPTY"))
        }

        val trimmedContent = content.trim()
        if (trimmedContent.isBlank()) {
            return Result.failure(IllegalArgumentException("MESSAGE_EMPTY"))
        }

        Log.d("ApplicationChatRepo", "sendMessage applicationId=$applicationId")
        Log.d("ApplicationChatRepo", "contentLength=${trimmedContent.length}")

        return try {
            auth.currentUserOrNull()
                ?: throw IllegalStateException("Utilizador não autenticado.")

            supabase.postgrest.rpc(
                function = "send_application_message",
                parameters = buildJsonObject {
                    put("application_uuid", applicationId)
                    put("message_content", trimmedContent)
                }
            )

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e(
                "ApplicationChatRepo",
                "Erro ao enviar mensagem",
                exception
            )

            Result.failure(exception)
        }
    }

    suspend fun markMessagesAsRead(
        applicationId: String
    ): Result<Unit> {
        return try {
            val currentUserId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador não autenticado.")

            supabase
                .from("application_messages")
                .update(
                    UpdateMessageReadAtDto(
                        readAt = Instant.now().toString()
                    )
                ) {
                    filter {
                        eq("application_id", applicationId)
                        eq("receiver_profile_id", currentUserId)
                        isNull("read_at")
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e(
                "ApplicationChatRepo",
                "Erro ao marcar mensagens como lidas",
                exception
            )

            Result.failure(exception)
        }
    }

    fun subscribeToMessages(
        applicationId: String,
        scope: CoroutineScope,
        onMessageReceived: () -> Unit
    ) {
        unsubscribeFromMessages()

        val channel = supabase.channel(
            channelId = "application-chat-$applicationId"
        )

        currentChannel = channel

        val changeFlow = channel.postgresChangeFlow<PostgresAction.Insert>(
            schema = "public"
        ) {
            table = "application_messages"
            filter(
                column = "application_id",
                operator = FilterOperator.EQ,
                value = applicationId
            )
        }

        subscriptionJob = changeFlow
            .onEach {
                onMessageReceived()
            }
            .launchIn(scope)

        scope.launch {
            runCatching {
                channel.subscribe()
            }.onFailure { exception ->
                Log.e(
                    "ApplicationChatRepo",
                    "Erro ao subscrever canal realtime",
                    exception
                )
            }
        }
    }

    fun unsubscribeFromMessages() {
        subscriptionJob?.cancel()
        subscriptionJob = null

        val channel = currentChannel
        currentChannel = null

        if (channel != null) {
            cleanupScope.launch {
                runCatching {
                    supabase.realtime.removeChannel(channel)
                }.onFailure { exception ->
                    Log.e(
                        "ApplicationChatRepo",
                        "Erro ao remover canal realtime",
                        exception
                    )
                }
            }
        }
    }
}