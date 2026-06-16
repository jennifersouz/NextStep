package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.TeacherAssignmentUpdateDto
import com.example.nextstep.data.model.TeacherOrientationRequestDto
import com.example.nextstep.data.model.TeacherRequestStatusUpdateDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlin.time.Duration.Companion.minutes

class TeacherRequestsRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getRequests(): Result<List<TeacherOrientationRequestDto>> {
        return try {
            auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            val requests = supabase
                .from("teacher_orientation_requests_view")
                .select()
                .decodeList<TeacherOrientationRequestDto>()

            Result.success(requests)
        } catch (exception: Exception) {
            Log.e("TeacherRequestsRepo", "Error fetching requests", exception)
            Result.failure(exception)
        }
    }

    suspend fun getRequestDetail(applicationId: String): Result<TeacherOrientationRequestDto> {
        return try {
            val request = supabase
                .from("teacher_orientation_requests_view")
                .select {
                    filter {
                        eq("application_id", applicationId)
                    }
                }
                .decodeSingle<TeacherOrientationRequestDto>()
            Result.success(request)
        } catch (exception: Exception) {
            Log.e("TeacherRequestsRepo", "Error fetching request detail", exception)
            Result.failure(exception)
        }
    }

    suspend fun acceptRequest(applicationId: String): Result<Unit> {
        Log.d("TeacherRequestsRepo", "Accepting applicationId=$applicationId")
        return updateRequestStatus(applicationId, "accepted")
    }

    suspend fun rejectRequest(applicationId: String): Result<Unit> {
        Log.d("TeacherRequestsRepo", "Rejecting applicationId=$applicationId")
        return updateRequestStatus(applicationId, "rejected")
    }

    private suspend fun updateRequestStatus(applicationId: String, status: String): Result<Unit> {
        return try {
            val userId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador autenticado não encontrado")

            supabase
                .from("teacher_requests")
                .update(TeacherRequestStatusUpdateDto(status = status)) {
                    filter {
                        eq("application_id", applicationId)
                        eq("teacher_profile_id", userId)
                    }
                }

            // Sync the applications table for backwards compatibility
            if (status == "accepted") {
                supabase
                    .from("applications")
                    .update(
                        TeacherAssignmentUpdateDto(
                            teacherProfileId = userId,
                            teacherStatus = "accepted"
                        )
                    ) {
                        filter { eq("id", applicationId) }
                    }
            }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("TeacherRequestsRepo", "Error updating request status for applicationId=$applicationId", exception)
            Result.failure(exception)
        }
    }

    suspend fun getDocumentUrl(path: String): Result<String> {
        if (path.isBlank()) {
            Log.e("TeacherRequestsRepo", "Document path is blank")
            return Result.failure(IllegalArgumentException("Caminho do documento não disponível."))
        }
        
        return try {
            Log.d("TeacherRequestsRepo", "Generating signed URL for path: '$path' in bucket 'application-documents'")
            val bucket = supabase.storage.from("application-documents")
            val url = bucket.createSignedUrl(path = path, expiresIn = 1.minutes)
            Log.d("TeacherRequestsRepo", "Successfully generated signed URL for path: $path")
            Result.success(url)
        } catch (exception: Exception) {
            Log.e("TeacherRequestsRepo", "Error generating signed URL for path: $path. Exception: ${exception.message}", exception)
            
            val friendlyMessage = when {
                exception.message?.contains("Object not found", ignoreCase = true) == true || 
                exception.message?.contains("404") == true ->
                    "Documento não encontrado no Storage."
                else -> "Não foi possível aceder ao documento."
            }
            Result.failure(Exception(friendlyMessage))
        }
    }
}
