package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdvisorDto
import com.example.nextstep.data.model.AdvisorInviteCreateDto
import com.example.nextstep.data.model.AdvisorInviteDto
import com.example.nextstep.data.model.CompanyAdvisorDto
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
                val activeDisplayName = advisor.name
                    .takeIf { it.isNotBlank() }
                    ?: advisor.email

                CompanyAdvisorDto(
                    id = advisor.profileId,
                    profileId = advisor.profileId,
                    name = activeDisplayName,
                    email = advisor.email,
                    phone = advisor.phone ?: "",
                    department = advisor.department ?: "",
                    status = "active"
                )
            }

            val pendingItems = invites
                .filter { invite ->
                    val inviteStatus = invite.status?.lowercase()?.trim() ?: "pending"
                    inviteStatus == "pending" &&
                            invite.acceptedAt == null &&
                            invite.email.trim().lowercase() !in activeEmails
                }
                .map { invite ->
                    val inviteDisplayName = invite.name
                        ?.takeIf { it.isNotBlank() }
                        ?: invite.email

                    val invitePhone = invite.phone
                        ?.takeIf { it.isNotBlank() }
                        ?: ""

                    val inviteDepartment = invite.department
                        ?.takeIf { it.isNotBlank() }
                        ?: ""

                    CompanyAdvisorDto(
                        id = invite.id,
                        profileId = "",
                        name = inviteDisplayName,
                        email = invite.email,
                        phone = invitePhone,
                        department = inviteDepartment,
                        status = "pending"
                    )
                }

            val result = (activeItems + pendingItems)
                .sortedWith(
                    compareBy<CompanyAdvisorDto> { advisor ->
                        if (advisor.status == "active") 0 else 1
                    }.thenBy { advisor ->
                        advisor.name
                            .ifBlank { advisor.email }
                            .lowercase()
                            .trim()
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

    suspend fun inviteAdvisor(email: String): Result<Unit> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            val normalizedEmail = email.trim().lowercase()

            // Verificar se já existe um convite pendente para este email
            val existingInvites = supabase
                .from("advisor_invites")
                .select {
                    filter {
                        eq("company_profile_id", companyProfileId)
                        eq("email", normalizedEmail)
                    }
                }
                .decodeList<AdvisorInviteDto>()

            val hasPendingInvite = existingInvites.any { invite ->
                val inviteStatus = invite.status?.lowercase()?.trim() ?: "pending"
                inviteStatus == "pending" && invite.acceptedAt == null
            }

            if (hasPendingInvite) {
                return Result.failure(
                    IllegalStateException("Já existe um convite pendente para este e-mail.")
                )
            }

            // Verificar se já existe um orientador ativo com este email
            val existingActive = supabase
                .from("advisors")
                .select {
                    filter {
                        eq("company_profile_id", companyProfileId)
                        eq("email", normalizedEmail)
                    }
                }
                .decodeList<AdvisorDto>()

            if (existingActive.isNotEmpty()) {
                return Result.failure(
                    IllegalStateException("Já existe um orientador ativo com este e-mail.")
                )
            }

            supabase
                .from("advisor_invites")
                .insert(
                    AdvisorInviteCreateDto(
                        companyProfileId = companyProfileId,
                        email = normalizedEmail,
                        status = "pending"
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

            // Tenta apagar como convite pendente - não crasha se não encontrar
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

            // Tenta apagar como orientador ativo - não crasha se não encontrar
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