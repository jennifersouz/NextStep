package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.data.model.AdvisorTaskListItemDto
import com.example.nextstep.data.model.CreateApplicationTaskDto
import com.example.nextstep.data.model.UpdateApplicationTaskStatusDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

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
            Log.d("TasksDebug", "ApplicationId recebido: $applicationId")

            // Para alunos, usamos a tabela application_tasks diretamente em vez da view
            // advisor_tasks_view, porque essa view filtra por advisor_profile_id = auth.uid()
            val tasks = supabase
                .from("application_tasks")
                .select {
                    filter {
                        eq("application_id", applicationId)
                    }
                }
                .decodeList<AdvisorTaskListItemDto>()

            Log.d("TasksDebug", "Tarefas encontradas: ${tasks.size}")
            Log.d("TasksDebug", "Resultado bruto: $tasks")

            Result.success(tasks)
        } catch (exception: Exception) {
            Log.e("AdvisorTasksRepo", "Erro ao carregar tarefas da candidatura", exception)
            Result.failure(exception)
        }
    }

    suspend fun createTask(
        applicationId: String,
        title: String
    ): Result<Unit> {
        return try {
            val taskDto = CreateApplicationTaskDto(
                applicationId = applicationId,
                title = title,
                status = "pending"
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
            val updateDto = UpdateApplicationTaskStatusDto(status = status)
            
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
