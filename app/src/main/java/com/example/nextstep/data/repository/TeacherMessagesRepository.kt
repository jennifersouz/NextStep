package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.ApplicationMessageDto
import com.example.nextstep.data.model.TeacherConversationDto
import com.example.nextstep.data.model.TeacherStudentDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class TeacherMessagesRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth
    private val chatRepository = ApplicationChatRepository()

    suspend fun getConversations(): Result<List<TeacherConversationDto>> {
        return try {
            auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            Log.d("TeacherMessagesRepo", "Loading message students from teacher_students_view")

            // Busca alunos do docente através da view que representa alunos acompanhados
            // A view teacher_students_view já deve filtrar apenas os aceites (teacher_status = 'accepted')
            val students = try {
                supabase
                    .from("teacher_students_view")
                    .select()
                    .decodeList<TeacherStudentDto>()
            } catch (e: Exception) {
                Log.e("TeacherMessagesRepo", "Erro ao carregar alunos da view", e)
                emptyList()
            }

            Log.d("TeacherMessagesRepo", "Students loaded: ${students.size}")

            // Para cada aluno/estágio, busca a última mensagem
            val conversations = students.map { student ->
                val appId = student.applicationId
                val lastMessageData = getLastMessage(appId)

                TeacherConversationDto(
                    applicationId = appId,
                    studentProfileId = student.studentProfileId,
                    studentName = student.studentName,
                    studentEmail = student.studentEmail,
                    offerTitle = student.offerTitle,
                    companyName = student.companyName,
                    lastMessage = lastMessageData?.content,
                    lastMessageAt = lastMessageData?.createdAt,
                    unreadCount = 0
                )
            }

            Result.success(conversations)
        } catch (exception: Exception) {
            Log.e("TeacherMessagesRepo", "Erro ao carregar conversas", exception)
            if (exception.message?.contains("relation") == true ||
                exception.message?.contains("does not exist") == true
            ) {
                Result.success(emptyList())
            } else {
                Result.failure(exception)
            }
        }
    }

    private suspend fun getLastMessage(applicationId: String): ApplicationMessageDto? {
        return try {
            supabase
                .from("application_chat_messages_view")
                .select {
                    filter {
                        eq("application_id", applicationId)
                    }
                    limit(1)
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<ApplicationMessageDto>()
                .firstOrNull()
        } catch (e: Exception) {
            Log.e("TeacherMessagesRepo", "Erro ao buscar última mensagem para $applicationId", e)
            null
        }
    }

    suspend fun getMessages(applicationId: String): Result<List<ApplicationMessageDto>> {
        return chatRepository.getMessages(applicationId)
    }

    suspend fun sendMessage(applicationId: String, message: String): Result<Unit> {
        return chatRepository.sendMessage(applicationId, message)
    }
}