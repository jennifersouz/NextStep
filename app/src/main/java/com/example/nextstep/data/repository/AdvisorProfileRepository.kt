package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdvisorProfileDto
import com.example.nextstep.data.model.UpdateAdvisorProfileDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class AdvisorProfileRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getAdvisorProfile(): Result<AdvisorProfileDto> {
        return try {
            val currentUser = auth.currentUserOrNull()
                ?: return Result.failure(
                    IllegalStateException("Utilizador não autenticado.")
                )

            val profiles = supabase
                .from("advisors")
                .select {
                    filter {
                        eq("profile_id", currentUser.id)
                    }
                }
                .decodeList<AdvisorProfileDto>()

            val profile = profiles.firstOrNull()
                ?: return Result.failure(
                    IllegalStateException("ADVISOR_PROFILE_NOT_FOUND")
                )

            Result.success(profile)
        } catch (exception: Exception) {
            Log.e("AdvisorProfileRepo", "Erro ao carregar perfil do orientador", exception)
            Result.failure(exception)
        }
    }

    suspend fun updateAdvisorProfile(
        name: String,
        phone: String?,
        department: String?
    ): Result<Unit> {
        return try {
            val currentUser = auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            supabase
                .from("advisors")
                .update(
                    UpdateAdvisorProfileDto(
                        name = name.trim(),
                        phone = phone?.trim()?.takeIf { it.isNotBlank() },
                        department = department?.trim()?.takeIf { it.isNotBlank() }
                    )
                ) {
                    filter {
                        eq("profile_id", currentUser.id)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdvisorProfileRepo", "Erro ao atualizar perfil do orientador", exception)
            Result.failure(exception)
        }
    }
}