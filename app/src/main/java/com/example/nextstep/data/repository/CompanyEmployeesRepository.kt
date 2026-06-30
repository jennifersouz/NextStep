package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.CompanyEmployeeInviteDisplayDto
import com.example.nextstep.data.model.CompanyEmployeeInviteInsertDto
import com.example.nextstep.data.model.CompanyEmployeeDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class CompanyEmployeesRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun createInvite(email: String): Result<Unit> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: return Result.failure(IllegalStateException("Empresa não autenticada."))

            supabase.from("company_employee_invites").insert(
                CompanyEmployeeInviteInsertDto(
                    companyProfileId = companyProfileId,
                    email = email.trim().lowercase()
                )
            )

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("CompanyEmployeesRepo", "Erro ao criar convite", exception)
            Result.failure(exception)
        }
    }

    suspend fun getEmployees(): Result<List<CompanyEmployeeInviteDisplayDto>> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            val invites = supabase
                .from("company_employee_invites")
                .select {
                    filter { eq("company_profile_id", companyProfileId) }
                }
                .decodeList<CompanyEmployeeInviteRawDto>()

            val activeEmployees = supabase
                .from("company_employees")
                .select {
                    filter { eq("company_profile_id", companyProfileId) }
                }
                .decodeList<CompanyEmployeeDto>()

            val activeEmails = activeEmployees
                .map { it.email.lowercase().trim() }
                .toSet()

            val activeItems = activeEmployees.map { emp ->
                CompanyEmployeeInviteDisplayDto(
                    id = emp.profileId ?: "",
                    profileId = emp.profileId,
                    email = emp.email,
                    firstName = emp.firstName,
                    lastName = emp.lastName,
                    phone = emp.phone,
                    department = emp.department,
                    status = "active"
                )
            }

            val pendingItems = invites
                .filter { invite ->
                    invite.email.lowercase().trim() !in activeEmails
                }
                .map { invite ->
                    CompanyEmployeeInviteDisplayDto(
                        id = invite.id,
                        email = invite.email,
                        firstName = invite.firstName,
                        lastName = invite.lastName,
                        phone = invite.phone,
                        department = invite.department,
                        status = "pending",
                        acceptedAt = invite.acceptedAt
                    )
                }

            val result = (activeItems + pendingItems)
                .sortedWith(
                    compareBy<CompanyEmployeeInviteDisplayDto> { if (it.status == "active") 0 else 1 }
                        .thenBy { it.displayName.lowercase() }
                )

            Result.success(result)
        } catch (exception: Exception) {
            Log.e("CompanyEmployeesRepo", "Erro ao carregar funcionários", exception)
            Result.failure(exception)
        }
    }

    suspend fun deleteEmployee(employeeId: String): Result<Unit> {
        return try {
            val companyProfileId = auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Empresa não autenticada.")

            runCatching {
                supabase
                    .from("company_employee_invites")
                    .delete {
                        filter {
                            eq("id", employeeId)
                            eq("company_profile_id", companyProfileId)
                        }
                    }
            }

            runCatching {
                supabase
                    .from("company_employees")
                    .delete {
                        filter {
                            eq("profile_id", employeeId)
                            eq("company_profile_id", companyProfileId)
                        }
                    }
            }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("CompanyEmployeesRepo", "Erro ao eliminar funcionário", exception)
            Result.failure(exception)
        }
    }
}

@kotlinx.serialization.Serializable
private data class CompanyEmployeeInviteRawDto(
    val id: String = "",
    val email: String = "",
    @kotlinx.serialization.SerialName("first_name")
    val firstName: String? = null,
    @kotlinx.serialization.SerialName("last_name")
    val lastName: String? = null,
    val phone: String? = null,
    val department: String? = null,
    @kotlinx.serialization.SerialName("accepted_at")
    val acceptedAt: String? = null
)
