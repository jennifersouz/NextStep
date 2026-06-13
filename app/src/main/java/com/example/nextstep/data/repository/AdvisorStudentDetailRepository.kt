package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdvisorStudentDetailDto
import com.example.nextstep.data.model.AdvisorAssignedApplicationDto
import com.example.nextstep.data.model.AdvisorTaskListItemDto

class AdvisorStudentDetailRepository {

    private val assignedAppsRepository = AdvisorAssignedApplicationsRepository()
    private val tasksRepository = AdvisorTasksRepository()

    suspend fun getStudentDetail(applicationId: String): Result<AdvisorStudentDetailDto> {
        return try {
            val applications = assignedAppsRepository.getAssignedApplications()
                .getOrDefault(emptyList())

            val app = applications.find { it.applicationId == applicationId }
                ?: return Result.failure(IllegalStateException("Aplicação não encontrada."))

            val tasksResult = tasksRepository.getTasksByApplication(applicationId)
            val tasks = tasksResult.getOrDefault(emptyList())

            val detail = mapApplicationToDetail(app, tasks)

            Result.success(detail)
        } catch (exception: Exception) {
            Log.e("AdvisorDetailRepo", "Erro ao carregar detalhe do aluno", exception)
            Result.failure(exception)
        }
    }

    private fun mapApplicationToDetail(
        app: AdvisorAssignedApplicationDto,
        tasks: List<AdvisorTaskListItemDto>
    ): AdvisorStudentDetailDto {
        return AdvisorStudentDetailDto(
            applicationId = app.applicationId,
            studentName = app.studentFullName,
            studentEmail = app.studentEmail,
            offerTitle = app.offerTitle,
            course = app.course,
            status = app.status,
            startDate = null,
            expectedEndDate = null,
            completedTasks = tasks.count { it.status == "completed" },
            totalTasks = tasks.size,
            tasks = tasks
        )
    }
}
