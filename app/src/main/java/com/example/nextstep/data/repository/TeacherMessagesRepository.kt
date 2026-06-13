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

            // Agrupar por studentProfileId para ter apenas uma conversa por aluno
            val groupedByStudent = students.groupBy { it.studentProfileId }

            val conversations = groupedByStudent.map { (studentId, studentApps) ->
                // Escolher a candidatura principal
                // 1. Preferir 'accepted'
                // 2. Escolher a mais recente por createdAt
                val primaryApp = studentApps.sortedWith(
                    compareByDescending<TeacherStudentDto> { 
                        it.status?.lowercase() == "accepted" || it.status?.lowercase() == "aceite" 
                    }.thenByDescending { it.createdAt ?: "" }
                ).first()

                // Buscar a última mensagem considerando TODAS as candidaturas deste aluno com este docente
                // Para simplificar, buscamos a última mensagem da candidatura principal
                // ou tentamos buscar entre todas as IDs do grupo.
                val appIds = studentApps.map { it.applicationId }
                val lastMessageData = getLastMessageAcrossApps(appIds)

                TeacherConversationDto(
                    applicationId = primaryApp.applicationId,
                    studentProfileId = primaryApp.studentProfileId,
                    studentName = primaryApp.studentName,
                    studentEmail = primaryApp.studentEmail,
                    offerTitle = primaryApp.offerTitle,
                    companyName = primaryApp.companyName,
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

    private suspend fun getLastMessageAcrossApps(applicationIds: List<String>): ApplicationMessageDto? {
        if (applicationIds.isEmpty()) return null
        return try {
            supabase
                .from("application_chat_messages_view")
                .select {
                    filter {
                        isIn("application_id", applicationIds)
                    }
                    limit(1)
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<ApplicationMessageDto>()
                .firstOrNull()
        } catch (e: Exception) {
            Log.e("TeacherMessagesRepo", "Erro ao buscar última mensagem para apps $applicationIds", e)
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