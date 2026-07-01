package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdminApplicationDto
import com.example.nextstep.data.model.AdminOfferDto
import com.example.nextstep.data.model.AdminProfileDto
import com.example.nextstep.data.model.AdminTeacherEvaluationDto
import com.example.nextstep.data.model.AdminCompanyDto
import com.example.nextstep.data.model.ProfileDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class AdminDashboardRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getActiveInternshipsCount(): Result<Int> {
        return try {
            val offers = supabase
                .from("offers")
                .select {
                    filter {
                        eq("is_active", true)
                    }
                }
                .decodeList<AdminOfferDto>()
            Result.success(offers.size)
        } catch (exception: Exception) {
            Log.e("AdminDashboardRepo", "Erro ao contar estágios ativos", exception)
            Result.failure(exception)
        }
    }

    suspend fun getApplicationsCount(): Result<Int> {
        return try {
            val applications = supabase
                .from("applications")
                .select()
                .decodeList<AdminApplicationDto>()
            Result.success(applications.size)
        } catch (exception: Exception) {
            Log.e("AdminDashboardRepo", "Erro ao contar candidaturas", exception)
            Result.failure(exception)
        }
    }

    suspend fun getCompletedEvaluationsCount(): Result<Int> {
        return try {
            val evaluations = supabase
                .from("teacher_evaluations")
                .select()
                .decodeList<AdminTeacherEvaluationDto>()
            Result.success(evaluations.size)
        } catch (exception: Exception) {
            Log.e("AdminDashboardRepo", "Erro ao contar avaliações concluídas", exception)
            Result.failure(exception)
        }
    }

    suspend fun getUsersCount(): Result<Int> {
        return try {
            val profiles = supabase
                .from("profiles")
                .select()
                .decodeList<AdminProfileDto>()
            Result.success(profiles.size)
        } catch (exception: Exception) {
            Log.e("AdminDashboardRepo", "Erro ao contar utilizadores", exception)
            Result.failure(exception)
        }
    }

    suspend fun getAdminProfileName(): Result<String> {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            val userId = currentUser.id

            val profile = supabase
                .from("profiles")
                .select {
                    filter { eq("id", userId) }
                }
                .decodeList<AdminProfileDto>()
                .firstOrNull()

            if (profile != null) {
                Log.d("AdminDashboardRepo", "Loaded admin profile: firstName=${profile.firstName}, lastName=${profile.lastName}, email=${profile.email}")

                val fullName = listOfNotNull(
                    profile.firstName?.takeIf { it.isNotBlank() },
                    profile.lastName?.takeIf { it.isNotBlank() }
                ).joinToString(" ")

                Result.success(fullName.ifBlank { profile.email ?: "Administrador" })
            } else {
                Log.w("AdminDashboardRepo", "Profile not found for userId=$userId, falling back to Auth email")
                Result.success(currentUser.email ?: "Administrador")
            }
        } catch (exception: Exception) {
            Log.e("AdminDashboardRepo", "Erro ao obter perfil", exception)
            Result.failure(exception)
        }
    }

    suspend fun getAdminProfileEmail(): Result<String> {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            val userId = currentUser.id

            val profile = supabase
                .from("profiles")
                .select {
                    filter { eq("id", userId) }
                }
                .decodeList<AdminProfileDto>()
                .firstOrNull()

            val email = profile?.email ?: currentUser.email ?: ""
            Result.success(email)
        } catch (exception: Exception) {
            Log.e("AdminDashboardRepo", "Erro ao obter email do perfil", exception)
            // Fallback to Auth email
            Result.success(supabase.auth.currentUserOrNull()?.email ?: "")
        }
    }

    suspend fun getTotalCompaniesCount(): Result<Int> {
        return try {
            val companies = supabase
                .from("companies")
                .select()
                .decodeList<AdminCompanyDto>()
            Result.success(companies.size)
        } catch (exception: Exception) {
            Log.e("AdminDashboardRepo", "Erro ao contar empresas", exception)
            Result.failure(exception)
        }
    }

    suspend fun getActiveCompaniesCount(): Result<Int> {
        return try {
            val companies = supabase
                .from("companies")
                .select {
                    filter {
                        eq("is_active", true)
                    }
                }
                .decodeList<AdminCompanyDto>()
            Result.success(companies.size)
        } catch (exception: Exception) {
            Log.e("AdminDashboardRepo", "Erro ao contar empresas ativas", exception)
            Result.failure(exception)
        }
    }

    suspend fun updateAdminProfileName(name: String): Result<Unit> {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            val parts = name.trim().split(" ", limit = 2)
            val firstName = parts.getOrElse(0) { "" }
            val lastName = parts.getOrElse(1) { "" }

            supabase
                .from("profiles")
                .update({
                    set("first_name", firstName.trim())
                    set("last_name", lastName.trim())
                }) {
                    filter { eq("id", currentUser.id) }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminDashboardRepo", "Erro ao atualizar perfil do admin", exception)
            Result.failure(exception)
        }
    }

    suspend fun getRecentProfiles(): Result<List<ProfileDto>> {
        return try {
            val profiles = supabase
                .from("profiles")
                .select {
                    order("created_at", Order.DESCENDING)
                    limit(5)
                }
                .decodeList<ProfileDto>()

            Result.success(profiles)
        } catch (exception: Exception) {
            Log.e("AdminDashboardRepo", "Erro ao carregar perfis recentes", exception)
            Result.success(emptyList())
        }
    }
}
