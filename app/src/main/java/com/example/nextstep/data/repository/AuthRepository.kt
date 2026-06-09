package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CompanyDto
import com.example.nextstep.data.model.InstitutionInsertDto
import com.example.nextstep.data.model.ProfileDto
import com.example.nextstep.data.model.StudentDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest

class AuthRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun login(
        email: String,
        password: String
    ): Result<String> {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email.trim().lowercase()
                this.password = password
            }

            val roleResult = getCurrentUserRole()

            if (roleResult.isSuccess) {
                return Result.success(roleResult.getOrThrow())
            }

            // Caso seja orientador: a conta existe no Auth,
            // mas ainda não tem linha em profiles/advisors.
            runCatching {
                supabase.postgrest.rpc("accept_advisor_invite")
            }

            val roleAfterInvite = getCurrentUserRole()

            if (roleAfterInvite.isSuccess) {
                Result.success(roleAfterInvite.getOrThrow())
            } else {
                Result.failure(
                    roleAfterInvite.exceptionOrNull()
                        ?: IllegalStateException("Não foi possível obter o perfil do utilizador.")
                )
            }
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

    suspend fun registerInstitution(
        name: String,
        nif: String?,
        locality: String,
        address: String?,
        phone: String?,
        email: String,
        password: String
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
                    role = "institution"
                )
            )

            supabase.from("institutions").insert(
                InstitutionInsertDto(
                    profileId = userId,
                    name = name,
                    nif = nif,
                    locality = locality,
                    address = address,
                    phone = phone
                )
            )

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AuthRepository", "Erro ao criar conta de instituição", exception)
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

    suspend fun getCurrentUserRole(): Result<String> {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
                ?: return Result.failure(
                    IllegalStateException("Utilizador não autenticado.")
                )

            val profiles = supabase
                .from("profiles")
                .select {
                    filter {
                        eq("id", currentUser.id)
                    }
                }
                .decodeList<ProfileDto>()

            val profile = profiles.firstOrNull()
                ?: return Result.failure(
                    IllegalStateException("PROFILE_NOT_FOUND")
                )

            Result.success(profile.role)
        } catch (exception: Exception) {
            Log.e("AuthRepository", "Erro ao buscar role do utilizador", exception)
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