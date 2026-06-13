package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CompanyOfferDto
import com.example.nextstep.data.model.CompanyOfferUpdateDto
import com.example.nextstep.data.model.OfferActiveStatusUpdateDto
import com.example.nextstep.data.model.OfferArchiveUpdateDto
import com.example.nextstep.data.model.OfferDto
import com.example.nextstep.data.model.UpdateOfferDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import java.time.Instant

class CompanyOffersRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getMyOffers(): Result<List<CompanyOfferDto>> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            val offers = supabase
                .from("offers")
                .select {
                    filter {
                        eq("company_profile_id", companyProfileId)
                    }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<CompanyOfferDto>()

            Result.success(offers)
        } catch (exception: Exception) {
            Log.e("CompanyOffersRepository", "Erro ao carregar ofertas da empresa", exception)
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

    /**
     * Altera apenas o estado is_active da oferta.
     * Não mexe em archived_at, archived_by nem archive_reason.
     */
    suspend fun changeOfferActiveStatus(
        offerId: String,
        isActive: Boolean
    ): Result<OfferDto> {
        return try {
            val currentUserId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            if (offerId.isBlank()) {
                throw IllegalArgumentException("offerId não pode estar vazio.")
            }

            supabase
                .from("offers")
                .update(
                    OfferActiveStatusUpdateDto(isActive = isActive)
                ) {
                    filter {
                        eq("id", offerId)
                        eq("company_profile_id", currentUserId)
                    }
                }

            val updatedOffer = supabase
                .from("offers")
                .select {
                    filter {
                        eq("id", offerId)
                        eq("company_profile_id", currentUserId)
                    }
                }
                .decodeSingle<OfferDto>()

            Result.success(updatedOffer)
        } catch (exception: Exception) {
            Log.e("CompanyOffersRepository", "Erro ao alterar estado da oferta", exception)
            Result.failure(exception)
        }
    }

    /**
     * Arquiva a oferta: is_active = false, archived_at = now, archived_by = currentUser, archive_reason = motivo.
     * Não apaga a oferta.
     */
    suspend fun archiveOffer(
        offerId: String,
        reason: String?
    ): Result<OfferDto> {
        return try {
            val currentUserId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            if (offerId.isBlank()) {
                throw IllegalArgumentException("offerId não pode estar vazio.")
            }

            val now = Instant.now().toString()

            supabase
                .from("offers")
                .update(
                    OfferArchiveUpdateDto(
                        isActive = false,
                        archivedAt = now,
                        archivedBy = currentUserId.takeIf { it.isNotBlank() },
                        archiveReason = reason?.takeIf { it.isNotBlank() }
                    )
                ) {
                    filter {
                        eq("id", offerId)
                        eq("company_profile_id", currentUserId)
                    }
                }

            val updatedOffer = supabase
                .from("offers")
                .select {
                    filter {
                        eq("id", offerId)
                        eq("company_profile_id", currentUserId)
                    }
                }
                .decodeSingle<OfferDto>()

            Result.success(updatedOffer)
        } catch (exception: Exception) {
            Log.e("CompanyOffersRepository", "Erro ao arquivar oferta", exception)
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
                    OfferActiveStatusUpdateDto(
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
                    OfferActiveStatusUpdateDto(
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