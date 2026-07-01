package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AdvisorRegistrationRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    /**
     * Check if there's a pending advisor invite for the given email.
     * Looks up advisor_invites with:
     * - lower(trim(email)) matches
     * - accepted_at IS NULL
     * - status IS NULL OR status = 'pending'
     */
    suspend fun hasPendingAdvisorInvite(email: String): Boolean {
        return try {
            val result = supabase.postgrest.rpc(
                function = "has_pending_advisor_invite",
                parameters = buildJsonObject {
                    put("invite_email", email.trim().lowercase())
                }
            )
            result.data.toString().toBoolean()
        } catch (exception: Exception) {
            Log.e("AdvisorRegistration", "Erro ao verificar convite pendente", exception)
            false
        }
    }

    /**
     * Full advisor registration flow:
     * 1. Validate pending invite exists
     * 2. Create Auth user with signUp
     * 3. Call accept_advisor_invite RPC with user data
     *
     * @param email Advisor email (normalized internally)
     * @param password Chosen password
     * @param name Full name (firstName + lastName combined)
     * @param phone Optional phone number
     * @param department Optional department
     * @return Result with success or specific error message key
     */
    suspend fun registerAdvisor(
        email: String,
        password: String,
        name: String? = null,
        phone: String? = null,
        department: String? = null
    ): Result<Unit> {
        return try {
            val normalizedEmail = email.trim().lowercase()
            Log.d("AdvisorRegistration", "registerAdvisor email=$normalizedEmail")

            // 1. Validate pending invite before signup
            val hasInvite = hasPendingAdvisorInvite(normalizedEmail)
            Log.d("AdvisorRegistration", "hasPendingAdvisorInvite=$hasInvite")

            if (!hasInvite) {
                return Result.failure(IllegalStateException("no_pending_advisor_invite"))
            }

            // 2. Create Auth user
            val createdUser = auth.signUpWith(Email) {
                this.email = normalizedEmail
                this.password = password
            }

            val currentUser = auth.currentUserOrNull()
                ?: throw IllegalStateException(
                    "Conta criada, mas ainda não existe sessão ativa. Verifica se a confirmação por email está ativa no Supabase."
                )

            Log.d("AdvisorRegistration", "Auth user created: ${currentUser.id}")

            // 3. Call RPC accept_advisor_invite with user data
            // If name is provided, we pass it; otherwise null (RPC will use invite name)
            val rpcName = name?.trim()?.takeIf { it.isNotBlank() }
            val rpcPhone = phone?.trim()?.takeIf { it.isNotBlank() }
            val rpcDepartment = department?.trim()?.takeIf { it.isNotBlank() }

            supabase.postgrest.rpc(
                function = "accept_advisor_invite",
                parameters = buildJsonObject {
                    put("user_name", rpcName)
                    put("user_phone", rpcPhone)
                    put("user_department", rpcDepartment)
                }
            )

            Log.d(
                "AdvisorRegistration",
                "Orientador registado e convite aceite: ${currentUser.id}"
            )

            Result.success(Unit)
        } catch (exception: Exception) {
            val errorMessage = exception.message?.lowercase() ?: ""

            // If user already exists, return friendly error
            if ("user_already_exists" in errorMessage || "already registered" in errorMessage) {
                Log.e("AdvisorRegistration", "User already exists: $email")
                return Result.failure(IllegalStateException("user_already_exists"))
            }

            // If RPC failed after signup, we have an orphan user
            if ("no_pending_advisor_invite" in errorMessage) {
                Log.e("AdvisorRegistration", "Invite not found after signup (orphan user)")
                return Result.failure(IllegalStateException("orphan_account"))
            }

            Log.e("AdvisorRegistration", "Erro ao registar orientador", exception)
            Result.failure(IllegalStateException("advisor_register_generic"))
        }
    }

    /**
     * Try to accept advisor invite after login.
     * Useful when user already exists and just needs to accept the invite.
     */
    suspend fun acceptInviteAfterLoginIfNeeded(): Result<Unit> {
        return try {
            supabase.postgrest.rpc(
                function = "accept_advisor_invite",
                parameters = buildJsonObject {
                    put("user_name", null)
                    put("user_phone", null)
                    put("user_department", null)
                }
            )
            Log.d("AdvisorRegistration", "Convite aceite após login")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdvisorRegistration", "Erro ao aceitar convite após login", exception)
            // If "no_pending_advisor_invite", it's not an error - just no pending invite
            if ("no_pending_advisor_invite" in (exception.message?.lowercase() ?: "")) {
                return Result.failure(IllegalStateException("no_pending_advisor_invite"))
            }
            Result.failure(exception)
        }
    }
}