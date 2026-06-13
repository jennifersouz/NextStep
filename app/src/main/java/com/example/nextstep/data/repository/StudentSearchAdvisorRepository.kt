package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.TeacherDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from

class StudentSearchAdvisorRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getAllTeachers(): Result<List<TeacherDto>> {
        return try {
            // 1. Tentativa na tabela 'teachers'
            val teachersResponse = supabase
                .from("teachers")
                .select()
            
            val teachersList = teachersResponse.decodeList<TeacherDto>()
            if (teachersList.isNotEmpty()) {
                return Result.success(teachersList)
            }

            // 2. Fallback na view de orientadores (alguns sistemas tratam professores como orientadores)
            try {
                val advisorsResponse = supabase
                    .from("student_available_advisors_view")
                    .select()
                val advisors = advisorsResponse.decodeList<TeacherDto>()
                if (advisors.isNotEmpty()) {
                    return Result.success(advisors)
                }
            } catch (e: Exception) {
                Log.d("SearchAdvisorRepo", "View 'student_available_advisors_view' not found or empty")
            }

            // 3. Fallback na tabela 'profiles' filtrando pela role 'teacher'
            val profilesResponse = supabase
                .from("profiles")
                .select {
                    filter {
                        eq("role", "teacher")
                    }
                }
            
            val profilesList = profilesResponse.decodeList<TeacherDto>()
            Result.success(profilesList)

        } catch (exception: Exception) {
            Log.e("SearchAdvisorRepo", "Erro ao carregar professores", exception)
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
