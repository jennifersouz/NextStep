package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.StudentSubmittedApplicationDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class StudentInternshipsRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getStudentInternships(): Result<List<StudentSubmittedApplicationDto>> {
        return try {
            val userId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador não autenticado.")

            val applications = supabase
                .from("student_applications_view")
                .select {
                    filter {
                        eq("student_profile_id", userId)
                    }
                }
                .decodeList<StudentSubmittedApplicationDto>()
                .filter { it.status == "accepted" || it.status == "completed" }

            Result.success(applications)
        } catch (exception: Exception) {
            Log.e("StudentInternshipsRepo", "Erro ao carregar estágios", exception)
            Result.failure(exception)
        }
    }
}
