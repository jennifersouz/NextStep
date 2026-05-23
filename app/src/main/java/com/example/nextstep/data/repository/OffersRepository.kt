package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.OfferDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from

class OffersRepository {

    private val supabase = SupabaseClientProvider.client

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

            Result.success(offers)
        } catch (exception: Exception) {
            Log.e("OffersRepository", "Erro ao buscar ofertas", exception)
            Result.failure(exception)
        }
    }
}