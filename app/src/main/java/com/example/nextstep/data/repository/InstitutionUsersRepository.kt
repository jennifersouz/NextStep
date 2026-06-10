package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.InstitutionInviteInsertDto
import com.example.nextstep.data.model.InstitutionUserDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
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