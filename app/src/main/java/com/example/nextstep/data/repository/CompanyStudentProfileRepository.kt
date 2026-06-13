package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CompanyStudentProfileDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class CompanyStudentProfileRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    /**
     * Busca o perfil do candidato garantindo que a empresa autenticada
     * só vê candidatos das suas próprias ofertas.
     */
    suspend fun getStudentProfile(
        applicationId: String
    ): Result<CompanyStudentProfileDto> {
        return try {
            val currentUserId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            val profiles = supabase
                .from("company_candidate_profiles_view")
                .select {
                    filter {
                        eq("application_id", applicationId)
                        eq("company_profile_id", currentUserId)
                    }
                }
                .decodeList<CompanyStudentProfileDto>()

            val profile = profiles.firstOrNull()
                ?: return Result.failure(
                    IllegalStateException("PERMISSION_DENIED")
                )

            Result.success(profile)
        } catch (exception: Exception) {
            val message = exception.message ?: ""
            if (message.contains("PERMISSION_DENIED", ignoreCase = true)) {
                Log.e("CompanyStudentProfile", "Empresa sem permissão para ver este candidato", exception)
            } else {
                Log.e("CompanyStudentProfile", "Erro ao carregar perfil do aluno", exception)
            }
            Result.failure(exception)
        }
    }
}