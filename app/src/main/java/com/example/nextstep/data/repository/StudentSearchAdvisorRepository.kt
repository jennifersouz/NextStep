package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.TeacherDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest

class StudentSearchAdvisorRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getAllTeachers(): Result<List<TeacherDto>> {
        return try {
            val currentUserId = auth.currentSessionOrNull()?.user?.id
            Log.d("AdvisorDebug", "=================== ADVISOR SEARCH DIAGNOSTIC ===================")
            Log.d("AdvisorDebug", "Auth User ID: ${currentUserId ?: "NULL - NOT AUTHENTICATED"}")

            // Get student profile to find education_institution
            val studentResponse = supabase
                .from("students")
                .select {
                    filter {
                        eq("profile_id", currentUserId ?: "")
                    }
                }
            val studentList = try {
                studentResponse.decodeList<Map<String, Any?>>()
            } catch (e: Exception) {
                emptyList()
            }
            Log.d("AdvisorDebug", "Student Profile Raw Response: $studentList")
            if (studentList.isNotEmpty()) {
                val student = studentList[0]
                val educationInstitution = student["education_institution"] as? String ?: "NULL"
                Log.d("AdvisorDebug", "Student Profile Found - education_institution: '$educationInstitution'")
            } else {
                Log.d("AdvisorDebug", "Student Profile: NOT FOUND for profile_id = $currentUserId")
            }

            // Call RPC function (SECURITY DEFINER) to bypass RLS
            Log.d("AdvisorDebug", "Calling RPC: get_teachers_from_student_institution()")
            val teachersResponse = supabase.postgrest.rpc(
                function = "get_teachers_from_student_institution"
            )
            val rawResponse = teachersResponse.data
            Log.d("AdvisorDebug", "RPC Raw Response: $rawResponse")
            val teachersList = teachersResponse.decodeList<TeacherDto>()
            Log.d("AdvisorDebug", "Teachers found via RPC: ${teachersList.size}")
            teachersList.forEach { teacher ->
                Log.d("AdvisorDebug", "  -> Teacher: ${teacher.displayFullName} (profile_id=${teacher.profileId})")
            }

            Log.d("AdvisorDebug", "=================== END DIAGNOSTIC ===================")
            Result.success(teachersList)

        } catch (exception: Exception) {
            Log.e("AdvisorDebug", "ERRO AO CARREGAR PROFESSORES", exception)
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