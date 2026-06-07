package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CompanyStudentProfileDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from

class CompanyStudentProfileRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getStudentProfile(
        applicationId: String
    ): Result<CompanyStudentProfileDto> {
        return try {
            val profiles = supabase
                .from("company_student_profiles_view")
                .select {
                    filter {
                        eq("application_id", applicationId)
                    }
                }
                .decodeList<CompanyStudentProfileDto>()

            val profile = profiles.firstOrNull()
                ?: return Result.failure(
                    IllegalStateException("STUDENT_PROFILE_NOT_FOUND")
                )

            Result.success(profile)
        } catch (exception: Exception) {
            Log.e(
                "CompanyStudentProfile",
                "Erro ao carregar perfil do aluno",
                exception
            )
            Result.failure(exception)
        }
    }
}