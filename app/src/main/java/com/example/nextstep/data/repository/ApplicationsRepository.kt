package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.ApplicationDto
import com.example.nextstep.data.model.CreateApplicationDto
import com.example.nextstep.data.model.StudentSubmittedApplicationDto
import com.example.nextstep.data.model.UpdateApplicationReportDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage

class ApplicationsRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun submitApplication(
        offerId: String,
        companyProfileId: String,
        motivationLetterFileName: String,
        motivationLetterBytes: ByteArray,
        cvFileName: String,
        cvBytes: ByteArray
    ): Result<Unit> {
        return try {
            val studentProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador não autenticado.")

            val existingApplications = supabase
                .from("applications")
                .select {
                    filter {
                        eq("offer_id", offerId)
                        eq("student_profile_id", studentProfileId)
                    }
                }
                .decodeList<ApplicationDto>()

            if (existingApplications.isNotEmpty()) {
                throw IllegalStateException("already_applied")
            }

            val timestamp = System.currentTimeMillis()

            val motivationPath =
                "$studentProfileId/$offerId/motivation_${timestamp}_${sanitizeFileName(motivationLetterFileName)}"

            val cvPath =
                "$studentProfileId/$offerId/cv_${timestamp}_${sanitizeFileName(cvFileName)}"

            val bucket = supabase.storage.from("application-documents")

            bucket.upload(
                path = motivationPath,
                data = motivationLetterBytes
            ) {
                upsert = true
            }

            bucket.upload(
                path = cvPath,
                data = cvBytes
            ) {
                upsert = true
            }

            supabase.from("applications").insert(
                CreateApplicationDto(
                    offerId = offerId,
                    studentProfileId = studentProfileId,
                    companyProfileId = companyProfileId,
                    motivationLetterPath = motivationPath,
                    cvPath = cvPath,
                    status = "pending"
                )
            )

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("ApplicationsRepository", "Erro ao submeter candidatura", exception)
            Result.failure(exception)
        }
    }

    suspend fun hasCurrentStudentApplied(
        offerId: String
    ): Result<Boolean> {
        return try {
            val studentProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador não autenticado.")

            val applications = supabase
                .from("applications")
                .select {
                    filter {
                        eq("offer_id", offerId)
                        eq("student_profile_id", studentProfileId)
                    }
                }
                .decodeList<ApplicationDto>()

            Result.success(applications.isNotEmpty())
        } catch (exception: Exception) {
            Log.e("ApplicationsRepository", "Erro ao verificar candidatura existente", exception)
            Result.failure(exception)
        }
    }

    suspend fun uploadReport(
        application: StudentSubmittedApplicationDto,
        reportFileName: String,
        reportBytes: ByteArray
    ): Result<Unit> {
        return try {
            val studentProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador não autenticado.")

            if (application.studentProfileId != studentProfileId) {
                throw IllegalStateException("not_owner")
            }

            val timestamp = System.currentTimeMillis()
            val reportPath =
                "$studentProfileId/${application.offerId}/report_${timestamp}_${sanitizeFileName(reportFileName)}"

            val bucket = supabase.storage.from("application-documents")

            bucket.upload(
                path = reportPath,
                data = reportBytes
            ) {
                upsert = true
            }

            // Update report_path in the database
            supabase.from("applications").update(
                UpdateApplicationReportDto(reportPath = reportPath)
            ) {
                filter {
                    eq("id", application.id)
                }
            }

            Result.success(Unit)
        } catch (exception: RestException) {
            Log.e("ApplicationsRepository", "Erro Rest ao anexar relatório", exception)
            Result.failure(exception)
        } catch (exception: Exception) {
            Log.e("ApplicationsRepository", "Erro ao anexar relatório", exception)
            Result.failure(exception)
        }
    }

    private fun sanitizeFileName(fileName: String): String {
        return fileName.replace(Regex("[^A-Za-z0-9._-]"), "_")
    }
}