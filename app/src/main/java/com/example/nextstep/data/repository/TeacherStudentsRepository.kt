package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdvisorDocumentDto
import com.example.nextstep.data.model.TeacherStudentDetailDto
import com.example.nextstep.data.model.TeacherStudentDto
import com.example.nextstep.data.model.TeacherStudentDetailNonSerializable
import com.example.nextstep.data.model.TeacherEvaluationDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlin.time.Duration.Companion.minutes

class TeacherStudentsRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth
    private val tasksRepository = TeacherTasksRepository()

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
                Result.success(emptyList())
            } else {
                Result.failure(exception)
            }
        }
    }

    suspend fun getStudentDetail(applicationId: String): Result<TeacherStudentDetailNonSerializable> {
        return try {
            Log.d("TeacherStudentsRepo", "Fetching detail for applicationId: $applicationId")
            auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            // Query the view using the specific applicationId
            val studentDetail = supabase
                .from("teacher_students_view")
                .select {
                    filter {
                        eq("application_id", applicationId)
                    }
                }
                .decodeSingle<TeacherStudentDetailDto>()

            Log.d("TeacherStudentDetailRepo", "Loaded applicationId=${studentDetail.applicationId}")
            Log.d("TeacherStudentDetailRepo", "cvPath=${studentDetail.cvPath}")
            Log.d("TeacherStudentDetailRepo", "motivationPath=${studentDetail.motivationLetterPath}")

            // Get tasks
            val tasks = tasksRepository.getTasksByApplication(applicationId)
            val completed = tasks.count { it.status.lowercase() in listOf("completed", "concluida", "concluída") }

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
                    cvPath = studentDetail.cvPath,
                    motivationLetterPath = studentDetail.motivationLetterPath,
                    documents = studentDetail.documents,
                    evaluation = studentDetail.evaluation?.let { eval ->
                        TeacherEvaluationDto(
                            grade = eval.grade,
                            qualitativeFeedback = eval.qualitativeFeedback
                        )
                    }
                )
            )
        } catch (exception: Exception) {
            Log.e("TeacherStudentsRepo", "Erro ao carregar detalhe do aluno para $applicationId", exception)
            Result.failure(exception)
        }
    }

    suspend fun getSignedUrl(bucket: String, path: String): Result<String> {
        if (path.isBlank()) {
            return Result.failure(IllegalArgumentException("Caminho do documento vazio."))
        }
        return try {
            Log.d("TeacherStudentsRepo", "Generating signed URL for path: '$path' in bucket: '$bucket'")
            val url = supabase.storage.from(bucket).createSignedUrl(path, 60.minutes)
            Result.success(url)
        } catch (e: Exception) {
            Log.e("TeacherStudentsRepo", "Error getting signed URL for $path in bucket $bucket", e)
            val friendlyMessage = when {
                e.message?.contains("Object not found", ignoreCase = true) == true || 
                e.message?.contains("404") == true ->
                    "Documento não encontrado no Storage."
                else -> "Não foi possível aceder ao documento."
            }
            Result.failure(Exception(friendlyMessage))
        }
    }
}
