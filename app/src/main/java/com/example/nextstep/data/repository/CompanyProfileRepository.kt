package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CompanyNameDto
import com.example.nextstep.data.model.CompanyProfileDto
import com.example.nextstep.data.model.OfferDto
import com.example.nextstep.data.model.UpdateCompanyProfileDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class CompanyProfileRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getCurrentCompanyProfile(): Result<CompanyProfileDto> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            val company = supabase
                .from("companies")
                .select {
                    filter {
                        eq("profile_id", companyProfileId)
                    }
                }
                .decodeSingle<CompanyProfileDto>()

            Result.success(company)
        } catch (exception: Exception) {
            Log.e(
                "CompanyProfileRepository",
                "Erro ao carregar perfil da empresa",
                exception
            )
            Result.failure(exception)
        }
    }

    suspend fun getCompanyProfileById(profileId: String): Result<CompanyProfileDto> {
        Log.d("CompanyProfileDebug", "Buscando empresa pelo ID: $profileId")
        return try {
            val response = supabase
                .from("companies")
                .select {
                    filter {
                        or {
                            eq("profile_id", profileId)
                            eq("id", profileId)
                        }
                    }
                }
                .decodeList<CompanyProfileDto>()

            Log.d("CompanyProfileDebug", "Empresas encontradas: ${response.size}")
            Log.d("CompanyProfileDebug", "Resultado completo: $response")

            val company = response.firstOrNull()
            if (company != null) {
                Result.success(company)
            } else {
                Result.failure(NoSuchElementException("Perfil da empresa não encontrado"))
            }
        } catch (exception: Exception) {
            Log.e(
                "CompanyProfileRepository",
                "Erro ao carregar perfil da empresa com id: $profileId",
                exception
            )
            Result.failure(exception)
        }
    }

    suspend fun getCompanyOffersById(profileId: String): Result<List<OfferDto>> {
        return try {
            val companyResponse = supabase
                .from("companies")
                .select {
                    filter {
                        or {
                            eq("profile_id", profileId)
                            eq("id", profileId)
                        }
                    }
                }
                .decodeList<CompanyProfileDto>()

            val company = companyResponse.firstOrNull()
            val targetProfileId = company?.profileId ?: profileId

            val offers = supabase
                .from("offers")
                .select {
                    filter {
                        eq("company_profile_id", targetProfileId)
                    }
                }
                .decodeList<OfferDto>()

            val currentName = company?.companyName
            val updatedOffers = if (currentName != null) {
                offers.map { it.copy(companyName = currentName) }
            } else {
                offers
            }

            Result.success(updatedOffers)
        } catch (exception: Exception) {
            Log.e(
                "CompanyProfileRepository",
                "Erro ao carregar estágios da empresa com id: $profileId",
                exception
            )
            Result.failure(exception)
        }
    }

    suspend fun getCurrentCompanyOffers(): Result<List<OfferDto>> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            val offers = supabase
                .from("offers")
                .select {
                    filter {
                        eq("company_profile_id", companyProfileId)
                    }
                }
                .decodeList<OfferDto>()

            val currentName = try {
                val company = supabase
                    .from("companies")
                    .select(columns = Columns.list("company_name")) {
                        filter {
                            eq("profile_id", companyProfileId)
                        }
                    }
                    .decodeSingle<CompanyNameDto>()
                company.companyName
            } catch (e: Exception) {
                Log.e("CompanyProfileRepository", "Erro ao buscar nome atual da empresa", e)
                null
            }

            val updatedOffers = if (currentName != null) {
                offers.map { it.copy(companyName = currentName) }
            } else {
                offers
            }

            Result.success(updatedOffers)
        } catch (exception: Exception) {
            Log.e(
                "CompanyProfileRepository",
                "Erro ao carregar estágios da empresa",
                exception
            )
            Result.failure(exception)
        }
    }

    suspend fun updateCurrentCompanyProfile(
        companyName: String,
        businessArea: String,
        location: String,
        description: String,
        phone: String
    ): Result<Unit> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            supabase
                .from("companies")
                .update(
                    UpdateCompanyProfileDto(
                        companyName = companyName.trim(),
                        businessArea = businessArea.trim(),
                        location = location.trim(),
                        description = description.trim(),
                        phone = phone.trim()
                    )
                ) {
                    filter {
                        eq("profile_id", companyProfileId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e(
                "CompanyProfileRepository",
                "Erro ao atualizar perfil da empresa",
                exception
            )
            Result.failure(exception)
        }
    }
}