package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CompanyProfileDto
import com.example.nextstep.data.model.OfferDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class CompanyProfileRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getCurrentCompanyProfile(): Result<CompanyProfileDto> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            val company = supabase
                .from("companies")
                .select {
                    filter {
                        eq("profile_id", companyProfileId)
                    }
                }
                .decodeSingle<CompanyProfileDto>()

            Result.success(company)
        } catch (exception: Exception) {
            Log.e(
                "CompanyProfileRepository",
                "Erro ao carregar perfil da empresa",
                exception
            )
            Result.failure(exception)
        }
    }

    suspend fun getCurrentCompanyOffers(): Result<List<OfferDto>> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            val offers = supabase
                .from("offers")
                .select {
                    filter {
                        eq("company_profile_id", companyProfileId)
                    }
                }
                .decodeList<OfferDto>()

            Result.success(offers)
        } catch (exception: Exception) {
            Log.e(
                "CompanyProfileRepository",
                "Erro ao carregar estágios da empresa",
                exception
            )
            Result.failure(exception)
        }
    }
}