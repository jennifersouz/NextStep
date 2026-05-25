package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CompanyApplicationDto
import com.example.nextstep.data.model.UpdateApplicationStatusDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.minutes

class CompanyApplicationsRepository {

    private val supabase = SupabaseClientProvider.client

    // DTO interno para validar o resultado do update sem campos da View
    @Serializable
    private data class UpdateViewedResultDto(
        val id: String,
        @SerialName("viewed_by_company")
        val viewedByCompany: Boolean
    )

    suspend fun getCompanyApplications(): Result<List<CompanyApplicationDto>> {
        return try {
            val applications = supabase
                .from("company_applications_view")
                .select()
                .decodeList<CompanyApplicationDto>()
                .sortedByDescending { application ->
                    application.createdAt.orEmpty()
                }

            Result.success(applications)
        } catch (exception: Exception) {
            Log.e("CompanyApplicationsRepo", "Erro ao carregar candidaturas", exception)
            Result.failure(exception)
        }
    }

    suspend fun getCompanyApplicationById(applicationId: String): Result<CompanyApplicationDto> {
        return try {
            val application = supabase
                .from("company_applications_view")
                .select {
                    filter { eq("id", applicationId) }
                }
                .decodeSingle<CompanyApplicationDto>()

            Result.success(application)
        } catch (exception: Exception) {
            Log.e("CompanyApplicationsRepo", "Erro ao carregar detalhe", exception)
            Result.failure(exception)
        }
    }

    suspend fun markApplicationAsViewed(applicationId: String): Result<Unit> {
        return try {
            val currentUserId = supabase.auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            Log.d("MARK_VIEWED_DEBUG", "Iniciando update: user=$currentUserId app=$applicationId")

            // Realizamos o update na tabela real 'applications'
            val response = supabase
                .from("applications")
                .update(
                    mapOf("viewed_by_company" to true)
                ) {
                    filter {
                        eq("id", applicationId)
                        eq("company_profile_id", currentUserId)
                    }
                    select() // RETURNING *
                }

            // Usamos o DTO minimalista para evitar o erro de campos em falta (MissingFieldException)
            val updatedRows = response.decodeList<UpdateViewedResultDto>()
            
            Log.d("MARK_VIEWED_DEBUG", "Linhas afetadas: ${updatedRows.size}")

            if (updatedRows.isEmpty()) {
                Log.e("MARK_VIEWED_DEBUG", "ERRO: RLS bloqueou o update ou ID não encontrado.")
                throw IllegalStateException("Permissão negada para atualizar esta candidatura.")
            }

            val isNowViewed = updatedRows.first().viewedByCompany
            Log.d("MARK_VIEWED_DEBUG", "Sucesso no banco: viewed_by_company=$isNowViewed")

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("MARK_VIEWED_DEBUG", "Falha ao marcar como vista", exception)
            Result.failure(exception)
        }
    }

    suspend fun updateApplicationStatus(applicationId: String, status: String): Result<Unit> {
        return try {
            supabase
                .from("applications")
                .update(
                    UpdateApplicationStatusDto(status = status)
                ) {
                    filter { eq("id", applicationId) }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("CompanyApplicationsRepo", "Erro ao atualizar estado", exception)
            Result.failure(exception)
        }
    }

    suspend fun createSignedDocumentUrl(documentPath: String): Result<String> {
        return try {
            val bucket = supabase.storage.from("application-documents")
            val signedUrl = bucket.createSignedUrl(path = documentPath, expiresIn = 5.minutes)
            Result.success(signedUrl)
        } catch (exception: Exception) {
            Log.e("CompanyApplicationsRepo", "Erro ao gerar URL", exception)
            Result.failure(exception)
        }
    }
}