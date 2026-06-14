package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.SentAdvisorRequestDto
import com.example.nextstep.data.model.TeacherRequestDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from

class StudentSentAdvisorRequestsRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getSentRequests(studentProfileId: String): Result<List<SentAdvisorRequestDto>> {
        return try {
            val appResponse = supabase
                .from("applications")
                .select {
                    filter {
                        eq("student_profile_id", studentProfileId)
                    }
                }
                .decodeList<Map<String, String>>()

            val appIds = appResponse.mapNotNull { it["id"] }

            if (appIds.isEmpty()) {
                return Result.success(emptyList())
            }

            val teacherRequests = supabase
                .from("teacher_requests")
                .select {
                    filter {
                        isIn("application_id", appIds)
                    }
                }
                .decodeList<TeacherRequestDto>()

            val nonCancelled = teacherRequests.filter { it.status != "cancelled" }

            val enriched = nonCancelled.map { req ->
                val teacherName = fetchTeacherName(req.teacherProfileId) ?: "Orientador"
                SentAdvisorRequestDto(
                    id = req.id,
                    studentProfileId = studentProfileId,
                    teacherProfileId = req.teacherProfileId,
                    teacherStatus = req.status,
                    createdAt = req.createdAt,
                    teacherName = teacherName
                )
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

    suspend fun cancelRequest(requestId: String): Result<Unit> {
        return try {
            supabase
                .from("teacher_requests")
                .update(
                    mapOf("status" to "cancelled")
                ) {
                    filter {
                        eq("id", requestId)
                    }
                }
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("SentAdvisorReqRepo", "Erro ao cancelar pedido", exception)
            Result.failure(exception)
        }
    }
}
