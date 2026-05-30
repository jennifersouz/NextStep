package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.StudentSubmittedApplicationDto
import com.example.nextstep.data.model.UpdateStudentPresenceDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlin.time.Duration.Companion.minutes

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

    suspend fun getSubmittedApplicationById(
        applicationId: String
    ): Result<StudentSubmittedApplicationDto> {
        return try {
            val currentStudentId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador não autenticado.")

            val application = supabase
                .from("student_applications_view")
                .select {
                    filter {
                        eq("id", applicationId)
                        eq("student_profile_id", currentStudentId)
                    }
                }
                .decodeSingle<StudentSubmittedApplicationDto>()

            Result.success(application)
        } catch (exception: Exception) {
            Log.e(
                "StudentApplicationsRepo",
                "Erro ao carregar detalhe da candidatura submetida",
                exception
            )
            Result.failure(exception)
        }
    }

    suspend fun confirmPresence(
        applicationId: String
    ): Result<Unit> {
        return try {
            val currentStudentId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador não autenticado.")

            supabase
                .from("applications")
                .update(
                    UpdateStudentPresenceDto(
                        studentPresenceConfirmed = true
                    )
                ) {
                    filter {
                        eq("id", applicationId)
                        eq("student_profile_id", currentStudentId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e(
                "StudentApplicationsRepo",
                "Erro ao confirmar presença",
                exception
            )
            Result.failure(exception)
        }
    }

    suspend fun createSignedDocumentUrl(
        documentPath: String
    ): Result<String> {
        return try {
            val bucket = supabase.storage.from("application-documents")

            val signedUrl = bucket.createSignedUrl(
                path = documentPath,
                expiresIn = 5.minutes
            )

            Result.success(signedUrl)
        } catch (exception: Exception) {
            Log.e(
                "StudentApplicationsRepo",
                "Erro ao gerar URL do documento",
                exception
            )
            Result.failure(exception)
        }
    }
}