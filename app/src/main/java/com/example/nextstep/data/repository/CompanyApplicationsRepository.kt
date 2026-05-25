package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CompanyApplicationDto
import com.example.nextstep.data.model.UpdateApplicationViewedDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from

class CompanyApplicationsRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getCompanyApplications(): Result<List<CompanyApplicationDto>> {
        return try {
            val applications = supabase
                .from("company_applications_view")
                .select()
                .decodeList<CompanyApplicationDto>()
                .sortedByDescending { application ->
                    application.createdAt.orEmpty()
                }

            Result.success(applications)
        } catch (exception: Exception) {
            Log.e(
                "CompanyApplicationsRepo",
                "Erro ao carregar candidaturas da empresa",
                exception
            )
            Result.failure(exception)
        }
    }

    suspend fun markApplicationAsViewed(
        applicationId: String
    ): Result<Unit> {
        return try {
            supabase
                .from("applications")
                .update(
                    UpdateApplicationViewedDto(
                        viewedByCompany = true
                    )
                ) {
                    filter {
                        eq("id", applicationId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e(
                "CompanyApplicationsRepo",
                "Erro ao marcar candidatura como vista",
                exception
            )
            Result.failure(exception)
        }
    }
}