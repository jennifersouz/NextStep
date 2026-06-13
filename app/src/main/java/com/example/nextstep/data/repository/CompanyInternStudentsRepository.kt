package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CompanyInternStudentDto
import com.example.nextstep.data.model.CompanyInternStudentStatusUpdateDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class CompanyInternStudentsRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    private val validInternshipStatuses = setOf(
        "accepted", "active", "in_progress", "inactive", "completed"
    )

    /**
     * RF23: Fetches all intern students for the authenticated company.
     * Queries the company_intern_students_view which already filters by
     * company_profile_id = auth.uid() and valid internship statuses.
     */
    suspend fun getInternStudents(): Result<List<CompanyInternStudentDto>> {
        return try {
            val currentUserId = auth.currentUserOrNull()?.id
                ?: return Result.failure(
                    IllegalStateException("EMPLOYER_NOT_AUTHENTICATED")
                )

            val students = supabase
                .from("company_intern_students_view")
                .select()
                .decodeList<CompanyInternStudentDto>()

            Result.success(students)
        } catch (exception: Exception) {
            Log.e(
                "CompanyInternStudentsRepo",
                "Erro ao carregar alunos em estágio",
                exception
            )
            Result.failure(exception)
        }
    }

    /**
     * RF23: Fetches a single intern student by applicationId.
     * Validates that the application belongs to the authenticated company
     * and has a valid internship status.
     */
    suspend fun getInternStudentByApplicationId(
        applicationId: String
    ): Result<CompanyInternStudentDto> {
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

            val students = supabase
                .from("company_intern_students_view")
                .select {
                    filter {
                        eq("application_id", applicationId)
                    }
                }
                .decodeList<CompanyInternStudentDto>()

            val student = students.firstOrNull()
                ?: return Result.failure(
                    IllegalStateException("PERMISSION_DENIED")
                )

            Result.success(student)
        } catch (exception: Exception) {
            Log.e(
                "CompanyInternStudentsRepo",
                "Erro ao carregar aluno em estágio",
                exception
            )
            Result.failure(exception)
        }
    }

    /**
     * RF24: Updates the internship status of a student application.
     * Validates that the application belongs to the authenticated company.
     */
    suspend fun updateInternStudentStatus(
        applicationId: String,
        newStatus: String
    ): Result<CompanyInternStudentDto> {
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

            if (newStatus !in listOf("active", "inactive")) {
                return Result.failure(
                    IllegalArgumentException("INVALID_STATUS")
                )
            }

            // Update the application status
            supabase.from("applications").update(
                CompanyInternStudentStatusUpdateDto(status = newStatus)
            ) {
                filter {
                    eq("id", applicationId)
                    eq("company_profile_id", currentUserId)
                }
            }

            // Fetch the updated student info from the view
            val updatedStudentResult = getInternStudentByApplicationId(applicationId)
            
            if (updatedStudentResult.isSuccess) {
                Result.success(updatedStudentResult.getOrThrow())
            } else {
                Result.failure(updatedStudentResult.exceptionOrNull() ?: Exception("Unknown error after update"))
            }
        } catch (exception: Exception) {
            Log.e(
                "CompanyInternStudentsRepo",
                "Erro ao atualizar estado do aluno",
                exception
            )
            Result.failure(exception)
        }
    }
}
