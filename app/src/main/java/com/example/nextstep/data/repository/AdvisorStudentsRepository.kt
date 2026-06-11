package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdvisorAssignedApplicationDto
import com.example.nextstep.data.model.AdvisorAssignedStudentDto

class AdvisorStudentsRepository {

    private val assignedAppsRepository = AdvisorAssignedApplicationsRepository()

    suspend fun getAssignedStudents(): Result<List<AdvisorAssignedStudentDto>> {
        return try {
            val applications = assignedAppsRepository.getAssignedApplications()
                .getOrDefault(emptyList())

            val students = applications.map { app ->
                AdvisorAssignedStudentDto(
                    applicationId = app.applicationId,
                    studentName = app.studentFullName,
                    studentEmail = app.studentEmail,
                    offerTitle = app.offerTitle,
                    status = app.status
                )
            }

            Result.success(students)
        } catch (exception: Exception) {
            Log.e("AdvisorStudentsRepo", "Erro ao carregar alunos", exception)
            Result.failure(exception)
        }
    }
}