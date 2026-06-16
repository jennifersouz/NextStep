package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.ApplicationDto
import com.example.nextstep.data.model.CompanyEvaluationDto
import com.example.nextstep.data.model.CompanyEvaluationUpsertDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class CompanyEvaluationRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    companion object {
        private val ALLOWED_EVALUATION_STATUSES = setOf(
            "accepted", "active", "inactive", "in_progress", "completed"
        )
    }

    suspend fun getEvaluation(applicationId: String): Result<CompanyEvaluationDto?> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: return Result.failure(IllegalStateException("EMPRESA_NAO_AUTENTICADA"))

            if (applicationId.isBlank()) {
                return Result.success(null)
            }

            val evaluations = supabase
                .from("company_evaluations")
                .select {
                    filter {
                        eq("application_id", applicationId)
                        eq("company_profile_id", companyProfileId)
                    }
                }
                .decodeList<CompanyEvaluationDto>()

            Result.success(evaluations.firstOrNull())
        } catch (exception: Exception) {
            Log.e("CompanyEvaluationRepo", "Erro ao carregar avaliação", exception)
            Result.failure(exception)
        }
    }

    suspend fun upsertEvaluation(evaluation: CompanyEvaluationUpsertDto): Result<CompanyEvaluationDto> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: return Result.failure(IllegalStateException("EMPRESA_NAO_AUTENTICADA"))

            if (evaluation.applicationId.isBlank()) {
                return Result.failure(IllegalArgumentException("APPLICATION_ID_EMPTY"))
            }

            if (evaluation.studentProfileId.isBlank()) {
                return Result.failure(IllegalArgumentException("STUDENT_PROFILE_ID_EMPTY"))
            }

            // Security: verify this application belongs to the authenticated company
            val applications = supabase
                .from("applications")
                .select {
                    filter {
                        eq("id", evaluation.applicationId)
                        eq("company_profile_id", companyProfileId)
                    }
                }
                .decodeList<ApplicationDto>()

            if (applications.isEmpty()) {
                return Result.failure(IllegalStateException("PERMISSION_DENIED"))
            }

            val appStatus = applications.first().status.trim().lowercase()
            if (appStatus !in ALLOWED_EVALUATION_STATUSES) {
                return Result.failure(IllegalStateException("NOT_IN_INTERNSHIP"))
            }

            // Check if an evaluation already exists for this application + company
            val existing = getEvaluation(evaluation.applicationId).getOrNull()

            if (existing != null && existing.id != null) {
                // Update existing evaluation
                supabase
                    .from("company_evaluations")
                    .update(evaluation) {
                        filter { eq("id", existing.id) }
                    }

                Result.success(
                    existing.copy(
                        grade = evaluation.grade,
                        qualitativeFeedback = evaluation.qualitativeFeedback,
                        strengths = evaluation.strengths,
                        improvements = evaluation.improvements,
                        recommendation = evaluation.recommendation,
                        status = evaluation.status
                    )
                )
            } else {
                // Insert new evaluation
                val result = supabase
                    .from("company_evaluations")
                    .insert(evaluation) {
                        select()
                    }
                    .decodeSingle<CompanyEvaluationDto>()

                Result.success(result)
            }
        } catch (exception: Exception) {
            Log.e("CompanyEvaluationRepo", "Erro ao guardar avaliação", exception)
            Result.failure(exception)
        }
    }
}
