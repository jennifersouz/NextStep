package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdvisorConversationDto

class AdvisorMessagesRepository {

    private val assignedAppsRepository = AdvisorAssignedApplicationsRepository()

    suspend fun getAdvisorConversations(): Result<List<AdvisorConversationDto>> {
        return try {
            val applications = assignedAppsRepository.getAssignedApplications()
                .getOrDefault(emptyList())

            val conversations = applications.map { app ->
                AdvisorConversationDto(
                    applicationId = app.applicationId,
                    studentName = app.studentFullName,
                    studentEmail = app.studentEmail,
                    offerTitle = app.offerTitle,
                    lastMessage = app.lastMessage,
                    lastMessageAt = app.lastMessageAt,
                    unreadCount = app.unreadCount
                )
            }

            Result.success(conversations)
        } catch (exception: Exception) {
            Log.e("AdvisorMessagesRepo", "Erro ao carregar conversas", exception)
            Result.failure(exception)
        }
    }
}