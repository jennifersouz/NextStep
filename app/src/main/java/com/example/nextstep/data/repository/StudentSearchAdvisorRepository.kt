package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.TeacherDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from

class StudentSearchAdvisorRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getAllTeachers(): Result<List<TeacherDto>> {
        return try {
            val response = supabase
                .from("teachers")
                .select()

            Log.d("SearchAdvisorRepo", "Resultado bruto: ${response.data}")

            val teachers = response.decodeList<TeacherDto>()

            Log.d("SearchAdvisorRepo", "Quantidade carregada: ${teachers.size}")

            Result.success(teachers)
        } catch (exception: Exception) {
            Log.e("SearchAdvisorRepo", "Erro ao carregar todos os professores", exception)
            Result.failure(exception)
        }
    }

    suspend fun sendOrientationRequest(
        internshipId: String,
        teacherProfileId: String
    ): Result<Unit> {
        return try {
            supabase
                .from("applications")
                .update(
                    mapOf(
                        "teacher_profile_id" to teacherProfileId,
                        "teacher_status" to "pending"
                    )
                ) {
                    filter {
                        eq("id", internshipId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("SearchAdvisorRepo", "Erro ao enviar pedido de orientação", exception)
            Result.failure(exception)
        }
    }
}
