package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.TeacherEvaluationDto
import com.example.nextstep.data.model.TeacherEvaluationInsertDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class TeacherEvaluationRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    /**
     * Loads the existing evaluation for a given application.
     * Returns null if no evaluation exists yet.
     */
    suspend fun getEvaluation(applicationId: String): Result<TeacherEvaluationDto?> {
        return try {
            auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            val evaluations = supabase
                .from("teacher_evaluations")
                .select {
                    filter {
                        eq("application_id", applicationId)
                    }
                }
                .decodeList<TeacherEvaluationDto>()

            val evaluation = evaluations.firstOrNull()
            Result.success(evaluation)
        } catch (exception: Exception) {
            Log.e("TeacherEvaluationRepo", "Erro ao carregar avaliação", exception)
            // If the table doesn't exist yet, treat as no evaluation
            if (exception.message?.contains("relation") == true ||
                exception.message?.contains("does not exist") == true
            ) {
                Result.success(null)
            } else {
                Result.failure(exception)
            }
        }
    }

    /**
     * Gets the current authenticated teacher's profile ID.
     */
    suspend fun getCurrentTeacherProfileId(): Result<String> {
        return try {
            val user = auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            // Query the teacher_profiles table for the current user
            val profiles = supabase
                .from("teacher_profiles")
                .select {
                    filter {
                        eq("user_id", user.id)
                    }
                }
                .decodeList<TeacherProfileDto>()

            val profile = profiles.firstOrNull()
                ?: return Result.failure(IllegalStateException("Perfil de docente não encontrado."))

            Result.success(profile.id)
        } catch (exception: Exception) {
            Log.e("TeacherEvaluationRepo", "Erro ao buscar perfil do docente", exception)
            if (exception.message?.contains("relation") == true ||
                exception.message?.contains("does not exist") == true
            ) {
                Result.failure(IllegalStateException("Perfil de docente não encontrado."))
            } else {
                Result.failure(exception)
            }
        }
    }

    /**
     * Saves (inserts or updates) an evaluation for a given application.
     * If an evaluation already exists for this application, it updates it.
     * Otherwise, it inserts a new one.
     */
    suspend fun saveEvaluation(
        applicationId: String,
        grade: Double,
        qualitativeFeedback: String,
        strengths: String?,
        improvements: String?
    ): Result<TeacherEvaluationDto> {
        return try {
            auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            // Get the student_profile_id from the applications table
            val applications = supabase
                .from("applications")
                .select {
                    filter {
                        eq("id", applicationId)
                    }
                }
                .decodeList<ApplicationBasicDto>()

            val application = applications.firstOrNull()
                ?: return Result.failure(IllegalStateException("Candidatura não encontrada."))

            val studentProfileId = application.studentProfileId
                ?: return Result.failure(IllegalStateException("Perfil do aluno não encontrado na candidatura."))

            // Check if evaluation already exists
            val existing = getEvaluation(applicationId).getOrNull()

            if (existing != null && existing.id != null) {
                // Update existing evaluation
                val updateDto = TeacherEvaluationUpdateDto(
                    grade = grade,
                    qualitativeFeedback = qualitativeFeedback,
                    strengths = strengths,
                    improvements = improvements
                )
                supabase
                    .from("teacher_evaluations")
                    .update(updateDto) {
                        filter {
                            eq("id", existing.id)
                        }
                    }

                Result.success(
                    existing.copy(
                        grade = grade.toString(),
                        qualitativeFeedback = qualitativeFeedback,
                        strengths = strengths,
                        improvements = improvements
                    )
                )
            } else {
                // Get the teacher profile ID
                val teacherProfileId = getCurrentTeacherProfileId().getOrElse {
                    return Result.failure(it)
                }

                // Insert new evaluation
                val insertDto = TeacherEvaluationInsertDto(
                    applicationId = applicationId,
                    teacherProfileId = teacherProfileId,
                    studentProfileId = studentProfileId,
                    grade = grade,
                    qualitativeFeedback = qualitativeFeedback,
                    strengths = strengths,
                    improvements = improvements
                )

                val result = supabase
                    .from("teacher_evaluations")
                    .insert(insertDto) {
                        select()
                    }
                    .decodeSingle<TeacherEvaluationDto>()

                Result.success(result)
            }
        } catch (exception: Exception) {
            Log.e("TeacherEvaluationRepo", "Erro ao guardar avaliação", exception)
            if (exception.message?.contains("relation") == true ||
                exception.message?.contains("does not exist") == true
            ) {
                Result.failure(IllegalStateException("A tabela de avaliações ainda não está disponível. Contacte o administrador."))
            } else {
                Result.failure(exception)
            }
        }
    }
}

/**
 * Minimal DTO to get student_profile_id from applications table.
 */
@kotlinx.serialization.Serializable
data class ApplicationBasicDto(
    @kotlinx.serialization.SerialName("id")
    val id: String,
    @kotlinx.serialization.SerialName("student_profile_id")
    val studentProfileId: String? = null
)

/**
 * Minimal DTO for the teacher profile.
 */
@kotlinx.serialization.Serializable
data class TeacherProfileDto(
    @kotlinx.serialization.SerialName("id")
    val id: String,
    @kotlinx.serialization.SerialName("user_id")
    val userId: String? = null
)

/**
 * DTO for updating an existing evaluation (only mutable fields).
 */
@kotlinx.serialization.Serializable
data class TeacherEvaluationUpdateDto(
    @kotlinx.serialization.SerialName("grade")
    val grade: Double,
    @kotlinx.serialization.SerialName("qualitative_feedback")
    val qualitativeFeedback: String,
    @kotlinx.serialization.SerialName("strengths")
    val strengths: String? = null,
    @kotlinx.serialization.SerialName("improvements")
    val improvements: String? = null
)
