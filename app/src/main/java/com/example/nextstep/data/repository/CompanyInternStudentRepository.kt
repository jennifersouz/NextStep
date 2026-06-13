package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CompanyAdvisorEvaluationDto
import com.example.nextstep.data.model.CompanyInternStudentProfileDto
import com.example.nextstep.data.model.CompanyStudentActivityDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlin.time.Duration.Companion.minutes

class CompanyInternStudentRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    private val validInternshipStatuses = setOf(
        "accepted", "active", "in_progress", "inactive", "completed"
    )

    /**
     * RF26: Fetches the intern student profile for a given applicationId.
     * Uses the company_intern_students_view which filters by company ownership.
     */
    suspend fun getInternStudentProfile(
        applicationId: String
    ): Result<CompanyInternStudentProfileDto> {
        Log.d("CompanyInternStudentRepo", "getInternStudentProfile applicationId=$applicationId")
        return try {
            val currentUserId = auth.currentUserOrNull()?.id
                ?: return Result.failure(
                    IllegalStateException("EMPLOYER_NOT_AUTHENTICATED")
                )

            if (applicationId.isBlank()) {
                return Result.failure(
                    IllegalArgumentException("APPLICATION_ID_EMPTY")
                )
            }

            val profiles = supabase
                .from("company_intern_students_view")
                .select {
                    filter {
                        eq("application_id", applicationId)
                        eq("company_profile_id", currentUserId)
                    }
                }
                .decodeList<CompanyInternStudentProfileDto>()

            val profile = profiles.firstOrNull()
                ?: return Result.failure(
                    IllegalStateException("PERMISSION_DENIED")
                )

            val status = profile.internshipStatus?.trim()?.lowercase()
            if (status == null || status !in validInternshipStatuses) {
                return Result.failure(
                    IllegalStateException("NOT_IN_INTERNSHIP")
                )
            }

            Result.success(profile)
        } catch (exception: Exception) {
            Log.e("CompanyInternStudentRepo", "Erro ao carregar perfil do aluno em estágio", exception)
            Result.failure(exception)
        }
    }

    /**
     * RF27: Fetches the list of activities for a given applicationId.
     * Uses company_student_activities_view for company-scoped access.
     */
    suspend fun getStudentActivities(
        applicationId: String
    ): Result<List<CompanyStudentActivityDto>> {
        val source = "company_student_activities_view"
        Log.d("CompanyInternStudentRepo", "getStudentActivities applicationId=$applicationId source=$source using CompanyStudentActivityDto")
        return try {
            if (applicationId.isBlank()) {
                Log.d("CompanyInternStudentRepo", "getStudentActivities empty applicationId -> emptyList")
                return Result.success(emptyList())
            }

            // DO NOT use CompanyInternStudentProfileDto here for validation.
            // It causes MissingFieldException because the table/view has different columns.
            val activities = supabase
                .from(source)
                .select {
                    filter {
                        eq("application_id", applicationId)
                    }
                }
                .decodeList<CompanyStudentActivityDto>()

            Log.d("CompanyInternStudentRepo", "getStudentActivities found ${activities.size} results from $source")
            Result.success(activities)
        } catch (exception: Exception) {
            Log.e(
                "CompanyInternStudentRepo",
                "Erro ao carregar atividades do aluno applicationId=$applicationId source=$source",
                exception
            )
            Result.failure(exception)
        }
    }

    /**
     * RF28: Fetches the advisor evaluation for a given applicationId.
     * Uses company_advisor_evaluations_view for company-scoped access.
     */
    suspend fun getAdvisorEvaluation(
        applicationId: String
    ): Result<CompanyAdvisorEvaluationDto?> {
        val source = "company_advisor_evaluations_view"
        Log.d("CompanyInternStudentRepo", "getAdvisorEvaluation applicationId=$applicationId source=$source using CompanyAdvisorEvaluationDto")
        return try {
            if (applicationId.isBlank()) {
                Log.d("CompanyInternStudentRepo", "getAdvisorEvaluation empty applicationId -> null")
                return Result.success(null)
            }

            // DO NOT use CompanyInternStudentProfileDto here for validation.
            val evaluations = supabase
                .from(source)
                .select {
                    filter {
                        eq("application_id", applicationId)
                    }
                }
                .decodeList<CompanyAdvisorEvaluationDto>()

            val result = evaluations.firstOrNull()
            Log.d("CompanyInternStudentRepo", "getAdvisorEvaluation found result=${result != null} from $source")
            Result.success(result)
        } catch (exception: Exception) {
            Log.e(
                "CompanyInternStudentRepo",
                "Erro ao carregar avaliação do orientador applicationId=$applicationId source=$source",
                exception
            )
            Result.failure(exception)
        }
    }

    /**
     * Creates a signed URL for a document in storage (CV, motivation letter, etc.).
     */
    suspend fun createSignedDocumentUrl(documentPath: String): Result<String> {
        return try {
            val bucket = supabase.storage.from("application-documents")
            val signedUrl = bucket.createSignedUrl(documentPath, expiresIn = 30.minutes)
            Result.success(signedUrl)
        } catch (exception: Exception) {
            Log.e("CompanyInternStudentRepo", "Erro ao criar signed URL", exception)
            Result.failure(exception)
        }
    }
}
