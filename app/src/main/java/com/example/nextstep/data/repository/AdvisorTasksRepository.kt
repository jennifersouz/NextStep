package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdvisorTaskListItemDto

class AdvisorTasksRepository {

    private val assignedAppsRepository = AdvisorAssignedApplicationsRepository()

    suspend fun getAdvisorTasks(): Result<List<AdvisorTaskListItemDto>> {
        return try {
            val applications = assignedAppsRepository.getAssignedApplications()
                .getOrDefault(emptyList())

            val tasks = applications.map { app ->
                AdvisorTaskListItemDto(
                    id = "${app.applicationId}_follow_up",
                    applicationId = app.applicationId,
                    title = "Acompanhamento de estágio",
                    description = "Acompanhar progresso do aluno ${app.studentFullName}",
                    studentName = app.studentFullName,
                    offerTitle = app.offerTitle,
                    status = if (app.status == "completed") "completed" else "pending",
                    dueDate = null
                )
            }

            Result.success(tasks)
        } catch (exception: Exception) {
            Log.e("AdvisorTasksRepo", "Erro ao carregar tarefas", exception)
            Result.failure(exception)
        }
    }
}