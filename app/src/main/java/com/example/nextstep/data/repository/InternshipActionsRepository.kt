package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationStatusUpdateDto(
    val status: String
)

class InternshipActionsRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun cancelInternship(applicationId: String): Result<Unit> {
        return try {
            Log.d("InternshipAction", "Action=cancel applicationId=$applicationId")
            supabase.from("applications")
                .update(ApplicationStatusUpdateDto(status = "rejected")) {
                    filter { eq("id", applicationId) }
                }
            Log.d("InternshipAction", "Operation successful")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("InternshipAction", "Erro ao cancelar estágio", exception)
            Result.failure(exception)
        }
    }

    suspend fun finishInternship(applicationId: String): Result<Unit> {
        return try {
            Log.d("InternshipAction", "Action=end applicationId=$applicationId")
            supabase.from("applications")
                .update(ApplicationStatusUpdateDto(status = "completed")) {
                    filter { eq("id", applicationId) }
                }
            Log.d("InternshipAction", "Operation successful")
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("InternshipAction", "Erro ao concluir estágio", exception)
            Result.failure(exception)
        }
    }
}
