package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.SentAdvisorRequestDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from

class StudentSentAdvisorRequestsRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getSentRequests(studentProfileId: String): Result<List<SentAdvisorRequestDto>> {
        return try {
            val allApplications = supabase
                .from("applications")
                .select {
                    filter {
                        eq("student_profile_id", studentProfileId)
                    }
                }
                .decodeList<SentAdvisorRequestDto>()

            val applications = allApplications.filter { !it.teacherProfileId.isNullOrBlank() }

            val enriched = applications.map { app ->
                if (app.teacherName.isNullOrBlank() && app.teacherProfileId != null) {
                    val name = fetchTeacherName(app.teacherProfileId)
                    app.copy(teacherName = name ?: "Orientador")
                } else {
                    app
                }
            }

            Result.success(enriched)
        } catch (exception: Exception) {
            Log.e("SentAdvisorReqRepo", "Erro ao carregar pedidos", exception)
            Result.failure(exception)
        }
    }

    private suspend fun fetchTeacherName(profileId: String): String? {
        return try {
            val response = supabase
                .from("profiles")
                .select {
                    filter {
                        eq("id", profileId)
                    }
                }
                .decodeSingleOrNull<Map<String, String>>()
            
            if (response != null) {
                val first = response["first_name"] ?: ""
                val last = response["last_name"] ?: ""
                "$first $last".trim().ifBlank { null }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun cancelRequest(applicationId: String): Result<Unit> {
        return try {
            supabase
                .from("applications")
                .update(
                    mapOf(
                        "teacher_profile_id" to null,
                        "teacher_status" to null
                    )
                ) {
                    filter {
                        eq("id", applicationId)
                    }
                }
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("SentAdvisorReqRepo", "Erro ao cancelar pedido", exception)
            Result.failure(exception)
        }
    }
}
