package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.StudentProfileDto
import com.example.nextstep.data.model.TeacherDto
import com.example.nextstep.data.model.TeacherRequestCreateDto
import com.example.nextstep.data.model.TeacherRequestDto
import com.example.nextstep.data.model.TeacherRequestStatusUpdateDto
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
            Log.d("TeacherRequestDebug", "=================== ADVISOR SEARCH DIAGNOSTIC ===================")
            Log.d("TeacherRequestDebug", "Auth User ID: ${currentUserId ?: "NULL - NOT AUTHENTICATED"}")

            val studentResponse = supabase
                .from("students")
                .select {
                    filter {
                        eq("profile_id", currentUserId ?: "")
                    }
                }

            Log.d("TeacherRequestDebug", "Student Profile raw response: ${studentResponse.data}")

            val studentList = try {
                studentResponse.decodeList<StudentProfileDto>()
            } catch (e: Exception) {
                Log.e("TeacherRequestDebug", "Error decoding student profile", e)
                emptyList()
            }

            Log.d("TeacherRequestDebug", "Student Profile decoded: $studentList")

            if (studentList.isNotEmpty()) {
                val student = studentList[0]
                val educationInstitution = student.educationInstitution ?: "NULL"
                Log.d("TeacherRequestDebug", "Student Profile Found - education_institution: '$educationInstitution'")
            } else {
                Log.d("TeacherRequestDebug", "Student Profile: NOT FOUND for profile_id = $currentUserId")
            }

            Log.d("TeacherRequestDebug", "Calling RPC: get_teachers_from_student_institution()")
            val teachersResponse = supabase.postgrest.rpc(
                function = "get_teachers_from_student_institution"
            )
            val rawResponse = teachersResponse.data
            Log.d("TeacherRequestDebug", "RPC Raw Response: $rawResponse")
            val teachersList = teachersResponse.decodeList<TeacherDto>()
            Log.d("TeacherRequestDebug", "Teachers found via RPC: ${teachersList.size}")
            teachersList.forEach { teacher ->
                Log.d("TeacherRequestDebug", "  -> Teacher: ${teacher.displayFullName} (profile_id=${teacher.profileId})")
            }

            Log.d("TeacherRequestDebug", "=================== END DIAGNOSTIC ===================")
            Result.success(teachersList)

        } catch (exception: Exception) {
            Log.e("TeacherRequestDebug", "ERRO AO CARREGAR PROFESSORES", exception)
            Log.e("SearchAdvisorRepo", "Erro ao carregar professores", exception)
            Result.failure(exception)
        }
    }

    suspend fun getTeacherRequests(applicationId: String): Result<Map<String, String>> {
        return try {
            val response = supabase
                .from("teacher_requests")
                .select {
                    filter {
                        eq("application_id", applicationId)
                    }
                }

            val requests = response.decodeList<TeacherRequestDto>()
            val statusMap = requests
                .filter { it.status != "cancelled" }
                .mapNotNull { req ->
                    val teacherId = req.teacherProfileId?.takeIf { it.isNotBlank() }
                    val status = req.status?.takeIf { it.isNotBlank() } ?: "pending"

                    if (teacherId == null) {
                        null
                    } else {
                        teacherId to status
                    }
                }
                .toMap()

            Log.d("TeacherRequestDebug", "Teacher requests for $applicationId: $statusMap")
            Result.success(statusMap)
        } catch (exception: Exception) {
            Log.e("TeacherRequestDebug", "Error loading teacher requests", exception)
            Result.failure(exception)
        }
    }

    suspend fun sendOrientationRequest(
        internshipId: String,
        teacherProfileId: String
    ): Result<Unit> {
        return try {
            Log.d("TeacherRequestDebug", "========== SEND ORIENTATION REQUEST ==========")
            Log.d("TeacherRequestDebug", "Application ID (internshipId): $internshipId")
            Log.d("TeacherRequestDebug", "Teacher Profile ID: $teacherProfileId")

            // Check if there is already a pending/accepted request for this teacher
            val existing = supabase
                .from("teacher_requests")
                .select {
                    filter {
                        eq("application_id", internshipId)
                        eq("teacher_profile_id", teacherProfileId)
                    }
                }
                .decodeList<TeacherRequestDto>()

            if (existing.isNotEmpty()) {
                val currentStatus = existing.first().status
                if (currentStatus == "pending" || currentStatus == "accepted") {
                    Log.d("TeacherRequestDebug", "Request already exists with status=$currentStatus — skipping")
                    return Result.success(Unit)
                }
                // Rejected/cancelled → update back to pending
                supabase
                    .from("teacher_requests")
                    .update(
                        TeacherRequestStatusUpdateDto(status = "pending")
                    ) {
                        filter {
                            eq("application_id", internshipId)
                            eq("teacher_profile_id", teacherProfileId)
                        }
                    }
                Log.d("TeacherRequestDebug", "Existing request updated back to pending!")
            } else {
                // No existing request → insert new
                supabase
                    .from("teacher_requests")
                    .insert(
                        TeacherRequestCreateDto(
                            applicationId = internshipId,
                            teacherProfileId = teacherProfileId,
                            status = "pending"
                        )
                    )
                Log.d("TeacherRequestDebug", "New request inserted into teacher_requests!")
            }

            Log.d("TeacherRequestDebug", "================================================")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("TeacherRequestDebug", "ERRO AO ENVIAR PEDIDO DE ORIENTAÇÃO", exception)
            Log.e("SearchAdvisorRepo", "Erro ao enviar pedido de orientação", exception)
            Result.failure(exception)
        }
    }
}