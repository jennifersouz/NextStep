package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdvisorDocumentDto
import com.example.nextstep.data.model.AdvisorTaskListItemDto
import com.example.nextstep.data.model.TeacherStudentDetailDto
import com.example.nextstep.data.model.TeacherStudentDto
import com.example.nextstep.data.model.TeacherStudentDetailNonSerializable
import com.example.nextstep.data.model.TeacherEvaluationDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class TeacherStudentsRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth
    private val tasksRepository = AdvisorTasksRepository()

    suspend fun getStudents(): Result<List<TeacherStudentDto>> {
        return try {
            auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            val students = supabase
                .from("teacher_students_view")
                .select()
                .decodeList<TeacherStudentDto>()

            Result.success(students)
        } catch (exception: Exception) {
            Log.e("TeacherStudentsRepo", "Erro ao carregar alunos", exception)
            if (exception.message?.contains("relation") == true ||
                exception.message?.contains("does not exist") == true
            ) {
                // View not yet created, return empty
                Result.success(emptyList())
            } else {
                Result.failure(exception)
            }
        }
    }

    suspend fun getStudentDetail(applicationId: String): Result<TeacherStudentDetailNonSerializable> {
        return try {
            auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            // Try to get from the teacher_students_view first
            val studentDetail = try {
                supabase
                    .from("teacher_students_view")
                    .select {
                        filter {
                            eq("application_id", applicationId)
                        }
                    }
                    .decodeSingle<TeacherStudentDetailDto>()
            } catch (e: Exception) {
                // Fallback: use the existing teacher_orientation_requests_view
                val students = supabase
                    .from("teacher_students_view")
                    .select()
                    .decodeList<TeacherStudentDto>()

                val student = students.find { it.applicationId == applicationId }
                    ?: return Result.failure(IllegalStateException("Aluno não encontrado."))

                TeacherStudentDetailDto(
                    applicationId = student.applicationId,
                    studentProfileId = student.studentProfileId,
                    studentName = student.studentName,
                    studentEmail = student.studentEmail,
                    offerTitle = student.offerTitle,
                    companyName = student.companyName,
                    status = student.status
                )
            }

            // Get tasks
            val tasksResult = tasksRepository.getTasksByApplication(applicationId)
            val tasks = tasksResult.getOrDefault(emptyList())

            val completed = tasks.count { it.status == "completed" || it.status == "concluida" || it.status == "concluída" }

            Result.success(
                TeacherStudentDetailNonSerializable(
                    applicationId = studentDetail.applicationId,
                    studentProfileId = studentDetail.studentProfileId,
                    studentName = studentDetail.studentName,
                    studentEmail = studentDetail.studentEmail,
                    offerTitle = studentDetail.offerTitle,
                    companyName = studentDetail.companyName,
                    course = studentDetail.course,
                    status = studentDetail.status,
                    location = studentDetail.location,
                    workMode = studentDetail.workMode,
                    duration = studentDetail.duration,
                    startDate = studentDetail.startDate,
                    expectedEndDate = studentDetail.expectedEndDate,
                    companyAdvisorName = studentDetail.companyAdvisorName,
                    lastActivityAt = studentDetail.lastActivityAt,
                    completedTasks = completed,
                    totalTasks = tasks.size,
                    tasks = tasks,
                    documents = studentDetail.documents,
                    evaluation = studentDetail.evaluation?.let { eval ->
                        TeacherEvaluationDto(
                            grade = eval.grade,
                            qualitativeFeedback = eval.comments
                        )
                    }
                )
            )
        } catch (exception: Exception) {
            Log.e("TeacherStudentsRepo", "Erro ao carregar detalhe do aluno", exception)
            Result.failure(exception)
        }
    }
}