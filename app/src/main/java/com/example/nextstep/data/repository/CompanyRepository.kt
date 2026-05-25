package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CompanyInternshipDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class CompanyRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getCompanyInternships(): Result<List<CompanyInternshipDto>> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador não autenticado.")

            val internships = supabase
                .from("internships")
                .select {
                    filter {
                        eq("company_profile_id", companyProfileId)
                    }
                }
                .decodeList<CompanyInternshipDto>()

            Result.success(internships)
        } catch (exception: Exception) {
            Log.e("CompanyRepository", "Erro ao carregar estágios", exception)
            Result.failure(exception)
        }
    }
}