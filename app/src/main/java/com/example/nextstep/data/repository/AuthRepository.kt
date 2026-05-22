package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CompanyDto
import com.example.nextstep.data.model.ProfileDto
import com.example.nextstep.data.model.StudentDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from

class AuthRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AuthRepository", "Erro ao iniciar sessão", exception)
            Result.failure(exception)
        }
    }

    suspend fun registerStudent(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        studentNumber: String,
        course: String,
        academicYear: Int
    ): Result<Unit> {
        return try {
            val userId = createAuthUserAndGetId(
                email = email,
                password = password
            )

            supabase.from("profiles").insert(
                ProfileDto(
                    id = userId,
                    email = email,
                    role = "student"
                )
            )

            supabase.from("students").insert(
                StudentDto(
                    profileId = userId,
                    firstName = firstName,
                    lastName = lastName,
                    studentNumber = studentNumber,
                    course = course,
                    academicYear = academicYear
                )
            )

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AuthRepository", "Erro ao criar conta de aluno", exception)
            Result.failure(exception)
        }
    }

    suspend fun registerCompany(
        email: String,
        password: String,
        companyName: String,
        nif: String,
        businessArea: String,
        location: String
    ): Result<Unit> {
        return try {
            val userId = createAuthUserAndGetId(
                email = email,
                password = password
            )

            supabase.from("profiles").insert(
                ProfileDto(
                    id = userId,
                    email = email,
                    role = "company"
                )
            )

            supabase.from("companies").insert(
                CompanyDto(
                    profileId = userId,
                    companyName = companyName,
                    nif = nif,
                    businessArea = businessArea,
                    location = location
                )
            )

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AuthRepository", "Erro ao criar conta de empresa", exception)
            Result.failure(exception)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AuthRepository", "Erro ao terminar sessão", exception)
            Result.failure(exception)
        }
    }

    private suspend fun createAuthUserAndGetId(
        email: String,
        password: String
    ): String {
        val createdUser = auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        val userId = createdUser?.id ?: auth.currentUserOrNull()?.id

        return userId ?: throw IllegalStateException(
            "Não foi possível obter o ID do utilizador após o registo."
        )
    }
}