package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CompanyOfferDto
import com.example.nextstep.data.model.CompanyOfferUpdateDto
import com.example.nextstep.data.model.OfferDto
import com.example.nextstep.data.model.UpdateOfferActiveDto
import com.example.nextstep.data.model.UpdateOfferDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class CompanyOffersRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

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
                .sortedByDescending { offer ->
                    offer.id
                }

            Result.success(offers)
        } catch (exception: Exception) {
            Log.e("CompanyOffersRepository", "Erro ao carregar ofertas da empresa", exception)
            Result.failure(exception)
        }
    }

    suspend fun getCompanyOfferById(
        offerId: String
    ): Result<OfferDto> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            val offer = supabase
                .from("offers")
                .select {
                    filter {
                        eq("id", offerId)
                        eq("company_profile_id", companyProfileId)
                    }
                }
                .decodeSingle<OfferDto>()

            Result.success(offer)
        } catch (exception: Exception) {
            Log.e("CompanyOffersRepository", "Erro ao carregar detalhe da oferta", exception)
            Result.failure(exception)
        }
    }

    suspend fun getOfferById(
        offerId: String
    ): Result<CompanyOfferDto> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            val offer = supabase
                .from("offers")
                .select {
                    filter {
                        eq("id", offerId)
                        eq("company_profile_id", companyProfileId)
                    }
                }
                .decodeSingle<CompanyOfferDto>()

            Result.success(offer)
        } catch (exception: Exception) {
            Log.e("CompanyOffersRepository", "Erro ao carregar detalhe da oferta", exception)
            Result.failure(exception)
        }
    }

    suspend fun updateOffer(
        offerId: String,
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
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            supabase
                .from("offers")
                .update(
                    UpdateOfferDto(
                        title = title.trim(),
                        description = description.trim(),
                        area = area.trim(),
                        location = location.trim(),
                        workMode = workMode.trim(),
                        duration = duration.trim(),
                        vacancies = vacancies,
                        requirements = requirements.trim()
                    )
                ) {
                    filter {
                        eq("id", offerId)
                        eq("company_profile_id", companyProfileId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("CompanyOffersRepository", "Erro ao atualizar oferta", exception)
            Result.failure(exception)
        }
    }

    suspend fun updateOffer(
        offerId: String,
        request: CompanyOfferUpdateDto
    ): Result<CompanyOfferDto> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            supabase
                .from("offers")
                .update(request) {
                    filter {
                        eq("id", offerId)
                        eq("company_profile_id", companyProfileId)
                    }
                }

            val updatedOffer = supabase
                .from("offers")
                .select {
                    filter {
                        eq("id", offerId)
                        eq("company_profile_id", companyProfileId)
                    }
                }
                .decodeSingle<CompanyOfferDto>()

            Result.success(updatedOffer)
        } catch (exception: Exception) {
            Log.e("CompanyOffersRepository", "Erro ao atualizar oferta", exception)
            Result.failure(exception)
        }
    }

    suspend fun deactivateOffer(
        offerId: String
    ): Result<Unit> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            supabase
                .from("offers")
                .update(
                    UpdateOfferActiveDto(
                        isActive = false
                    )
                ) {
                    filter {
                        eq("id", offerId)
                        eq("company_profile_id", companyProfileId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("CompanyOffersRepository", "Erro ao desativar oferta", exception)
            Result.failure(exception)
        }
    }

    suspend fun activateOffer(
        offerId: String
    ): Result<Unit> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            supabase
                .from("offers")
                .update(
                    UpdateOfferActiveDto(
                        isActive = true
                    )
                ) {
                    filter {
                        eq("id", offerId)
                        eq("company_profile_id", companyProfileId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("CompanyOffersRepository", "Erro ao ativar oferta", exception)
            Result.failure(exception)
        }
    }
}