package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepository {

    private val auth = SupabaseClientProvider.client.auth

    suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AuthRepository", "Erro ao fazer login", exception)
            Result.failure(exception)
        }
    }

    suspend fun register(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AuthRepository", "Erro ao criar conta", exception)
            Result.failure(exception)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}