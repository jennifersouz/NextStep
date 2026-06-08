package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CreateStudentAdvisorRequestDto
import com.example.nextstep.data.model.StudentAvailableAdvisorDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class StudentAdvisorsRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getAvailableAdvisors(): Result<List<StudentAvailableAdvisorDto>> {
        return try {
            val advisors = supabase
                .from("student_available_advisors_view")
                .select()
                .decodeList<StudentAvailableAdvisorDto>()
                .sortedBy { advisor ->
                    advisor.name.lowercase()
                }

            Result.success(advisors)
        } catch (exception: Exception) {
            Log.e("StudentAdvisorsRepo", "Erro ao carregar orientadores", exception)
            Result.failure(exception)
        }
    }

    suspend fun sendAdvisorRequest(
        advisorProfileId: String
    ): Result<Unit> {
        return try {
            val studentProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Aluno não autenticado.")

            supabase
                .from("student_advisor_requests")
                .insert(
                    CreateStudentAdvisorRequestDto(
                        studentProfileId = studentProfileId,
                        advisorProfileId = advisorProfileId
                    )
                )

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("StudentAdvisorsRepo", "Erro ao enviar pedido", exception)
            Result.failure(exception)
        }
    }
}