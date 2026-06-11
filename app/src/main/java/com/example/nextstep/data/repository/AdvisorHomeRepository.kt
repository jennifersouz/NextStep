package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.ActivityDto
import com.example.nextstep.data.model.AdvisorActivityDto
import com.example.nextstep.data.model.AdvisorAssignedStudentDto
import com.example.nextstep.data.model.AdvisorSummaryDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class AdvisorHomeRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth
    private val profileRepository = AdvisorProfileRepository()
    private val assignedAppsRepository = AdvisorAssignedApplicationsRepository()

    suspend fun getAdvisorName(): Result<String> {
        return profileRepository.getAdvisorProfile().map { profile ->
            profile.name.orEmpty().ifBlank { "Orientador" }
        }
    }

    suspend fun getAdvisorSummary(): Result<AdvisorSummaryDto> {
        return try {
            val applications = assignedAppsRepository.getAssignedApplications()
                .getOrDefault(emptyList())

            val assignedStudentsCount = applications.size
            val activeInternshipsCount = applications.count { app ->
                app.status == "accepted" || app.status == "active"
            }

            Result.success(
                AdvisorSummaryDto(
                    assignedStudentsCount = assignedStudentsCount,
                    activeInternshipsCount = activeInternshipsCount,
                    pendingTasksCount = 0
                )
            )
        } catch (exception: Exception) {
            Log.e("AdvisorHomeRepo", "Erro ao carregar sumário", exception)
            Result.failure(exception)
        }
    }

    suspend fun getAssignedStudentsPreview(): Result<List<AdvisorAssignedStudentDto>> {
        return try {
            val applications = assignedAppsRepository.getAssignedApplications()
                .getOrDefault(emptyList())

            val students = applications.map { app ->
                AdvisorAssignedStudentDto(
                    applicationId = app.applicationId,
                    studentName = app.studentFullName,
                    studentEmail = app.studentEmail,
                    offerTitle = app.offerTitle,
                    status = app.status
                )
            }

            Result.success(students)
        } catch (exception: Exception) {
            Log.e("AdvisorHomeRepo", "Erro ao carregar alunos", exception)
            Result.failure(exception)
        }
    }

    suspend fun getRecentActivities(): Result<List<AdvisorActivityDto>> {
        return try {
            val currentUser = auth.currentUserOrNull()
                ?: return Result.success(emptyList())

            val activities = supabase
                .from("advisor_activities_view")
                .select {
                    filter {
                        eq("advisor_profile_id", currentUser.id)
                    }
                    order("created_at", Order.DESCENDING)
                    limit(5)
                }
                .decodeList<ActivityDto>()

            Result.success(
                activities.map { activity ->
                    AdvisorActivityDto(
                        id = activity.id,
                        title = activity.title,
                        subtitle = activity.subtitle,
                        createdAt = activity.createdAt,
                        type = activity.type
                    )
                }
            )
        } catch (exception: Exception) {
            Log.e("AdvisorHomeRepo", "Erro ao carregar atividades", exception)
            Result.success(emptyList())
        }
    }
}