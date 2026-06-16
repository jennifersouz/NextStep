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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.Instant

class ApplicationChatRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    private var currentChannel: RealtimeChannel? = null
    private var subscriptionJob: Job? = null

    suspend fun getMessages(
        applicationId: String,
        chatType: String = "advisor"
    ): Result<List<ApplicationMessageDto>> {
        return try {
            auth.currentUserOrNull()
                ?: throw IllegalStateException("Utilizador não autenticado.")

            val messages = supabase
                .from("application_chat_messages_view")
                .select {
                    filter {
                        eq("application_id", applicationId)
                        eq("participant_type", chatType)
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
        content: String,
        chatType: String = "advisor"
    ): Result<Unit> {
        if (applicationId.isBlank()) {
            return Result.failure(IllegalArgumentException("APPLICATION_ID_EMPTY"))
        }

        val trimmedContent = content.trim()
        if (trimmedContent.isBlank()) {
            return Result.failure(IllegalArgumentException("MESSAGE_EMPTY"))
        }

        val currentUser = auth.currentUserOrNull()
        Log.d("ChatDebug", "sendMessage ApplicationId=$applicationId")
        Log.d("ChatDebug", "sendMessage ChatType=$chatType")
        Log.d("ChatDebug", "sendMessage Sender=${currentUser?.id}")
        Log.d("ChatDebug", "sendMessage contentLength=${trimmedContent.length}")

        return try {
            currentUser
                ?: throw IllegalStateException("Utilizador não autenticado.")

            supabase.postgrest.rpc(
                function = "send_application_message",
                parameters = buildJsonObject {
                    put("application_uuid", applicationId)
                    put("message_content", trimmedContent)
                    put("participant_type", chatType)
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
        applicationId: String,
        chatType: String = "advisor"
    ): Result<Unit> {
        return try {
            val currentUserId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador não autenticado.")

            // Determine receiver based on chatType for proper filtering
            // teacher chat: receiver is teacher_profile_id
            // advisor chat: receiver is advisor_profile_id
            // company chat: receiver is company_profile_id
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

    suspend fun subscribeToMessages(
        applicationId: String,
        chatType: String = "advisor",
        scope: CoroutineScope,
        onMessageReceived: () -> Unit
    ) {
        unsubscribeFromMessages()

        Log.d("ChatDebug", "subscribeToMessages - Canal criado")

        val channel = supabase.channel(
            channelId = "application-chat-$applicationId-$chatType"
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
            filter(
                column = "participant_type",
                operator = FilterOperator.EQ,
                value = chatType
            )
        }

        Log.d("ChatDebug", "subscribeToMessages - Flow criado")

        subscriptionJob = changeFlow
            .onEach {
                onMessageReceived()
            }
            .launchIn(scope)

        runCatching {
            channel.subscribe()
            Log.d("ChatDebug", "subscribeToMessages - Canal subscrito")
        }.onFailure { exception ->
            Log.e(
                "ApplicationChatRepo",
                "Erro ao subscrever canal realtime",
                exception
            )
        }
    }

    suspend fun unsubscribeFromMessages() {
        subscriptionJob?.cancel()
        subscriptionJob = null

        val channel = currentChannel
        currentChannel = null

        if (channel != null) {
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
