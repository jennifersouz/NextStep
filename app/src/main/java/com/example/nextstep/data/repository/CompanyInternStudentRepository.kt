package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CompanyInternStudentProfileDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlin.time.Duration.Companion.minutes

class CompanyInternStudentRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    private val validInternshipStatuses = setOf(
        "accepted", "active", "in_progress", "completed"
    )

    /**
     * RF26: Fetches the intern student profile for a given applicationId.
     * Validates that:
     *  1. The application belongs to the authenticated company.
     *  2. The application status is a valid internship status (accepted/active/in_progress/completed).
     */
    suspend fun getInternStudentProfile(
        applicationId: String
    ): Result<CompanyInternStudentProfileDto> {
        return try {
            val currentUserId = auth.currentUserOrNull()?.id
                ?: return Result.failure(
                    IllegalStateException("EMPLOYER_NOT_AUTHENTICATED")
                )

            if (applicationId.isBlank()) {
                return Result.failure(
                    IllegalArgumentException("APPLICATION_ID_EMPTY")
                )
            }

            val profiles = supabase
                .from("company_intern_students_view")
                .select {
                    filter {
                        eq("application_id", applicationId)
                        eq("company_profile_id", currentUserId)
                    }
                }
                .decodeList<CompanyInternStudentProfileDto>()

            val profile = profiles.firstOrNull()
                ?: return Result.failure(
                    IllegalStateException("PERMISSION_DENIED")
                )

            // Validate that the application status is a valid internship status
            val status = profile.internshipStatus?.trim()?.lowercase()
            if (status == null || status !in validInternshipStatuses) {
                return Result.failure(
                    IllegalStateException("NOT_IN_INTERNSHIP")
                )
            }

            Result.success(profile)
        } catch (exception: Exception) {
            Log.e("CompanyInternStudentRepo", "Erro ao carregar perfil do aluno em estágio", exception)
            Result.failure(exception)
        }
    }

    /**
     * Creates a signed URL for a document in storage (CV, motivation letter, etc.).
     */
    suspend fun createSignedDocumentUrl(documentPath: String): Result<String> {
        return try {
            val bucket = supabase.storage.from("application-documents")
            val signedUrl = bucket.createSignedUrl(documentPath, expiresIn = 30.minutes)
            Result.success(signedUrl)
        } catch (exception: Exception) {
            Log.e("CompanyInternStudentRepo", "Erro ao criar signed URL", exception)
            Result.failure(exception)
        }
    }
}