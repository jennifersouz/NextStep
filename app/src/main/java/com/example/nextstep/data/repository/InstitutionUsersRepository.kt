package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.InstitutionInviteInsertDto
import com.example.nextstep.data.model.InstitutionUserDetailDto
import com.example.nextstep.data.model.InstitutionUserDto
import com.example.nextstep.data.model.InstitutionStudentDto
import com.example.nextstep.data.model.InstitutionTeacherDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class InstitutionUsersRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun createInvite(
        targetRole: String,
        email: String
    ): Result<Unit> {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            val institutionExists = supabase
                .from("institutions")
                .select {
                    filter { eq("profile_id", currentUser.id) }
                }
                .decodeSingleOrNull<InstitutionIdOnlyDto>()

            if (institutionExists == null) {
                Log.e("InstitutionUsersRepository", "Instituição não encontrada para o utilizador autenticado: ${currentUser.id}")
                return Result.failure(Exception("Perfil de instituição não encontrado. Certifique-se de que registou a sua instituição."))
            }

            val invite = InstitutionInviteInsertDto(
                institutionProfileId = currentUser.id,
                targetRole = targetRole,
                email = email.trim().lowercase()
            )

            supabase.from("institution_invites").insert(invite)

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("InstitutionUsersRepository", "Erro ao criar convite", exception)
            Result.failure(exception)
        }
    }

    suspend fun getInstitutionUsers(): Result<List<InstitutionUserDto>> {
        return try {
            val users = supabase
                .from("institution_users_view")
                .select()
                .decodeList<InstitutionUserDto>()

            Log.d("InstitutionUsers", "usersCount=${users.size}")

            Result.success(users)
        } catch (exception: Exception) {
            Log.e("InstitutionUsersRepository", "Erro ao buscar utilizadores da instituição", exception)
            Result.failure(exception)
        }
    }

    suspend fun getInstitutionUserDetail(
        inviteId: String?,
        profileId: String?,
        role: String
    ): Result<InstitutionUserDetailDto> {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            Log.d("InstitutionUsersRepo", "getInstitutionUserDetail inviteId=$inviteId profileId=$profileId role=$role")

            // Case 1: If we have a profileId, the user has accepted and registered
            if (!profileId.isNullOrBlank() && profileId != "no_profile") {
                return when (role.lowercase().trim()) {
                    "student" -> {
                        val student = supabase
                            .from("students")
                            .select {
                                filter {
                                    eq("profile_id", profileId)
                                    eq("institution_profile_id", currentUser.id)
                                }
                                single()
                            }
                            .decodeAs<InstitutionStudentDto>()

                        Result.success(InstitutionUserDetailDto(
                            profileId = student.profileId,
                            email = student.email,
                            firstName = student.firstName,
                            lastName = student.lastName,
                            phone = student.phone,
                            targetRole = "student",
                            inviteStatus = "accepted",
                            acceptedAt = student.createdAt, 
                            isActive = student.isActive,
                            createdAt = student.createdAt,
                            institutionArchivedAt = student.institutionArchivedAt,
                            studentNumber = student.studentNumber,
                            course = student.course,
                            academicYear = student.academicYear,
                            educationInstitution = student.educationInstitution
                        ))
                    }
                    "teacher" -> {
                        val teacher = supabase
                            .from("teachers")
                            .select {
                                filter {
                                    eq("profile_id", profileId)
                                    eq("institution_profile_id", currentUser.id)
                                }
                                single()
                            }
                            .decodeAs<InstitutionTeacherDto>()

                        Result.success(InstitutionUserDetailDto(
                            profileId = teacher.profileId,
                            email = teacher.email,
                            firstName = teacher.firstName,
                            lastName = teacher.lastName,
                            phone = teacher.phone,
                            targetRole = "teacher",
                            inviteStatus = "accepted",
                            acceptedAt = teacher.createdAt,
                            isActive = teacher.isActive,
                            createdAt = teacher.createdAt,
                            institutionArchivedAt = teacher.institutionArchivedAt,
                            department = teacher.department
                        ))
                    }
                    else -> Result.failure(Exception("Função desconhecida: $role"))
                }
            }

            // Case 2: No profileId, must check the invite
            if (!inviteId.isNullOrBlank() && inviteId != "no_invite") {
                val invite = supabase
                    .from("institution_invites")
                    .select {
                        filter {
                            eq("id", inviteId)
                            eq("institution_profile_id", currentUser.id)
                        }
                        single()
                    }
                    .decodeAs<InstitutionInviteDetailDto>()

                // Fallback: If it was accepted but profileId was not provided, try to find the profile
                if (invite.acceptedAt != null) {
                    try {
                        val tableName = if (invite.targetRole == "student") "students" else "teachers"
                        val profileIdFound = supabase.from(tableName)
                            .select {
                                filter { eq("email", invite.email) }
                                single()
                            }
                            .decodeAs<ProfileIdOnlyDto>()
                            .profileId

                        return getInstitutionUserDetail(inviteId, profileIdFound, invite.targetRole)
                    } catch (e: Exception) {
                        Log.w("InstitutionUsersRepo", "Could not resolve profile for accepted invite ${invite.email}", e)
                        return Result.success(InstitutionUserDetailDto(
                            email = invite.email,
                            firstName = invite.firstName,
                            lastName = invite.lastName,
                            targetRole = invite.targetRole,
                            inviteStatus = "accepted",
                            acceptedAt = invite.acceptedAt,
                            createdAt = invite.createdAt
                        ))
                    }
                }

                return Result.success(InstitutionUserDetailDto(
                    email = invite.email,
                    firstName = invite.firstName,
                    lastName = invite.lastName,
                    targetRole = invite.targetRole,
                    inviteStatus = "pending",
                    createdAt = invite.createdAt
                ))
            }

            Result.failure(Exception("Dados insuficientes para carregar o detalhe."))
        } catch (exception: Exception) {
            Log.e("InstitutionUsersRepository", "Erro ao carregar detalhe", exception)
            Result.failure(Exception("Não foi possível carregar os dados do utilizador."))
        }
    }

    suspend fun deletePendingInvite(inviteId: String): Result<Unit> {
        return try {
            supabase.postgrest.rpc(
                function = "delete_pending_institution_invite",
                parameters = buildJsonObject {
                    put("invite_uuid", inviteId)
                }
            )

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("InstitutionUsersRepo", "Erro ao eliminar convite pendente", exception)
            Result.failure(exception)
        }
    }
}

@Serializable
data class InstitutionInviteDetailDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("institution_profile_id")
    val institutionProfileId: String = "",
    @SerialName("target_role")
    val targetRole: String = "",
    val email: String = "",
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    @SerialName("accepted_at")
    val acceptedAt: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
private data class ProfileIdOnlyDto(
    @SerialName("profile_id")
    val profileId: String
)

@Serializable
private data class InstitutionIdOnlyDto(
    @SerialName("profile_id")
    val profileId: String
)
