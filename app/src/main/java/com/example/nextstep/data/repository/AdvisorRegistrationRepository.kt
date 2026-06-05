package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest

class AdvisorRegistrationRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun registerAdvisor(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            auth.signUpWith(Email) {
                this.email = email.trim().lowercase()
                this.password = password
            }

            val currentUser = auth.currentUserOrNull()
                ?: throw IllegalStateException(
                    "Conta criada, mas ainda não existe sessão ativa. Verifica se a confirmação por email está ativa no Supabase."
                )

            supabase.postgrest.rpc("accept_advisor_invite")

            Log.d(
                "AdvisorRegistration",
                "Orientador registado e convite aceite: ${currentUser.id}"
            )

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdvisorRegistration", "Erro ao registar orientador", exception)
            Result.failure(exception)
        }
    }

    suspend fun acceptInviteAfterLoginIfNeeded(): Result<Unit> {
        return try {
            supabase.postgrest.rpc("accept_advisor_invite")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdvisorRegistration", "Erro ao aceitar convite após login", exception)
            Result.failure(exception)
        }
    }
}