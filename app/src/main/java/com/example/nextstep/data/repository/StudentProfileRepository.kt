package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.ProfileEmailDto
import com.example.nextstep.data.model.StudentProfile
import com.example.nextstep.data.model.StudentProfileDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class StudentProfileRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getCurrentStudentProfile(): Result<StudentProfile> {
        return try {
            val userId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Utilizador não autenticado.")

            val profile = supabase
                .from("profiles")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<ProfileEmailDto>()

            val student = supabase
                .from("students")
                .select {
                    filter {
                        eq("profile_id", userId)
                    }
                }
                .decodeSingle<StudentProfileDto>()

            val educationInstitution = student.educationInstitution
                ?.takeIf { it.isNotBlank() }
                ?: "Instituto Politécnico de Viana do Castelo"

            Result.success(
                StudentProfile(
                    profileId = student.profileId,
                    email = profile.email,
                    firstName = student.firstName,
                    lastName = student.lastName,
                    studentNumber = student.studentNumber,
                    course = student.course,
                    academicYear = student.academicYear,
                    educationInstitution = educationInstitution
                )
            )
        } catch (exception: Exception) {
            Log.e("StudentProfileRepository", "Erro ao carregar perfil do aluno", exception)
            Result.failure(exception)
        }
    }
}