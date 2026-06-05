package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.StudentChatConversationDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from

class StudentMessagesRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getConversations(): Result<List<StudentChatConversationDto>> {
        return try {
            val conversations = supabase
                .from("student_chat_conversations_view")
                .select()
                .decodeList<StudentChatConversationDto>()

            Result.success(conversations)
        } catch (exception: Exception) {
            Log.e(
                "StudentMessagesRepo",
                "Erro ao carregar conversas do aluno",
                exception
            )
            Result.failure(exception)
        }
    }
}