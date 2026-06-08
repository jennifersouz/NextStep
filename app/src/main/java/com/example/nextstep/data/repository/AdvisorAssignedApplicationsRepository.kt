package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdvisorAssignedApplicationDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class AdvisorAssignedApplicationsRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getAssignedApplications(): Result<List<AdvisorAssignedApplicationDto>> {
        return try {
            auth.currentUserOrNull()
                ?: throw IllegalStateException("Utilizador não autenticado.")

            val applications = supabase
                .from("advisor_assigned_applications_view")
                .select()
                .decodeList<AdvisorAssignedApplicationDto>()
                .sortedWith(
                    compareByDescending<AdvisorAssignedApplicationDto> { application ->
                        application.unreadCount > 0
                    }.thenByDescending { application ->
                        application.lastMessageAt.orEmpty()
                    }.thenBy { application ->
                        application.studentFullName.lowercase()
                    }
                )

            Result.success(applications)
        } catch (exception: Exception) {
            Log.e(
                "AdvisorAssignedAppsRepo",
                "Erro ao carregar alunos atribuídos",
                exception
            )
            Result.failure(exception)
        }
    }
}
