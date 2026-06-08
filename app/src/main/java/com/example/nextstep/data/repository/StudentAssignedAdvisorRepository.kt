package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.StudentAssignedAdvisorDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from

class StudentAssignedAdvisorRepository {
    private val supabase = SupabaseClientProvider.client

    suspend fun getAssignedAdvisor(applicationId: String): Result<StudentAssignedAdvisorDto> {
        return try {
            val result = supabase.from("student_assigned_advisors_view")
                .select {
                    filter {
                        eq("application_id", applicationId)
                    }
                }
                .decodeSingle<StudentAssignedAdvisorDto>()
            Result.success(result)
        } catch (e: Exception) {
            Log.e("StudentAssignedAdvRepo", "Error getting assigned advisor", e)
            Result.failure(e)
        }
    }
}
