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

    suspend fun archiveProfile(profileId: String, role: String): Result<Unit> {
        val repo = InstitutionRepository()
        return when (role.trim().lowercase()) {
            "student" -> repo.archiveStudent(profileId, reason = null)
            "teacher" -> repo.archiveTeacher(profileId, reason = null).map { }
            else -> Result.failure(Exception("Função desconhecida: $role"))
        }
    }

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
            val currentUser = supabase.auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            val students = supabase.from("students")
                .select {
                    filter { eq("institution_profile_id", currentUser.id) }
                }
                .decodeList<InstitutionStudentDto>()

            val teachers = supabase.from("teachers")
                .select {
                    filter { eq("institution_profile_id", currentUser.id) }
                }
                .decodeList<InstitutionTeacherDto>()

            val studentProfileIds = students.map { it.profileId }.filter { it.isNotBlank() }
            val teacherProfileIds = teachers.map { it.profileId }.filter { it.isNotBlank() }
            val allProfileIds = (studentProfileIds + teacherProfileIds).distinct()

            val profiles = if (allProfileIds.isNotEmpty()) {
                supabase.from("profiles")
                    .select {
                        filter { isIn("id", allProfileIds) }
                    }
                    .decodeList<ProfileListDto>()
                    .associateBy { it.id }
            } else emptyMap()

            val realUsers = buildList {
                for (student in students) {
                    if (student.profileId.isBlank()) continue
                    val profile = profiles[student.profileId]
                    add(InstitutionUserDto(
                        inviteId = "",
                        profileId = student.profileId,
                        email = profile?.email ?: "",
                        firstName = profile?.firstName ?: student.firstName,
                        lastName = profile?.lastName ?: student.lastName,
                        targetRole = "student",
                        acceptedAt = null,
                        inviteStatus = "accepted",
                        studentNumber = student.studentNumber,
                        course = student.course,
                        academicYear = student.academicYear,
                        department = null,
                        createdAt = student.createdAt,
                        institutionArchivedAt = student.institutionArchivedAt,
                        isActive = student.isActive
                    ))
                }
                for (teacher in teachers) {
                    if (teacher.profileId.isBlank()) continue
                    val profile = profiles[teacher.profileId]
                    add(InstitutionUserDto(
                        inviteId = "",
                        profileId = teacher.profileId,
                        email = profile?.email ?: "",
                        firstName = profile?.firstName ?: teacher.firstName,
                        lastName = profile?.lastName ?: teacher.lastName,
                        targetRole = "teacher",
                        acceptedAt = null,
                        inviteStatus = "accepted",
                        studentNumber = null,
                        course = null,
                        academicYear = null,
                        department = teacher.department,
                        createdAt = teacher.createdAt,
                        institutionArchivedAt = teacher.institutionArchivedAt,
                        isActive = teacher.isActive
                    ))
                }
            }

            val pendingInvites = supabase.from("institution_invites")
                .select {
                    filter { eq("institution_profile_id", currentUser.id) }
                }
                .decodeList<InstitutionInviteDto>()
                .filter { it.acceptedAt == null }

            val pendingUsers = pendingInvites.map { invite ->
                InstitutionUserDto(
                    inviteId = invite.id,
                    profileId = null,
                    email = invite.email,
                    firstName = invite.firstName,
                    lastName = invite.lastName,
                    targetRole = invite.targetRole,
                    acceptedAt = null,
                    inviteStatus = "pending",
                    createdAt = invite.createdAt
                )
            }

            val allUsers = (realUsers + pendingUsers)
                .sortedByDescending { it.createdAt.orEmpty() }

            Log.d("InstitutionUsers", "usersCount=${allUsers.size} (real=${realUsers.size} pending=${pendingUsers.size})")

            Result.success(allUsers)
        } catch (exception: Exception) {
            Log.e("InstitutionUsersRepository", "Erro ao buscar utilizadores da instituição", exception)
            Result.failure(exception)
        }
    }

    suspend fun getInstitutionUserDetail(
        profileId: String,
        role: String
    ): Result<InstitutionUserDetailDto> {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Utilizador não autenticado."))

            Log.d("InstitutionUsersRepo", "getInstitutionUserDetail profileId=$profileId role=$role")

            val profileFallback = try {
                supabase.from("profiles")
                    .select {
                        filter { eq("id", profileId) }
                    }
                    .decodeSingleOrNull<ProfileListDto>()
            } catch (_: Exception) { null }

            return when (role.lowercase().trim()) {
                "student" -> {
                    val student = supabase.from("students")
                        .select {
                            filter {
                                eq("profile_id", profileId)
                                eq("institution_profile_id", currentUser.id)
                            }
                        }
                        .decodeSingleOrNull<InstitutionStudentDto>()

                    if (student == null) {
                        throw Exception("Aluno não encontrado ou sem permissão de leitura.")
                    }

                    val email = student.email.takeIf { it.isNotBlank() }
                        ?: profileFallback?.email.orEmpty()

                    val firstName = student.firstName.takeIf { it.isNotBlank() }
                        ?: profileFallback?.firstName.orEmpty()

                    val lastName = student.lastName.takeIf { it.isNotBlank() }
                        ?: profileFallback?.lastName.orEmpty()

                    Result.success(InstitutionUserDetailDto(
                        profileId = student.profileId,
                        email = email,
                        firstName = firstName,
                        lastName = lastName,
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
                    val teacher = supabase.from("teachers")
                        .select {
                            filter {
                                eq("profile_id", profileId)
                                eq("institution_profile_id", currentUser.id)
                            }
                        }
                        .decodeSingleOrNull<InstitutionTeacherDto>()

                    if (teacher == null) {
                        throw Exception("Docente não encontrado ou sem permissão de leitura.")
                    }

                    val email = teacher.email.takeIf { it.isNotBlank() }
                        ?: profileFallback?.email.orEmpty()

                    val firstName = teacher.firstName.takeIf { it.isNotBlank() }
                        ?: profileFallback?.firstName.orEmpty()

                    val lastName = teacher.lastName.takeIf { it.isNotBlank() }
                        ?: profileFallback?.lastName.orEmpty()

                    Result.success(InstitutionUserDetailDto(
                        profileId = teacher.profileId,
                        email = email,
                        firstName = firstName,
                        lastName = lastName,
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
data class ProfileListDto(
    val id: String,
    val email: String,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null
)

@Serializable
data class InstitutionInviteDto(
    val id: String = "",
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
private data class InstitutionIdOnlyDto(
    @SerialName("profile_id")
    val profileId: String
)


