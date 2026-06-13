package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdminCompanyDto
import com.example.nextstep.data.model.AdminCompanyEditRequest
import com.example.nextstep.data.model.AdminCompanyOfferDto
import com.example.nextstep.data.model.AdminCompanyUpdateDto
import com.example.nextstep.data.model.AdminOfferDto
import com.example.nextstep.data.model.CompanyActiveUpdateDto
import com.example.nextstep.data.model.CompanyArchiveUpdateDto
import com.example.nextstep.data.model.CreateCompanyDto
import com.example.nextstep.data.model.OfferDeactivateUpdateDto
import com.example.nextstep.data.model.ProfileActiveUpdateDto
import com.example.nextstep.data.model.ProfileArchiveUpdateDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import java.time.Instant

class AdminCompaniesRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getCompanies(): Result<List<AdminCompanyDto>> {
        return try {
            val companies = supabase
                .from("companies")
                .select {
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<AdminCompanyDto>()

            Log.d("AdminCompaniesRepo", "Companies loaded: ${companies.size}")
            Result.success(companies)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Error loading companies", exception)
            Result.failure(exception)
        }
    }

    suspend fun getNonArchivedCompanies(): Result<List<AdminCompanyDto>> {
        return try {
            val allCompanies = supabase
                .from("companies")
                .select {
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<AdminCompanyDto>()

            val companies = allCompanies.filter { it.archivedAt == null }

            Log.d("AdminCompaniesRepo", "Non-archived companies loaded: ${companies.size}")
            Result.success(companies)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Error loading non-archived companies", exception)
            Result.failure(exception)
        }
    }

    suspend fun getCompaniesWithOfferCounts(): Result<List<AdminCompanyDto>> {
        return try {
            val companies = supabase
                .from("companies")
                .select {
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<AdminCompanyDto>()

            val offers = supabase
                .from("offers")
                .select()
                .decodeList<AdminOfferDto>()

            val offerCountMap = offers
                .mapNotNull { it.companyProfileId }
                .groupingBy { it }
                .eachCount()

            val companiesWithCounts = companies.map { company ->
                val profileId = company.profileId
                val count = if (profileId != null) offerCountMap[profileId] ?: 0 else 0
                company.copy(offersCount = count)
            }

            Log.d("AdminCompaniesRepo", "Companies reloaded: ${companiesWithCounts.size}")
            Result.success(companiesWithCounts)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Error loading companies with offer counts", exception)
            Result.failure(exception)
        }
    }

    suspend fun getCompanyById(companyId: String): Result<AdminCompanyDto?> {
        return try {
            val companies = supabase
                .from("companies")
                .select {
                    filter { eq("id", companyId) }
                }
                .decodeList<AdminCompanyDto>()
            Result.success(companies.firstOrNull())
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Error loading company id=$companyId", exception)
            Result.failure(exception)
        }
    }

    suspend fun createCompany(data: CreateCompanyDto): Result<AdminCompanyDto> {
        return try {
            Log.d("AdminCompaniesRepo", "Creating company: ${data.companyName}")

            val company = supabase
                .from("companies")
                .insert(data) {
                    select()
                }
                .decodeSingle<AdminCompanyDto>()

            Log.d("AdminCompaniesRepo", "Company created successfully id=${company.id}")
            Result.success(company)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Error creating company", exception)
            Result.failure(exception)
        }
    }

    suspend fun updateCompany(companyId: String, data: AdminCompanyUpdateDto): Result<Unit> {
        return try {
            Log.d("AdminCompaniesRepo", "Updating company id=$companyId (legacy)")
            val payload = data.copy(updatedAt = Instant.now().toString())
            supabase.from("companies").update(payload) { filter { eq("id", companyId) } }
            Log.d("AdminCompaniesRepo", "Company updated (legacy) id=$companyId")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Error updating company id=$companyId", exception)
            Result.failure(exception)
        }
    }

    /**
     * Editar empresa com validação, update de companies + profiles, e confirmação via select.
     * Nunca envia archived_at / archived_by / archive_reason.
     */
    suspend fun editCompany(
        companyId: String,
        companyProfileId: String,
        request: AdminCompanyEditRequest
    ): Result<AdminCompanyDto> {
        return try {
            if (companyId.isBlank()) throw IllegalArgumentException("ID da empresa está vazio.")
            if (companyProfileId.isBlank()) throw IllegalArgumentException("ID do perfil da empresa está vazio.")
            if (request.companyName.isBlank()) throw IllegalArgumentException("Nome da empresa é obrigatório.")

            Log.d(
                "AdminCompaniesRepo",
                "editCompany companyId=$companyId companyProfileId=$companyProfileId isActive=${request.isActive}"
            )

            // 1. Atualizar tabela companies
            supabase
                .from("companies")
                .update(request) {
                    filter { eq("id", companyId) }
                }

            // 2. Atualizar is_active no profile associado
            supabase
                .from("profiles")
                .update(
                    ProfileActiveUpdateDto(
                        isActive = request.isActive,
                        updatedAt = request.updatedAt
                    )
                ) {
                    filter { eq("id", companyProfileId) }
                }

            // 3. Confirmar com select
            val companies = supabase
                .from("companies")
                .select { filter { eq("id", companyId) } }
                .decodeList<AdminCompanyDto>()

            val updated = companies.firstOrNull()
                ?: throw IllegalStateException("Empresa não encontrada após atualização.")

            Log.d(
                "AdminCompaniesRepo",
                "Updated company loaded: id=${updated.id}, isActive=${updated.isActive}, name=${updated.companyName}"
            )

            Result.success(updated)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Error editing company id=$companyId", exception)
            Result.failure(exception)
        }
    }

    // ===== Account status management =====

    /**
     * Desativar acesso: marca is_active = false na empresa e no perfil
     */
    suspend fun deactivateCompany(companyId: String, companyProfileId: String): Result<Unit> {
        return try {
            Log.d("AdminCompaniesRepo", "Deactivating company id=$companyId, profile=$companyProfileId")

            val now = Instant.now().toString()

            // Desativar empresa
            supabase
                .from("companies")
                .update(
                    CompanyActiveUpdateDto(isActive = false, updatedAt = now)
                ) {
                    filter { eq("id", companyId) }
                }

            // Desativar perfil associado
            supabase
                .from("profiles")
                .update(
                    ProfileActiveUpdateDto(isActive = false, updatedAt = now)
                ) {
                    filter { eq("id", companyProfileId) }
                }

            Log.d("AdminCompaniesRepo", "Company deactivated successfully id=$companyId")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Error deactivating company id=$companyId", exception)
            Result.failure(exception)
        }
    }

    /**
     * Reativar acesso: is_active = true no perfil e empresa.
     */
    suspend fun reactivateCompany(companyId: String, companyProfileId: String): Result<Unit> {
        return try {
            Log.d("AdminCompaniesRepo", "Reactivating company id=$companyId, profile=$companyProfileId")

            val now = Instant.now().toString()

            supabase
                .from("companies")
                .update(
                    CompanyActiveUpdateDto(isActive = true, updatedAt = now)
                ) {
                    filter { eq("id", companyId) }
                }

            supabase
                .from("profiles")
                .update(
                    ProfileActiveUpdateDto(isActive = true, updatedAt = now)
                ) {
                    filter { eq("id", companyProfileId) }
                }

            Log.d("AdminCompaniesRepo", "Company reactivated successfully id=$companyId")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Error reactivating company id=$companyId", exception)
            Result.failure(exception)
        }
    }

    /**
     * Arquivar empresa: is_active = false em companies, profiles e offers ativas.
     * O adminId é obtido do utilizador autenticado — nunca recebido como parâmetro String
     * para evitar enviar "" (string vazia) como valor de UUID.
     */
    suspend fun archiveCompany(
        companyId: String,
        companyProfileId: String,
        reason: String?
    ): Result<Unit> {
        return try {
            if (companyId.isBlank()) {
                return Result.failure(IllegalArgumentException("ID da empresa está vazio."))
            }
            if (companyProfileId.isBlank()) {
                return Result.failure(IllegalArgumentException("ID do perfil da empresa está vazio."))
            }

            // Obter adminId do Auth — null se não disponível (nunca enviar "")
            val adminId = supabase.auth.currentUserOrNull()?.id?.takeIf { it.isNotBlank() }
            val cleanReason = reason?.takeIf { it.isNotBlank() }
            val now = Instant.now().toString()

            Log.d(
                "AdminCompaniesRepo",
                "archiveCompany companyId=$companyId companyProfileId=$companyProfileId adminId=$adminId reason=$cleanReason"
            )

            // 1. Arquivar empresa (só campos de arquivo — sem profile_id nem campos UUID a string vazia)
            supabase
                .from("companies")
                .update(
                    CompanyArchiveUpdateDto(
                        isActive = false,
                        archivedAt = now,
                        archivedBy = adminId,   // null se não autenticado — nunca ""
                        archiveReason = cleanReason,
                        updatedAt = now
                    )
                ) {
                    filter { eq("id", companyId) }
                }

            // 2. Arquivar perfil associado
            supabase
                .from("profiles")
                .update(
                    ProfileArchiveUpdateDto(
                        isActive = false,
                        archivedAt = now,
                        archivedBy = adminId,   // null se não autenticado — nunca ""
                        archiveReason = cleanReason,
                        updatedAt = now
                    )
                ) {
                    filter { eq("id", companyProfileId) }
                }

            // 3. Desativar ofertas ativas da empresa — apenas is_active, sem updated_at
            // (a tabela offers não tem coluna updated_at)
            supabase
                .from("offers")
                .update(OfferDeactivateUpdateDto(isActive = false)) {
                    filter {
                        eq("company_profile_id", companyProfileId)
                        eq("is_active", true)
                    }
                }

            Log.d("AdminCompaniesRepo", "Company archived successfully id=$companyId")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Error archiving company id=$companyId", exception)
            Result.failure(exception)
        }
    }

    // ===== Deletion impact and permanent delete were intentionally removed =====
    // The app does not support permanent deletion.
    // Allowed actions: deactivateCompany, reactivateCompany, archiveCompany.

    suspend fun getOffersByCompany(companyProfileId: String): Result<List<AdminCompanyOfferDto>> {
        return try {
            Log.d("AdminCompaniesRepo", "Loading offers for companyProfileId=$companyProfileId")

            val offers = supabase
                .from("offers")
                .select {
                    filter { eq("company_profile_id", companyProfileId) }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<AdminCompanyOfferDto>()

            Log.d("AdminCompaniesRepo", "Offers loaded: ${offers.size} for companyProfileId=$companyProfileId")
            Result.success(offers)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Error loading offers for companyProfileId=$companyProfileId", exception)
            Result.failure(exception)
        }
    }
}