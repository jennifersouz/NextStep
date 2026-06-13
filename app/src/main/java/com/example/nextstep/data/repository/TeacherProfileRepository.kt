package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.TeacherProfileDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class TeacherProfileRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getTeacherProfile(): Result<TeacherProfileDto> {
        return try {
            val currentUser = auth.currentUserOrNull()
                ?: return Result.failure(
                    IllegalStateException("Utilizador não autenticado.")
                )

            val profiles = supabase
                .from("teachers")
                .select {
                    filter {
                        eq("profile_id", currentUser.id)
                    }
                }
                .decodeList<TeacherProfileDto>()

            val profile = profiles.firstOrNull()
                ?: return Result.failure(
                    IllegalStateException("TEACHER_PROFILE_NOT_FOUND")
                )

            Result.success(profile)
        } catch (exception: Exception) {
            Log.e("TeacherProfileRepo", "Erro ao carregar perfil do docente", exception)
            Result.failure(exception)
        }
    }

    suspend fun updateTeacherProfile(
        firstName: String,
        lastName: String,
        phone: String?,
        department: String?
    ): Result<Unit> {
        return try {
            val currentUser = auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            val userId = currentUser.id

            // Update data in teachers table (first_name, last_name, phone, department are in teachers)
            supabase
                .from("teachers")
                .update({
                    set("first_name", firstName.trim())
                    set("last_name", lastName.trim())
                    set("phone", phone?.trim()?.takeIf { it.isNotBlank() })
                    set("department", department?.trim()?.takeIf { it.isNotBlank() })
                }) {
                    filter {
                        eq("profile_id", userId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("TeacherProfileRepo", "Erro ao atualizar perfil do docente", exception)
            Result.failure(exception)
        }
    }
}