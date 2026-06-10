package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.InstitutionProfileDto
import com.example.nextstep.data.model.UpdateInstitutionProfileDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class InstitutionProfileRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getInstitutionProfile(): Result<InstitutionProfileDto> {
        return try {
            val userId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Instituição não autenticada.")

            val profile = supabase
                .from("institution_profile_view")
                .select {
                    filter {
                        eq("profile_id", userId)
                    }
                }
                .decodeSingleOrNull<InstitutionProfileDto>()
                ?: throw IllegalStateException("Perfil da instituição não encontrado.")

            Result.success(profile)
        } catch (exception: Exception) {
            Log.e("InstitutionProfileRepository", "Erro ao carregar perfil da instituição", exception)
            Result.failure(exception)
        }
    }

    suspend fun updateInstitutionProfile(
        name: String,
        nif: String?,
        locality: String?,
        address: String?,
        phone: String?
    ): Result<Unit> {
        return try {
            val userId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Instituição não autenticada.")

            supabase
                .from("institutions")
                .update(
                    UpdateInstitutionProfileDto(
                        name = name.trim(),
                        nif = nif?.trim()?.takeIf { it.isNotBlank() },
                        locality = locality?.trim()?.takeIf { it.isNotBlank() },
                        address = address?.trim()?.takeIf { it.isNotBlank() },
                        phone = phone?.trim()?.takeIf { it.isNotBlank() }
                    )
                ) {
                    filter {
                        eq("profile_id", userId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("InstitutionProfileRepository", "Erro ao atualizar perfil da instituição", exception)
            Result.failure(exception)
        }
    }
}