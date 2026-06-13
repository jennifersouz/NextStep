package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdminCompanyDto
import com.example.nextstep.data.model.AdminCompanyOfferDto
import com.example.nextstep.data.model.AdminCompanyUpdateDto
import com.example.nextstep.data.model.AdminOfferDto
import com.example.nextstep.data.model.CreateCompanyDto
import com.example.nextstep.data.remote.SupabaseClientProvider
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
            Log.d("AdminCompaniesRepo", "Updating company id=$companyId")
            Log.d("AdminCompaniesRepo", "Payload company update = $data")

            val payload = data.copy(
                updatedAt = Instant.now().toString()
            )

            supabase
                .from("companies")
                .update(payload) {
                    filter { eq("id", companyId) }
                }

            Log.d("AdminCompaniesRepo", "Company updated successfully id=$companyId")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Error updating company id=$companyId", exception)
            Result.failure(exception)
        }
    }

    suspend fun setCompanyActive(companyId: String, isActive: Boolean): Result<Unit> {
        return try {
            Log.d("AdminCompaniesRepo", "Setting company active=$isActive id=$companyId")

            supabase
                .from("companies")
                .update(
                    AdminCompanyUpdateDto(
                        isActive = isActive,
                        updatedAt = Instant.now().toString()
                    )
                ) {
                    filter { eq("id", companyId) }
                }

            Log.d("AdminCompaniesRepo", "Company active state updated: id=$companyId active=$isActive")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Error setting company active id=$companyId", exception)
            Result.failure(exception)
        }
    }

    // Soft delete: desativa a empresa em vez de apagar
    suspend fun deactivateCompany(companyId: String): Result<Unit> {
        return try {
            Log.d("AdminCompaniesRepo", "Deactivating (soft delete) company id=$companyId")

            supabase
                .from("companies")
                .update(
                    AdminCompanyUpdateDto(
                        isActive = false,
                        updatedAt = Instant.now().toString()
                    )
                ) {
                    filter { eq("id", companyId) }
                }

            Log.d("AdminCompaniesRepo", "Company deactivated successfully id=$companyId")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Error deactivating company id=$companyId", exception)
            Result.failure(exception)
        }
    }

    // Hard delete
    suspend fun deleteCompany(companyId: String): Result<Unit> {
        return try {
            Log.d("AdminCompaniesRepo", "Deleting company id=$companyId")

            supabase
                .from("companies")
                .delete {
                    filter { eq("id", companyId) }
                }

            Log.d("AdminCompaniesRepo", "Company deleted successfully id=$companyId")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminCompaniesRepo", "Error deleting company id=$companyId", exception)
            Result.failure(exception)
        }
    }

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
