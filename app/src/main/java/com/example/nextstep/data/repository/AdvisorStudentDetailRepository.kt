package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdvisorStudentDetailDto
import com.example.nextstep.data.model.AdvisorTaskDto
import com.example.nextstep.data.model.AdvisorDocumentDto
import com.example.nextstep.data.model.AdvisorEvaluationDto
import com.example.nextstep.data.model.AdvisorAssignedApplicationDto

class AdvisorStudentDetailRepository {

    private val assignedAppsRepository = AdvisorAssignedApplicationsRepository()

    suspend fun getStudentDetail(applicationId: String): Result<AdvisorStudentDetailDto> {
        return try {
            val applications = assignedAppsRepository.getAssignedApplications()
                .getOrDefault(emptyList())

            val app = applications.find { it.applicationId == applicationId }
                ?: return Result.failure(IllegalStateException("Aplicação não encontrada."))

            val detail = mapApplicationToDetail(app)

            Result.success(detail)
        } catch (exception: Exception) {
            Log.e("AdvisorDetailRepo", "Erro ao carregar detalhe do aluno", exception)
            Result.failure(exception)
        }
    }

    private fun mapApplicationToDetail(app: AdvisorAssignedApplicationDto): AdvisorStudentDetailDto {
        return AdvisorStudentDetailDto(
            applicationId = app.applicationId,
            studentName = app.studentFullName,
            studentEmail = app.studentEmail,
            offerTitle = app.offerTitle,
            course = app.course,
            status = app.status,
            startDate = null,
            expectedEndDate = null,
            completedTasks = 0,
            totalTasks = 0,
            tasks = emptyList(),
            documents = emptyList(),
            evaluation = null
        )
    }
}