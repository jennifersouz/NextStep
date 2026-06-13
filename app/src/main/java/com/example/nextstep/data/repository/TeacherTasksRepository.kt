package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.ApplicationTaskDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class TeacherTasksRepository {
    private val supabase = SupabaseClientProvider.client

    suspend fun getTasksByApplication(applicationId: String): List<ApplicationTaskDto> {
        return try {
            supabase.from("application_tasks")
                .select {
                    filter {
                        eq("application_id", applicationId)
                    }
                    order(column = "created_at", order = Order.DESCENDING)
                }
                .decodeList<ApplicationTaskDto>()
        } catch (e: Exception) {
            Log.e("TeacherTasksRepo", "Error fetching tasks: ${e.message}", e)
            emptyList()
        }
    }
}
