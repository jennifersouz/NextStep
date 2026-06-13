package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdminCreateUserRequest
import com.example.nextstep.data.model.AdminProfileDto
import com.example.nextstep.data.model.AdminProfileUpdateDto
import com.example.nextstep.data.model.AdminUserEditRequest
import com.example.nextstep.data.model.InstitutionOptionDto
import com.example.nextstep.data.model.UserActiveStatusUpdateDto
import com.example.nextstep.data.model.UserArchiveUpdateDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.Instant

class AdminUsersRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getUsers(): Result<List<AdminProfileDto>> {
        return try {
            val profiles = supabase
                .from("profiles")
                .select {
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<AdminProfileDto>()

            Log.d("AdminUsersRepo", "Users loaded: ${profiles.size}")
            Result.success(profiles)
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Error loading users", exception)
            Result.failure(exception)
        }
    }

    suspend fun getNonArchivedUsers(): Result<List<AdminProfileDto>> {
        return try {
            val allProfiles = supabase
                .from("profiles")
                .select {
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<AdminProfileDto>()

            // Filter out archived on client side to avoid complex Supabase query
            val profiles = allProfiles.filter { it.archivedAt == null }

            Log.d("AdminUsersRepo", "Non-archived users loaded: ${profiles.size}")
            Result.success(profiles)
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Error loading non-archived users", exception)
            Result.failure(exception)
        }
    }

    /**
     * Carrega instituições activas e não arquivadas da tabela [profiles].
     *
     * A biblioteca Supabase Kotlin não suporta operador `in` de forma directa
     * nesta versão, por isso fazemos query simples e filtramos no Kotlin.
     * Filtro: role in ('institution', 'instituicao') && is_active == true && archived_at == null
     */
    suspend fun getInstitutions(): Result<List<InstitutionOptionDto>> {
        return try {
            val allProfiles = supabase
                .from("profiles")
                .select()
                .decodeList<InstitutionOptionDto>()

            val institutions = allProfiles.filter { profile ->
                val roleMatch = profile.role == "institution" || profile.role == "instituicao"
                val activeMatch = profile.isActive == true
                val notArchived = profile.archivedAt == null
                roleMatch && activeMatch && notArchived
            }

            Log.d("AdminUsersRepo", "Institutions loaded: ${institutions.size}")
            Result.success(institutions)
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Error loading institutions", exception)
            Result.failure(exception)
        }
    }

    suspend fun createUser(request: AdminCreateUserRequest): Result<Unit> {
        return try {
            Log.d("AdminUsersRepo", "Calling edge function admin-create-user for ${request.email}")

            val response = supabase.functions.invoke("admin-create-user", request)

            if (response.status.isSuccess()) {
                Log.d("AdminUsersRepo", "User created successfully via edge function")
                Result.success(Unit)
            } else {
                val body = response.bodyAsText()
                Log.e("AdminUsersRepo", "Edge function error: ${response.status} - $body")

                val errorMessage = try {
                    val json = Json.parseToJsonElement(body).jsonObject
                    json["error"]?.jsonPrimitive?.content
                        ?: json["message"]?.jsonPrimitive?.content
                        ?: "Erro desconhecido do servidor"
                } catch (e: Exception) {
                    "Erro ao criar utilizador (${response.status.value})"
                }

                Result.failure(Exception(errorMessage))
            }
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Exception calling edge function", exception)
            Result.failure(exception)
        }
    }

    suspend fun getUserById(userId: String): Result<AdminProfileDto?> {
        return try {
            val profiles = supabase
                .from("profiles")
                .select {
                    filter { eq("id", userId) }
                }
                .decodeList<AdminProfileDto>()
            Result.success(profiles.firstOrNull())
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Error loading user id=$userId", exception)
            Result.failure(exception)
        }
    }

    /**
     * Verifica se já existe um profile com o email informado.
     * O email é normalizado para lowercase e trim antes da consulta.
     */
    suspend fun emailExists(email: String): Boolean {
        return try {
            val cleaned = email.trim().lowercase()
            val profiles = supabase
                .from("profiles")
                .select {
                    filter { eq("email", cleaned) }
                }
                .decodeList<AdminProfileDto>()

            val exists = profiles.isNotEmpty()
            Log.d("AdminUsersRepo", "emailExists check for $cleaned: $exists")
            exists
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Error checking email $email", exception)
            // Em caso de erro, permitir que a validação prossiga (não bloquear)
            false
        }
    }

    /**
     * Atualiza um utilizador na tabela profiles e retorna os dados atualizados.
     * Não altera email — o DTO AdminUserEditRequest não contém campo email.
     */
    suspend fun updateUser(
        userId: String,
        request: AdminUserEditRequest
    ): Result<AdminProfileDto> {
        return try {
            if (userId.isBlank()) throw IllegalArgumentException("ID do utilizador está vazio.")

            Log.d("AdminUsersRepo", "Updating user id=$userId role=${request.role}")

            supabase
                .from("profiles")
                .update(request) {
                    filter { eq("id", userId) }
                }

            // Select pós-update para confirmar persistência
            val profiles = supabase
                .from("profiles")
                .select { filter { eq("id", userId) } }
                .decodeList<AdminProfileDto>()

            val user = profiles.firstOrNull()
                ?: throw IllegalStateException("Utilizador não encontrado após atualização.")

            Log.d(
                "AdminUsersRepo",
                "Updated user loaded id=${user.id}, email=${user.email}, firstName=${user.firstName}"
            )

            Result.success(user)
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Error updating user id=$userId", exception)
            Result.failure(exception)
        }
    }

    // ===== Legacy updateUser with AdminProfileUpdateDto (kept for backward compat) =====

    suspend fun updateUser(
        userId: String,
        updateData: AdminProfileUpdateDto
    ): Result<Unit> {
        return try {
            Log.d("AdminUsersRepo", "Updating user id=$userId (legacy)")
            Log.d("AdminUsersRepo", "Payload user update = $updateData")

            val payload = updateData.copy(
                updatedAt = Instant.now().toString()
            )

            supabase
                .from("profiles")
                .update(payload) {
                    filter { eq("id", userId) }
                }

            Log.d("AdminUsersRepo", "User updated successfully id=$userId")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Error updating user id=$userId", exception)
            Result.failure(exception)
        }
    }

    // ===== Account status management =====

    /**
     * Desativar acesso: marca is_active = false.
     * Confirma com select pós-update que a alteração persistiu.
     */
    suspend fun deactivateUser(userId: String): Result<AdminProfileDto> {
        return try {
            if (userId.isBlank()) throw IllegalArgumentException("ID do utilizador está vazio.")

            val now = Instant.now().toString()
            Log.d("AdminUsersRepo", "Deactivating user id=$userId")

            supabase
                .from("profiles")
                .update(UserActiveStatusUpdateDto(isActive = false, updatedAt = now)) {
                    filter { eq("id", userId) }
                }

            // Verificar se a alteração persistiu
            val profiles = supabase
                .from("profiles")
                .select { filter { eq("id", userId) } }
                .decodeList<AdminProfileDto>()

            val updated = profiles.firstOrNull()
                ?: throw IllegalStateException("Utilizador não encontrado após atualização.")

            Log.d(
                "AdminUsersRepo",
                "After deactivation id=${updated.id}, isActive=${updated.isActive}, archivedAt=${updated.archivedAt}"
            )

            if (updated.isActive != false) {
                throw IllegalStateException("Não foi possível desativar o utilizador. A alteração não persistiu.")
            }

            Log.d("AdminUsersRepo", "User deactivated and confirmed id=$userId")
            Result.success(updated)
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Error deactivating user id=$userId", exception)
            Result.failure(exception)
        }
    }

    /**
     * Reativar acesso: is_active = true.
     * Confirma com select pós-update que a alteração persistiu.
     */
    suspend fun reactivateUser(userId: String): Result<AdminProfileDto> {
        return try {
            if (userId.isBlank()) throw IllegalArgumentException("ID do utilizador está vazio.")

            val now = Instant.now().toString()
            Log.d("AdminUsersRepo", "Reactivating user id=$userId")

            supabase
                .from("profiles")
                .update(UserActiveStatusUpdateDto(isActive = true, updatedAt = now)) {
                    filter { eq("id", userId) }
                }

            // Verificar se a alteração persistiu
            val profiles = supabase
                .from("profiles")
                .select { filter { eq("id", userId) } }
                .decodeList<AdminProfileDto>()

            val updated = profiles.firstOrNull()
                ?: throw IllegalStateException("Utilizador não encontrado após atualização.")

            Log.d(
                "AdminUsersRepo",
                "After reactivation id=${updated.id}, isActive=${updated.isActive}, archivedAt=${updated.archivedAt}"
            )

            if (updated.isActive != true) {
                throw IllegalStateException("Não foi possível reativar o utilizador. A alteração não persistiu.")
            }

            Log.d("AdminUsersRepo", "User reactivated and confirmed id=$userId")
            Result.success(updated)
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Error reactivating user id=$userId", exception)
            Result.failure(exception)
        }
    }

    /**
     * Arquivar/Remover da plataforma: is_active = false, archived_at preenchido.
     * adminId é obtido internamente do Auth — nunca recebe string vazia como parâmetro.
     * Confirma com select pós-update que a alteração persistiu.
     */
    suspend fun archiveUser(userId: String, reason: String?): Result<AdminProfileDto> {
        return try {
            if (userId.isBlank()) throw IllegalArgumentException("ID do utilizador está vazio.")

            // Obter adminId do Auth — null se não disponível. NUNCA usar "" como UUID.
            val adminId = supabase.auth.currentUserOrNull()?.id?.takeIf { it.isNotBlank() }
            val cleanReason = reason?.takeIf { it.isNotBlank() }
            val now = Instant.now().toString()

            Log.d(
                "AdminUsersRepo",
                "archiveUser userId=$userId adminId=$adminId reason=$cleanReason"
            )

            supabase
                .from("profiles")
                .update(
                    UserArchiveUpdateDto(
                        isActive = false,
                        archivedAt = now,
                        archivedBy = adminId,   // null se não autenticado — nunca ""
                        archiveReason = cleanReason,
                        updatedAt = now
                    )
                ) {
                    filter { eq("id", userId) }
                }

            // Confirmar que a alteração persistiu
            val profiles = supabase
                .from("profiles")
                .select { filter { eq("id", userId) } }
                .decodeList<AdminProfileDto>()

            val updated = profiles.firstOrNull()
                ?: throw IllegalStateException("Utilizador não encontrado após arquivamento.")

            Log.d(
                "AdminUsersRepo",
                "After archive id=${updated.id}, isActive=${updated.isActive}, archivedAt=${updated.archivedAt}"
            )

            // A confirmação de sucesso baseia-se em archived_at estar preenchido.
            // Não depender de isActive porque o Supabase pode retornar null para esse campo
            // dependendo das políticas RLS, e o default do DTO é true — causaria falso erro.
            if (updated.archivedAt == null) {
                throw IllegalStateException("Não foi possível remover o utilizador da plataforma. A alteração não persistiu.")
            }

            Log.d("AdminUsersRepo", "User archived and confirmed id=$userId archivedAt=${updated.archivedAt}")
            Result.success(updated)
        } catch (exception: Exception) {
            Log.e("AdminUsersRepo", "Error archiving user id=$userId", exception)
            Result.failure(exception)
        }
    }

    // ===== Deletion impact and permanent delete were intentionally removed =====
    // The app does not support permanent deletion.
    // Allowed actions: deactivateUser, reactivateUser, archiveUser.
}