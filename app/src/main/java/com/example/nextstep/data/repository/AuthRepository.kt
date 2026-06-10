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
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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

            // Caso seja orientador ou docente que acabou de criar conta:
            // a conta existe no Auth, mas ainda não tem linha em profiles/advisors/teachers.
            // Tentamos aceitar convite se houver.
            runCatching {
                supabase.postgrest.rpc("accept_advisor_invite")
            }

            // Note: accept_institution_invite now requires user data, so we can't call it here without data.
            // The registration flow handles this during sign-up.

            val roleAfterInvite = getCurrentUserRole()

            if (roleAfterInvite.isSuccess) {
                Result.success(roleAfterInvite.getOrThrow())
            } else {
                val error = roleAfterInvite.exceptionOrNull()
                if (error?.message == "PROFILE_NOT_FOUND") {
                    Result.failure(IllegalStateException("INCOMPLETE_ACCOUNT"))
                } else {
                    Result.failure(
                        error ?: IllegalStateException("Não foi possível obter o perfil do utilizador.")
                    )
                }
            }
        } catch (exception: Exception) {
            Log.e("AuthRepository", "Erro ao iniciar sessão", exception)
            Result.failure(exception)
        }
    }

    suspend fun hasPendingInstitutionInvite(
        email: String,
        role: String
    ): Result<Boolean> {
        return try {
            val result = supabase.postgrest.rpc(
                function = "has_pending_institution_invite",
                parameters = buildJsonObject {
                    put("invite_email", email.trim().lowercase())
                    put("invite_role", role)
                }
            )
            Result.success(result.data.toString().toBoolean())
        } catch (exception: Exception) {
            Log.e("AuthRepository", "Erro ao verificar convite pendente", exception)
            Result.failure(exception)
        }
    }

    suspend fun registerInvitedStudent(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String?,
        studentNumber: String,
        course: String,
        academicYear: Int
    ): Result<Unit> {
        return try {
            val normalizedEmail = email.trim().lowercase()
            Log.d("AuthRepository", "registerInvitedStudent email=$normalizedEmail")

            // 1. Verificar se existe convite pendente
            val inviteCheck = hasPendingInstitutionInvite(normalizedEmail, "student")
            Log.d("AuthRepository", "hasPendingInvite student email=$normalizedEmail result=${inviteCheck.getOrNull()}")
            if (inviteCheck.isFailure || inviteCheck.getOrNull() == false) {
                return Result.failure(IllegalStateException("INVITE_NOT_FOUND"))
            }

            // 2. Criar utilizador no Auth
            val userId = createAuthUserAndGetId(normalizedEmail, password)
            Log.d("AuthRepository", "Created auth user id=$userId for student")

            // 3. Chamar RPC para aceitar convite e criar perfil/student com dados do utilizador
            supabase.postgrest.rpc(
                function = "accept_institution_invite",
                parameters = buildJsonObject {
                    put("invite_role", "student")
                    put("user_first_name", firstName.trim())
                    put("user_last_name", lastName.trim())
                    put("user_phone", phone?.trim()?.takeIf { it.isNotBlank() })
                    put("user_student_number", studentNumber.trim())
                    put("user_course", course.trim())
                    put("user_academic_year", academicYear)
                    put("user_department", null)
                }
            )
            Log.d("AuthRepository", "RPC accept_institution_invite(student) succeeded")

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AuthRepository", "Erro ao registar aluno convidado", exception)
            Result.failure(exception)
        }
    }

    suspend fun registerInvitedTeacher(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        department: String?,
        phone: String?
    ): Result<Unit> {
        return try {
            val normalizedEmail = email.trim().lowercase()
            Log.d("AuthRepository", "registerInvitedTeacher email=$normalizedEmail")

            // 1. Verificar se existe convite pendente
            val inviteCheck = hasPendingInstitutionInvite(normalizedEmail, "teacher")
            Log.d("AuthRepository", "hasPendingInvite teacher email=$normalizedEmail result=${inviteCheck.getOrNull()}")
            if (inviteCheck.isFailure || inviteCheck.getOrNull() == false) {
                return Result.failure(IllegalStateException("INVITE_NOT_FOUND"))
            }

            // 2. Criar utilizador no Auth
            val userId = createAuthUserAndGetId(normalizedEmail, password)
            Log.d("AuthRepository", "Created auth user id=$userId for teacher")

            // 3. Chamar RPC para aceitar convite e criar perfil/teacher com dados do utilizador
            supabase.postgrest.rpc(
                function = "accept_institution_invite",
                parameters = buildJsonObject {
                    put("invite_role", "teacher")
                    put("user_first_name", firstName.trim())
                    put("user_last_name", lastName.trim())
                    put("user_phone", phone?.trim()?.takeIf { it.isNotBlank() })
                    put("user_student_number", null)
                    put("user_course", null)
                    put("user_academic_year", null)
                    put("user_department", department?.trim()?.takeIf { it.isNotBlank() })
                }
            )
            Log.d("AuthRepository", "RPC accept_institution_invite(teacher) succeeded")

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AuthRepository", "Erro ao registar docente convidado", exception)
            Result.failure(exception)
        }
    }

    @Deprecated("Use registerInvitedStudent para alunos")
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