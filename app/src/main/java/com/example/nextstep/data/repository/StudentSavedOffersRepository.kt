package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CreateSavedOfferDto
import com.example.nextstep.data.model.OfferDto
import com.example.nextstep.data.model.SavedOfferDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class StudentSavedOffersRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun isOfferSaved(
        offerId: String
    ): Result<Boolean> {
        return try {
            val studentProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador não autenticado.")

            val savedOffers = supabase
                .from("saved_offers")
                .select {
                    filter {
                        eq("student_profile_id", studentProfileId)
                        eq("offer_id", offerId)
                    }
                }
                .decodeList<SavedOfferDto>()

            Result.success(savedOffers.isNotEmpty())
        } catch (exception: Exception) {
            Log.e("StudentSavedOffersRepo", "Erro ao verificar oferta guardada", exception)
            Result.failure(exception)
        }
    }

    suspend fun saveOffer(
        offerId: String
    ): Result<Unit> {
        return try {
            val studentProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador não autenticado.")

            val alreadySaved = isOfferSaved(offerId).getOrDefault(false)

            if (!alreadySaved) {
                supabase
                    .from("saved_offers")
                    .insert(
                        CreateSavedOfferDto(
                            studentProfileId = studentProfileId,
                            offerId = offerId
                        )
                    )
            }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("StudentSavedOffersRepo", "Erro ao guardar oferta", exception)
            Result.failure(exception)
        }
    }

    suspend fun unsaveOffer(
        offerId: String
    ): Result<Unit> {
        return try {
            val studentProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador não autenticado.")

            supabase
                .from("saved_offers")
                .delete {
                    filter {
                        eq("student_profile_id", studentProfileId)
                        eq("offer_id", offerId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("StudentSavedOffersRepo", "Erro ao remover oferta guardada", exception)
            Result.failure(exception)
        }
    }

    suspend fun getSavedOffers(): Result<List<OfferDto>> {
        return try {
            val offers = supabase
                .from("student_saved_offers_view")
                .select()
                .decodeList<OfferDto>()

            Result.success(offers)
        } catch (exception: Exception) {
            Log.e("StudentSavedOffersRepo", "Erro ao carregar ofertas guardadas", exception)
            Result.failure(exception)
        }
    }
}