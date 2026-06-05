package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.ApplicationParticipantsDto
import com.example.nextstep.data.model.ChatMessageDto
import com.example.nextstep.data.model.CreateChatMessageDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class ChatRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getMessages(
        applicationId: String
    ): Result<List<ChatMessageDto>> {
        return try {
            val currentUserId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador não autenticado.")

            val messages = supabase
                .from("application_messages")
                .select {
                    filter {
                        eq("application_id", applicationId)
                    }
                }
                .decodeList<ChatMessageDto>()
                .filter { message ->
                    message.senderProfileId == currentUserId ||
                            message.receiverProfileId == currentUserId
                }
                .sortedBy { message ->
                    message.createdAt.orEmpty()
                }

            Result.success(messages)
        } catch (exception: Exception) {
            Log.e("ChatRepository", "Erro ao carregar mensagens", exception)
            Result.failure(exception)
        }
    }

    suspend fun sendMessage(
        applicationId: String,
        content: String
    ): Result<Unit> {
        return try {
            val currentUserId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador não autenticado.")

            val application = supabase
                .from("applications")
                .select {
                    filter {
                        eq("id", applicationId)
                    }
                }
                .decodeSingle<ApplicationParticipantsDto>()

            val receiverProfileId = when (currentUserId) {
                application.studentProfileId -> application.companyProfileId
                application.companyProfileId -> application.studentProfileId
                else -> throw IllegalStateException("Utilizador não pertence a esta candidatura.")
            }

            supabase
                .from("application_messages")
                .insert(
                    CreateChatMessageDto(
                        applicationId = applicationId,
                        senderProfileId = currentUserId,
                        receiverProfileId = receiverProfileId,
                        content = content.trim()
                    )
                )

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("ChatRepository", "Erro ao enviar mensagem", exception)
            Result.failure(exception)
        }
    }
}