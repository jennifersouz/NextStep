package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CompanyDto
import com.example.nextstep.data.model.CompanyNameDto
import com.example.nextstep.data.model.CreateOfferDto
import com.example.nextstep.data.model.OfferDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class OffersRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getActiveOffers(): Result<List<OfferDto>> {
        return try {
            val offers = supabase
                .from("offers")
                .select {
                    filter {
                        eq("is_active", true)
                    }
                }
                .decodeList<OfferDto>()

            val profileIds = offers.mapNotNull { it.companyProfileId }.distinct()
            if (profileIds.isEmpty()) {
                return Result.success(offers)
            }

            val companies = supabase
                .from("companies")
                .select(columns = Columns.list("profile_id, company_name")) {
                    filter {
                        isIn("profile_id", profileIds)
                    }
                }
                .decodeList<CompanyDto>()

            val companyNameMap = companies.associate { it.profileId to it.companyName }

            val updatedOffers = offers.map { offer ->
                val currentName = offer.companyProfileId?.let { companyNameMap[it] }
                if (currentName != null && currentName != offer.companyName) {
                    offer.copy(companyName = currentName)
                } else {
                    offer
                }
            }

            Result.success(updatedOffers)
        } catch (exception: Exception) {
            Log.e("OffersRepository", "Erro ao buscar ofertas", exception)
            Result.failure(exception)
        }
    }

    suspend fun createOffer(
        title: String,
        description: String,
        area: String,
        location: String,
        workMode: String,
        duration: String,
        vacancies: Int,
        requirements: String
    ): Result<Unit> {
        return try {
            val user = auth.currentUserOrNull()
                ?: throw IllegalStateException("Utilizador não autenticado.")

            // Tentativa de obter o nome da empresa. 
            // Se não existir, o catch captura e o ViewModel mapeia o erro.
            val companyName = getCurrentCompanyName(user.id)

            supabase.from("offers").insert(
                CreateOfferDto(
                    companyProfileId = user.id,
                    companyName = companyName,
                    title = title,
                    description = description,
                    area = area,
                    location = location,
                    workMode = workMode,
                    duration = duration,
                    vacancies = vacancies,
                    requirements = requirements,
                    isActive = true
                )
            )

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("OffersRepository", "Erro ao criar oferta no Supabase", exception)
            Result.failure(exception)
        }
    }

    private suspend fun getCurrentCompanyName(companyProfileId: String): String {
        return try {
            val company = supabase
                .from("companies")
                .select(
                    columns = Columns.list("company_name")
                ) {
                    filter {
                        eq("profile_id", companyProfileId)
                    }
                }
                .decodeSingle<CompanyNameDto>()

            company.companyName
        } catch (e: Exception) {
            Log.e("OffersRepository", "Erro ao procurar nome da empresa para ID: $companyProfileId", e)
            throw IllegalStateException("PERFIL_EMPRESA_NAO_ENCONTRADO")
        }
    }

    suspend fun getOfferById(offerId: String): Result<OfferDto> {
        return try {
            val offer = supabase
                .from("offers")
                .select {
                    filter {
                        eq("id", offerId)
                        eq("is_active", true)
                    }
                }
                .decodeSingle<OfferDto>()

            val currentName = offer.companyProfileId?.let { profileId ->
                try {
                    val company = supabase
                        .from("companies")
                        .select(columns = Columns.list("company_name")) {
                            filter {
                                eq("profile_id", profileId)
                            }
                        }
                        .decodeSingle<CompanyNameDto>()
                    company.companyName
                } catch (e: Exception) {
                    Log.e("OffersRepository", "Erro ao buscar nome atual da empresa", e)
                    null
                }
            }

            val updatedOffer = if (currentName != null && currentName != offer.companyName) {
                offer.copy(companyName = currentName)
            } else {
                offer
            }

            Result.success(updatedOffer)
        } catch (exception: Exception) {
            Log.e("OffersRepository", "Erro ao buscar detalhes da oferta", exception)
            Result.failure(exception)
        }
    }
}