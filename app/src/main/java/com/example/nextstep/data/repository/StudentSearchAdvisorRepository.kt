package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.SentAdvisorRequestDto
import com.example.nextstep.data.model.StudentProfileDto
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

    suspend fun getApplicationTeacherStatus(
        applicationId: String
    ): Result<Pair<String?, String?>> {
        return try {
            val response = supabase
                .from("applications")
                .select {
                    filter {
                        eq("id", applicationId)
                    }
                }

            Log.d("TeacherRequestDebug", "getApplicationTeacherStatus raw response: ${response.data}")

            val resultList = response.decodeList<SentAdvisorRequestDto>()

            Log.d("TeacherRequestDebug", "getApplicationTeacherStatus decoded: $resultList")

            val app = resultList.firstOrNull()
            if (app != null) {
                Log.d("TeacherRequestDebug", "Application $applicationId - teacher_profile_id=${app.teacherProfileId}, teacher_status=${app.teacherStatus}")
                Result.success(Pair(app.teacherProfileId, app.teacherStatus))
            } else {
                Log.e("TeacherRequestDebug", "Application $applicationId NOT FOUND")
                Result.success(Pair(null, null))
            }
        } catch (exception: Exception) {
            Log.e("TeacherRequestDebug", "Error loading application $applicationId", exception)
            Result.failure(exception)
        }
    }

    suspend fun sendOrientationRequest(
        internshipId: String,
        teacherProfileId: String
    ): Result<Unit> {
        return try {
            val currentUserId = auth.currentSessionOrNull()?.user?.id
            Log.d("TeacherRequestDebug", "========== SEND ORIENTATION REQUEST ==========")
            Log.d("TeacherRequestDebug", "Student (auth.uid): $currentUserId")
            Log.d("TeacherRequestDebug", "Application ID (internshipId): $internshipId")
            Log.d("TeacherRequestDebug", "Teacher Profile ID: $teacherProfileId")

            val response = supabase
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
                    select()
                }

            Log.d("TeacherRequestDebug", "sendOrientationRequest raw response: ${response.data}")

            val resultList = response.decodeList<SentAdvisorRequestDto>()

            Log.d("TeacherRequestDebug", "Rows affected: ${resultList.size}")
            Log.d("TeacherRequestDebug", "sendOrientationRequest decoded: $resultList")

            if (resultList.isEmpty()) {
                Log.e("TeacherRequestDebug", "UPDATE returned 0 rows — RLS likely blocked the operation!")
                Log.e("TeacherRequestDebug", "Check RLS policy: missing 'Students can update own applications' on 'applications' table")
                return Result.failure(Exception("Nenhuma linha foi atualizada. Possível bloqueio RLS."))
            }

            val updated = resultList.first()
            val savedTeacherId = updated.teacherProfileId
            val savedStatus = updated.teacherStatus
            Log.d("TeacherRequestDebug", "Verified: teacher_profile_id=$savedTeacherId, teacher_status=$savedStatus")

            if (savedTeacherId != teacherProfileId) {
                Log.e("TeacherRequestDebug", "Mismatch: expected teacher_profile_id=$teacherProfileId, got=$savedTeacherId")
                return Result.failure(Exception("O orientador não foi gravado corretamente."))
            }

            Log.d("TeacherRequestDebug", "Request successfully persisted!")
            Log.d("TeacherRequestDebug", "================================================")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("TeacherRequestDebug", "ERRO AO ENVIAR PEDIDO DE ORIENTAÇÃO", exception)
            Log.e("SearchAdvisorRepo", "Erro ao enviar pedido de orientação", exception)
            Result.failure(exception)
        }
    }
}