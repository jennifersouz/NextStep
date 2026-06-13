package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdminCompanyDto
import com.example.nextstep.data.model.AdminCompanyUpdateDto
import com.example.nextstep.data.model.AdminOfferDto
import com.example.nextstep.data.model.CreateCompanyDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

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

            Result.success(companies)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Erro ao carregar empresas", exception)
            Result.failure(exception)
        }
    }

    suspend fun getCompaniesWithOfferCounts(): Result<List<AdminCompanyDto>> {
        return try {
            // Get all companies
            val companies = supabase
                .from("companies")
                .select {
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<AdminCompanyDto>()

            // Get counts of offers per company using a concrete DTO
            val offers = supabase
                .from("offers")
                .select()
                .decodeList<AdminOfferDto>()

            // Count offers per company_profile_id
            val offerCountMap = offers
                .mapNotNull { it.companyProfileId }
                .groupingBy { it }
                .eachCount()

            // Merge counts into companies
            val companiesWithCounts = companies.map { company ->
                val profileId = company.profileId
                val count = if (profileId != null) offerCountMap[profileId] ?: 0 else 0
                company.copy(offersCount = count)
            }

            Result.success(companiesWithCounts)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Erro ao carregar empresas com contagem de ofertas", exception)
            Result.failure(exception)
        }
    }

    suspend fun getCompanyById(companyId: String): Result<AdminCompanyDto?> {
        return try {
            val companies = supabase
                .from("companies")
                .select {
                    filter {
                        eq("id", companyId)
                    }
                }
                .decodeList<AdminCompanyDto>()

            Result.success(companies.firstOrNull())
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Erro ao carregar empresa", exception)
            Result.failure(exception)
        }
    }

    suspend fun createCompany(data: CreateCompanyDto): Result<AdminCompanyDto> {
        return try {
            val company = supabase
                .from("companies")
                .insert(data) {
                    select()
                }
                .decodeSingle<AdminCompanyDto>()

            Result.success(company)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Erro ao criar empresa", exception)
            Result.failure(exception)
        }
    }

    suspend fun updateCompany(companyId: String, data: AdminCompanyUpdateDto): Result<Unit> {
        return try {
            supabase
                .from("companies")
                .update(data) {
                    filter {
                        eq("id", companyId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Erro ao atualizar empresa", exception)
            Result.failure(exception)
        }
    }

    suspend fun setCompanyActive(companyId: String, isActive: Boolean): Result<Unit> {
        return try {
            supabase
                .from("companies")
                .update(
                    AdminCompanyUpdateDto(isActive = isActive)
                ) {
                    filter {
                        eq("id", companyId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Erro ao alterar estado da empresa", exception)
            Result.failure(exception)
        }
    }

    suspend fun deleteCompany(companyId: String): Result<Unit> {
        return try {
            supabase
                .from("companies")
                .delete {
                    filter {
                        eq("id", companyId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Erro ao remover empresa", exception)
            Result.failure(exception)
        }
    }
}
