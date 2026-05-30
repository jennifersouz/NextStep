package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.StudentSubmittedApplicationDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class StudentApplicationsRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getSubmittedApplications(): Result<List<StudentSubmittedApplicationDto>> {
        return try {
            auth.currentUserOrNull()
                ?: throw IllegalStateException("Utilizador não autenticado.")

            val applications = supabase
                .from("student_applications_view")
                .select()
                .decodeList<StudentSubmittedApplicationDto>()
                .sortedByDescending { application ->
                    application.createdAt.orEmpty()
                }

            Result.success(applications)
        } catch (exception: Exception) {
            Log.e(
                "StudentApplicationsRepo",
                "Erro ao carregar candidaturas submetidas",
                exception
            )
            Result.failure(exception)
        }
    }
}