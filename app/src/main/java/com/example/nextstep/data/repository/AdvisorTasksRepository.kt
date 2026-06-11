package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdvisorTaskListItemDto
import com.example.nextstep.data.model.CreateApplicationTaskDto
import com.example.nextstep.data.model.UpdateApplicationTaskStatusDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import java.time.Instant

class AdvisorTasksRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth

    suspend fun getAdvisorTasks(): Result<List<AdvisorTaskListItemDto>> {
        return try {
            auth.currentUserOrNull() ?: throw IllegalStateException("Não autenticado")

            // Esta função assume a existência de uma view advisor_tasks_view no Supabase
            val tasks = supabase
                .from("advisor_tasks_view") 
                .select()
                .decodeList<AdvisorTaskListItemDto>()

            Result.success(tasks)
        } catch (exception: Exception) {
            Log.e("AdvisorTasksRepo", "Erro ao carregar tarefas", exception)
            Result.failure(exception)
        }
    }

    suspend fun getTasksByApplication(applicationId: String): Result<List<AdvisorTaskListItemDto>> {
        return try {
            // Tentamos ler da view primeiro para ter os dados completos (student_name, etc)
            // Se falhar ou se quisermos apenas os dados da tabela, filtramos application_tasks
            val tasks = supabase
                .from("advisor_tasks_view")
                .select {
                    filter {
                        eq("application_id", applicationId)
                    }
                }
                .decodeList<AdvisorTaskListItemDto>()
            Result.success(tasks)
        } catch (exception: Exception) {
            Log.e("AdvisorTasksRepo", "Erro ao carregar tarefas da candidatura", exception)
            Result.failure(exception)
        }
    }

    suspend fun createTask(
        applicationId: String,
        title: String,
        description: String?,
        dueDate: String?,
        priority: String
    ): Result<Unit> {
        return try {
            val userId = auth.currentUserOrNull()?.id ?: throw IllegalStateException("Não autenticado")
            
            val taskDto = CreateApplicationTaskDto(
                applicationId = applicationId,
                title = title,
                description = description,
                dueDate = dueDate,
                priority = priority,
                status = "pending",
                createdByProfileId = userId
            )
            
            supabase.from("application_tasks").insert(taskDto)
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdvisorTasksRepo", "Erro ao criar tarefa", exception)
            Result.failure(exception)
        }
    }

    suspend fun updateTaskStatus(taskId: String, status: String): Result<Unit> {
        return try {
            val completedAt = if (status == "completed") Instant.now().toString() else null
            
            val updateDto = UpdateApplicationTaskStatusDto(
                status = status,
                completedAt = completedAt,
                updatedAt = Instant.now().toString()
            )
            
            supabase.from("application_tasks")
                .update(updateDto) {
                    filter { eq("id", taskId) }
                }
            
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdvisorTasksRepo", "Erro ao atualizar estado da tarefa", exception)
            Result.failure(exception)
        }
    }
}
