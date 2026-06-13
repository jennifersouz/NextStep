package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdvisorEvaluationDto
import com.example.nextstep.data.model.AdvisorEvaluationInsertDto
import com.example.nextstep.data.model.AdvisorEvaluationUpdateDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class AdvisorEvaluationRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    /**
     * Carrega a avaliação existente para uma candidatura.
     * Filtra por application_id + advisor_profile_id (orientador autenticado).
     * Devolve null se ainda não existir avaliação.
     */
    suspend fun getEvaluation(applicationId: String): Result<AdvisorEvaluationDto?> {
        return try {
            val advisorProfileId = auth.currentUserOrNull()?.id
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            val rows = supabase
                .from("advisor_evaluations")
                .select {
                    filter {
                        eq("application_id", applicationId)
                        eq("advisor_profile_id", advisorProfileId)
                    }
                }
                .decodeList<AdvisorEvaluationDto>()

            Log.d("AdvisorEvaluationRepo", "Evaluation loaded: ${rows.firstOrNull()?.id}, applicationId=$applicationId")
            Result.success(rows.firstOrNull())
        } catch (exception: Exception) {
            Log.e("AdvisorEvaluationRepo", "Erro ao carregar avaliação applicationId=$applicationId", exception)
            Result.failure(exception)
        }
    }

    /**
     * Guarda ou atualiza a avaliação para uma candidatura.
     * - Lê o student_profile_id da tabela applications.
     * - Faz insert se não existir; update se já existir.
     */
    suspend fun saveEvaluation(
        applicationId: String,
        grade: Double,
        qualitativeFeedback: String,
        strengths: String?,
        improvements: String?
    ): Result<AdvisorEvaluationDto> {
        return try {
            val advisorProfileId = auth.currentUserOrNull()?.id
                ?: return Result.failure(IllegalStateException("Utilizador autenticado não encontrado."))

            // Obter student_profile_id da candidatura
            val applications = supabase
                .from("applications")
                .select {
                    filter { eq("id", applicationId) }
                }
                .decodeList<ApplicationLookupDto>()

            val application = applications.firstOrNull()
                ?: return Result.failure(IllegalStateException("Candidatura não encontrada."))

            val studentProfileId = application.studentProfileId
                ?: return Result.failure(IllegalStateException("Perfil do aluno não encontrado na candidatura."))

            // Verificar se já existe avaliação
            val existing = getEvaluation(applicationId).getOrNull()

            if (existing != null && existing.id != null) {
                // Atualizar avaliação existente
                Log.d("AdvisorEvaluationRepo", "Updating existing evaluation id=${existing.id}")

                val updateDto = AdvisorEvaluationUpdateDto(
                    grade = grade,
                    qualitativeFeedback = qualitativeFeedback,
                    strengths = strengths,
                    improvements = improvements
                )

                supabase
                    .from("advisor_evaluations")
                    .update(updateDto) {
                        filter { eq("id", existing.id) }
                    }

                Result.success(
                    existing.copy(
                        grade = grade,
                        qualitativeFeedback = qualitativeFeedback,
                        strengths = strengths,
                        improvements = improvements
                    )
                )
            } else {
                // Inserir nova avaliação
                Log.d("AdvisorEvaluationRepo", "Inserting new evaluation for applicationId=$applicationId")

                val insertDto = AdvisorEvaluationInsertDto(
                    applicationId = applicationId,
                    advisorProfileId = advisorProfileId,
                    studentProfileId = studentProfileId,
                    grade = grade,
                    qualitativeFeedback = qualitativeFeedback,
                    strengths = strengths,
                    improvements = improvements
                )

                val result = supabase
                    .from("advisor_evaluations")
                    .insert(insertDto) {
                        select()
                    }
                    .decodeSingle<AdvisorEvaluationDto>()

                Log.d("AdvisorEvaluationRepo", "Evaluation inserted id=${result.id}")
                Result.success(result)
            }
        } catch (exception: Exception) {
            Log.e("AdvisorEvaluationRepo", "Erro ao guardar avaliação applicationId=$applicationId", exception)
            Result.failure(exception)
        }
    }
}

/**
 * DTO mínimo para ler student_profile_id da tabela applications.
 */
@Serializable
private data class ApplicationLookupDto(
    @SerialName("id")
    val id: String,
    @SerialName("student_profile_id")
    val studentProfileId: String? = null
)
