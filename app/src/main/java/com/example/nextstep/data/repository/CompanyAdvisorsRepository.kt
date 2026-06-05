package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdvisorDto
import com.example.nextstep.data.model.AdvisorInviteDto
import com.example.nextstep.data.model.CompanyAdvisorDto
import com.example.nextstep.data.model.CreateCompanyAdvisorDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class CompanyAdvisorsRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getAdvisors(): Result<List<CompanyAdvisorDto>> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            val invites = supabase
                .from("advisor_invites")
                .select {
                    filter {
                        eq("company_profile_id", companyProfileId)
                    }
                }
                .decodeList<AdvisorInviteDto>()

            val activeAdvisors = supabase
                .from("advisors")
                .select {
                    filter {
                        eq("company_profile_id", companyProfileId)
                    }
                }
                .decodeList<AdvisorDto>()

            val activeEmails = activeAdvisors
                .map { advisor -> advisor.email.lowercase().trim() }
                .toSet()

            val activeItems = activeAdvisors.map { advisor ->
                CompanyAdvisorDto(
                    id = advisor.profileId,
                    profileId = advisor.profileId,
                    name = advisor.name,
                    email = advisor.email,
                    phone = advisor.phone,
                    department = advisor.department,
                    status = "active"
                )
            }

            val pendingItems = invites
                .filter { invite ->
                    invite.status == "pending" &&
                            invite.email.lowercase().trim() !in activeEmails
                }
                .map { invite ->
                    CompanyAdvisorDto(
                        id = invite.id,
                        profileId = invite.advisorProfileId,
                        name = invite.name,
                        email = invite.email,
                        phone = invite.phone,
                        department = invite.department,
                        status = "pending"
                    )
                }

            val result = (activeItems + pendingItems)
                .sortedWith(
                    compareBy<CompanyAdvisorDto> { advisor ->
                        if (advisor.status == "active") 0 else 1
                    }.thenBy { advisor ->
                        advisor.name.lowercase()
                    }
                )

            Result.success(result)
        } catch (exception: Exception) {
            Log.e("CompanyAdvisorsRepo", "Erro ao carregar orientadores", exception)
            Result.failure(exception)
        }
    }

    suspend fun getAdvisorById(
        advisorId: String
    ): Result<CompanyAdvisorDto> {
        return try {
            val advisors = getAdvisors().getOrThrow()

            val advisor = advisors.firstOrNull { item ->
                item.id == advisorId || item.profileId == advisorId
            } ?: throw IllegalStateException("Orientador não encontrado.")

            Result.success(advisor)
        } catch (exception: Exception) {
            Log.e("CompanyAdvisorsRepo", "Erro ao carregar detalhe do orientador", exception)
            Result.failure(exception)
        }
    }

    suspend fun createAdvisor(
        name: String,
        email: String,
        phone: String,
        department: String
    ): Result<Unit> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            supabase
                .from("advisor_invites")
                .insert(
                    CreateCompanyAdvisorDto(
                        companyProfileId = companyProfileId,
                        name = name.trim(),
                        email = email.trim().lowercase(),
                        phone = phone.trim().ifBlank { null },
                        department = department.trim().ifBlank { null }
                    )
                )

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("CompanyAdvisorsRepo", "Erro ao criar convite de orientador", exception)
            Result.failure(exception)
        }
    }

    suspend fun deleteAdvisor(
        advisorId: String
    ): Result<Unit> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            // Tenta apagar como convite pendente
            runCatching {
                supabase
                    .from("advisor_invites")
                    .delete {
                        filter {
                            eq("id", advisorId)
                            eq("company_profile_id", companyProfileId)
                        }
                    }
            }

            // Tenta apagar como orientador ativo
            runCatching {
                supabase
                    .from("advisors")
                    .delete {
                        filter {
                            eq("profile_id", advisorId)
                            eq("company_profile_id", companyProfileId)
                        }
                    }
            }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("CompanyAdvisorsRepo", "Erro ao eliminar orientador", exception)
            Result.failure(exception)
        }
    }
}