package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdminCreateUserRequest
import com.example.nextstep.data.model.AdminProfileDto
import com.example.nextstep.data.model.AdminProfileUpdateDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.Instant

class AdminUsersRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getUsers(): Result<List<AdminProfileDto>> {
        return try {
            val profiles = supabase
                .from("profiles")
                .select {
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<AdminProfileDto>()

            Log.d("AdminUsersRepo", "Users loaded: ${profiles.size}")
            Result.success(profiles)
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Error loading users", exception)
            Result.failure(exception)
        }
    }

    suspend fun createUser(request: AdminCreateUserRequest): Result<Unit> {
        return try {
            Log.d("AdminUsersRepo", "Calling edge function admin-create-user for ${request.email}")
            
            val response = supabase.functions.invoke("admin-create-user", request)
            
            if (response.status.isSuccess()) {
                Log.d("AdminUsersRepo", "User created successfully via edge function")
                Result.success(Unit)
            } else {
                val body = response.bodyAsText()
                Log.e("AdminUsersRepo", "Edge function error: ${response.status} - $body")
                
                val errorMessage = try {
                    val json = Json.parseToJsonElement(body).jsonObject
                    json["error"]?.jsonPrimitive?.content 
                        ?: json["message"]?.jsonPrimitive?.content 
                        ?: "Erro desconhecido do servidor"
                } catch (e: Exception) {
                    "Erro ao criar utilizador (${response.status.value})"
                }
                
                Result.failure(Exception(errorMessage))
            }
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Exception calling edge function", exception)
            Result.failure(exception)
        }
    }

    suspend fun getUserById(userId: String): Result<AdminProfileDto?> {
        return try {
            val profiles = supabase
                .from("profiles")
                .select {
                    filter { eq("id", userId) }
                }
                .decodeList<AdminProfileDto>()
            Result.success(profiles.firstOrNull())
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Error loading user id=$userId", exception)
            Result.failure(exception)
        }
    }

    suspend fun updateUser(
        userId: String,
        updateData: AdminProfileUpdateDto
    ): Result<Unit> {
        return try {
            Log.d("AdminUsersRepo", "Updating user id=$userId")
            Log.d("AdminUsersRepo", "Payload user update = $updateData")

            val payload = updateData.copy(
                updatedAt = Instant.now().toString()
            )

            supabase
                .from("profiles")
                .update(payload) {
                    filter { eq("id", userId) }
                }

            Log.d("AdminUsersRepo", "User updated successfully id=$userId")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Error updating user id=$userId", exception)
            Result.failure(exception)
        }
    }

    suspend fun setUserActive(userId: String, isActive: Boolean): Result<Unit> {
        return try {
            Log.d("AdminUsersRepo", "Setting user active=$isActive id=$userId")

            supabase
                .from("profiles")
                .update(
                    AdminProfileUpdateDto(
                        isActive = isActive,
                        updatedAt = Instant.now().toString()
                    )
                ) {
                    filter { eq("id", userId) }
                }

            Log.d("AdminUsersRepo", "User active state updated: id=$userId active=$isActive")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Error setting user active id=$userId", exception)
            Result.failure(exception)
        }
    }

    // Soft delete: desativa a conta em vez de apagar para não quebrar foreign keys
    suspend fun deactivateUser(userId: String): Result<Unit> {
        return try {
            Log.d("AdminUsersRepo", "Deactivating (soft delete) user id=$userId")

            supabase
                .from("profiles")
                .update(
                    AdminProfileUpdateDto(
                        isActive = false,
                        updatedAt = Instant.now().toString()
                    )
                ) {
                    filter { eq("id", userId) }
                }

            Log.d("AdminUsersRepo", "User deactivated successfully id=$userId")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Error deactivating user id=$userId", exception)
            Result.failure(exception)
        }
    }

    // Hard delete — apenas usado se explicitamente necessário
    suspend fun deleteUserProfile(userId: String): Result<Unit> {
        return try {
            Log.d("AdminUsersRepo", "Deleting user id=$userId")

            supabase
                .from("profiles")
                .delete {
                    filter { eq("id", userId) }
                }

            Log.d("AdminUsersRepo", "User deleted successfully id=$userId")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Error deleting user id=$userId", exception)
            Result.failure(exception)
        }
    }
}