package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CompanyActiveAdvisorDto
import com.example.nextstep.data.model.UpdateApplicationAdvisorDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class CompanyAssignAdvisorRepository {

    private val supabase = SupabaseClientProvider.client

    @Serializable
    private data class UpdateAdvisorResultDto(
        val id: String,
        @SerialName("advisor_profile_id")
        val advisorProfileId: String?
    )

    suspend fun getActiveAdvisors(): Result<List<CompanyActiveAdvisorDto>> {
        return try {
            val advisors = supabase
                .from("company_active_advisors_view")
                .select()
                .decodeList<CompanyActiveAdvisorDto>()
                .sortedBy { it.name.lowercase() }

            Result.success(advisors)
        } catch (exception: Exception) {
            Log.e("CompanyAssignAdvisor", "Erro ao carregar orientadores ativos", exception)
            Result.failure(exception)
        }
    }

    suspend fun assignAdvisor(
        applicationId: String,
        advisorProfileId: String
    ): Result<Unit> {
        return try {
            Log.d("AssignAdvisor", "applicationId=$applicationId")
            Log.d("AssignAdvisor", "advisorProfileId=$advisorProfileId")
            Log.d("CompanyAssignAdvisor", "Atribuir orientador")
            Log.d("CompanyAssignAdvisor", "applicationId=$applicationId")
            Log.d("CompanyAssignAdvisor", "advisorProfileId=$advisorProfileId")

            val response = supabase
                .from("applications")
                .update(
                    UpdateApplicationAdvisorDto(
                        advisorProfileId = advisorProfileId
                    )
                ) {
                    filter {
                        eq("id", applicationId)
                    }
                    select()
                }

            val updatedRows = response.decodeList<UpdateAdvisorResultDto>()
            if (updatedRows.isEmpty()) {
                throw IllegalStateException(
                    "Nenhuma candidatura atualizada. Verifique permissões RLS ou ID da candidatura."
                )
            }

            val updated = updatedRows.first()
            if (updated.advisorProfileId != advisorProfileId) {
                throw IllegalStateException(
                    "Orientador não foi gravado. advisor_profile_id=${updated.advisorProfileId}"
                )
            }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("CompanyAssignAdvisor", "Erro ao atribuir orientador", exception)
            Result.failure(exception)
        }
    }

    suspend fun removeAdvisor(
        applicationId: String
    ): Result<Unit> {
        return try {
            val response = supabase
                .from("applications")
                .update(
                    UpdateApplicationAdvisorDto(
                        advisorProfileId = null
                    )
                ) {
                    filter {
                        eq("id", applicationId)
                    }
                    select()
                }

            val updatedRows = response.decodeList<UpdateAdvisorResultDto>()
            if (updatedRows.isEmpty()) {
                throw IllegalStateException(
                    "Nenhuma candidatura atualizada. Verifique permissões RLS ou ID da candidatura."
                )
            }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("CompanyAssignAdvisor", "Erro ao remover orientador", exception)
            Result.failure(exception)
        }
    }
}
