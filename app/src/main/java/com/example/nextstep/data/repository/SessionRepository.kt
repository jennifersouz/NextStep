package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth

class SessionRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun logout(): Result<Unit> {
        return try {
            supabase.auth.signOut()
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("SessionRepository", "Erro ao terminar sessão", exception)
            Result.failure(exception)
        }
    }
}