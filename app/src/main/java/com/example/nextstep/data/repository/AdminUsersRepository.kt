package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.ProfileDto
import com.example.nextstep.data.model.UpdateProfileDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class AdminUsersRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getUsers(): Result<List<ProfileDto>> {
        return try {
            val profiles = supabase
                .from("profiles")
                .select {
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<ProfileDto>()

            Result.success(profiles)
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Erro ao carregar utilizadores", exception)
            Result.failure(exception)
        }
    }

    suspend fun searchUsers(query: String): Result<List<ProfileDto>> {
        return try {
            val profiles = supabase
                .from("profiles")
                .select {
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<ProfileDto>()

            val filtered = profiles.filter { profile ->
                profile.email.contains(query, ignoreCase = true) ||
                        profile.firstName?.contains(query, ignoreCase = true) == true ||
                        profile.lastName?.contains(query, ignoreCase = true) == true
            }

            Result.success(filtered)
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Erro ao pesquisar utilizadores", exception)
            Result.failure(exception)
        }
    }

    suspend fun updateUser(
        userId: String,
        updateData: UpdateProfileDto
    ): Result<Unit> {
        return try {
            supabase
                .from("profiles")
                .update(updateData) {
                    filter {
                        eq("id", userId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Erro ao atualizar utilizador", exception)
            Result.failure(exception)
        }
    }

    suspend fun setUserActive(userId: String, isActive: Boolean): Result<Unit> {
        return try {
            supabase
                .from("profiles")
                .update(
                    UpdateProfileDto(isActive = isActive)
                ) {
                    filter {
                        eq("id", userId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Erro ao alterar estado do utilizador", exception)
            Result.failure(exception)
        }
    }

    suspend fun deleteUserProfile(userId: String): Result<Unit> {
        return try {
            supabase
                .from("profiles")
                .delete {
                    filter {
                        eq("id", userId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Erro ao remover utilizador", exception)
            Result.failure(exception)
        }
    }
}