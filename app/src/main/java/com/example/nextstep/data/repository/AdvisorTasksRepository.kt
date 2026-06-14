package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.NextStepApplication
import com.example.nextstep.data.local.ConnectivityUtil
import com.example.nextstep.data.local.TaskDao
import com.example.nextstep.data.local.TaskEntity
import com.example.nextstep.data.model.AdvisorTaskListItemDto
import com.example.nextstep.data.model.ApplicationTaskDto
import com.example.nextstep.data.model.CreateApplicationTaskDto
import com.example.nextstep.data.model.UpdateApplicationTaskStatusDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class AdvisorTasksRepository {

    private val supabase = SupabaseClientProvider.client
    private val auth = supabase.auth
    private val context by lazy { NextStepApplication.getInstance() }
    private val taskDao: TaskDao by lazy { NextStepApplication.getInstance().database.taskDao() }

    suspend fun getAdvisorTasks(): Result<List<AdvisorTaskListItemDto>> {
        if (ConnectivityUtil.isNetworkAvailable(context)) {
            return try {
                auth.currentUserOrNull() ?: throw IllegalStateException("Não autenticado")

                val tasks = supabase
                    .from("advisor_tasks_view")
                    .select()
                    .decodeList<AdvisorTaskListItemDto>()

                cacheTasksLocally(tasks)
                Result.success(tasks)
            } catch (exception: Exception) {
                Log.e("AdvisorTasksRepo", "Erro ao carregar tarefas online", exception)
                fallbackToLocalTasks()
            }
        }
        return fallbackToLocalTasks()
    }

    suspend fun getTasksByApplication(applicationId: String): Result<List<AdvisorTaskListItemDto>> {
        if (ConnectivityUtil.isNetworkAvailable(context)) {
            return try {
                Log.d("TasksDebug", "ApplicationId recebido: $applicationId")

                val tasks = supabase
                    .from("application_tasks")
                    .select {
                        filter {
                            eq("application_id", applicationId)
                        }
                    }
                    .decodeList<AdvisorTaskListItemDto>()

                cacheTasksLocally(tasks)
                Log.d("TasksDebug", "Tarefas encontradas: ${tasks.size}")

                Result.success(tasks)
            } catch (exception: Exception) {
                Log.e("AdvisorTasksRepo", "Erro ao carregar tarefas da candidatura", exception)
                fallbackToLocalTasksByApplication(applicationId)
            }
        }
        return fallbackToLocalTasksByApplication(applicationId)
    }

    suspend fun createTask(
        applicationId: String,
        title: String
    ): Result<Unit> {
        if (ConnectivityUtil.isNetworkAvailable(context)) {
            return try {
                val taskDto = CreateApplicationTaskDto(
                    applicationId = applicationId,
                    title = title,
                    status = "pending"
                )

                supabase.from("application_tasks").insert(taskDto)
                Result.success(Unit)
            } catch (exception: Exception) {
                Log.e("AdvisorTasksRepo", "Erro ao criar tarefa online", exception)
                saveTaskLocally(applicationId, title)
            }
        }
        return saveTaskLocally(applicationId, title)
    }

    suspend fun updateTaskStatus(taskId: String, status: String): Result<Unit> {
        if (ConnectivityUtil.isNetworkAvailable(context)) {
            return try {
                val updateDto = UpdateApplicationTaskStatusDto(status = status)

                supabase.from("application_tasks")
                    .update(updateDto) {
                        filter { eq("id", taskId) }
                    }

                taskDao.updateTaskStatus(taskId, status, getCurrentTimestamp())
                Result.success(Unit)
            } catch (exception: Exception) {
                Log.e("AdvisorTasksRepo", "Erro ao atualizar estado online", exception)
                updateTaskStatusLocally(taskId, status)
            }
        }
        return updateTaskStatusLocally(taskId, status)
    }

    suspend fun syncPendingTasks(): Result<Int> {
        if (!ConnectivityUtil.isNetworkAvailable(context)) {
            return Result.success(0)
        }

        return try {
            val pendingTasks = taskDao.getPendingSyncTasks()
            var syncedCount = 0

            for (task in pendingTasks) {
                try {
                    val taskDto = CreateApplicationTaskDto(
                        applicationId = task.applicationId,
                        title = task.title,
                        status = task.status
                    )
                    supabase.from("application_tasks").insert(taskDto)
                    taskDao.markAsSynced(task.id)
                    syncedCount++
                } catch (e: Exception) {
                    Log.e("AdvisorTasksRepo", "Erro ao sincronizar tarefa ${task.id}", e)
                }
            }

            Result.success(syncedCount)
        } catch (exception: Exception) {
            Log.e("AdvisorTasksRepo", "Erro na sincronização", exception)
            Result.failure(exception)
        }
    }

    private suspend fun cacheTasksLocally(tasks: List<AdvisorTaskListItemDto>) {
        val entities = tasks.map { it.toEntity() }
        taskDao.insertTasks(entities)
    }

    private suspend fun fallbackToLocalTasks(): Result<List<AdvisorTaskListItemDto>> {
        return try {
            val localTasks = taskDao.getAllTasks()
            Result.success(localTasks.map { it.toListItemDto() })
        } catch (exception: Exception) {
            Log.e("AdvisorTasksRepo", "Erro ao carregar tarefas locais", exception)
            Result.failure(exception)
        }
    }

    private suspend fun fallbackToLocalTasksByApplication(
        applicationId: String
    ): Result<List<AdvisorTaskListItemDto>> {
        return try {
            val localTasks = taskDao.getTasksByApplication(applicationId)
            Result.success(localTasks.map { it.toListItemDto() })
        } catch (exception: Exception) {
            Log.e("AdvisorTasksRepo", "Erro ao carregar tarefas locais da candidatura", exception)
            Result.failure(exception)
        }
    }

    private suspend fun saveTaskLocally(
        applicationId: String,
        title: String
    ): Result<Unit> {
        return try {
            val task = TaskEntity(
                id = UUID.randomUUID().toString(),
                applicationId = applicationId,
                title = title,
                status = "pending",
                createdAt = getCurrentTimestamp(),
                updatedAt = getCurrentTimestamp(),
                syncStatus = "pending_sync"
            )
            taskDao.insertTask(task)
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdvisorTasksRepo", "Erro ao guardar tarefa localmente", exception)
            Result.failure(exception)
        }
    }

    private suspend fun updateTaskStatusLocally(
        taskId: String,
        status: String
    ): Result<Unit> {
        return try {
            val existingTask = taskDao.getTaskById(taskId)
            if (existingTask != null) {
                val updatedTask = existingTask.copy(
                    status = status,
                    completedAt = if (status == "completed") getCurrentTimestamp() else null,
                    updatedAt = getCurrentTimestamp(),
                    syncStatus = "pending_sync"
                )
                taskDao.updateTask(updatedTask)
            }
            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("AdvisorTasksRepo", "Erro ao atualizar tarefa localmente", exception)
            Result.failure(exception)
        }
    }

    private fun getCurrentTimestamp(): String {
        return SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            Locale.US
        ).apply { timeZone = TimeZone.getTimeZone("UTC") }
            .format(Date())
    }
}

private fun AdvisorTaskListItemDto.toEntity() = TaskEntity(
    id = id,
    applicationId = applicationId,
    title = title,
    description = description,
    status = status,
    priority = priority,
    dueDate = dueDate,
    createdAt = createdAt,
    completedAt = completedAt,
    syncStatus = "synced"
)

private fun TaskEntity.toListItemDto() = AdvisorTaskListItemDto(
    id = id,
    applicationId = applicationId,
    title = title,
    description = description,
    status = status,
    priority = priority,
    dueDate = dueDate,
    createdAt = createdAt,
    completedAt = completedAt
)
