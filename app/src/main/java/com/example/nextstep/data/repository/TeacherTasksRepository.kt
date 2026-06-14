package com.example.nextstep.data.repository

import android.util.Log
import com.example.nextstep.NextStepApplication
import com.example.nextstep.data.local.ConnectivityUtil
import com.example.nextstep.data.local.TaskDao
import com.example.nextstep.data.local.TaskEntity
import com.example.nextstep.data.model.ApplicationTaskDto
import com.example.nextstep.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class TeacherTasksRepository {
    private val supabase = SupabaseClientProvider.client
    private val context by lazy { NextStepApplication.getInstance() }
    private val taskDao: TaskDao by lazy { NextStepApplication.getInstance().database.taskDao() }

    suspend fun getTasksByApplication(applicationId: String): List<ApplicationTaskDto> {
        if (ConnectivityUtil.isNetworkAvailable(context)) {
            return try {
                val tasks = supabase.from("application_tasks")
                    .select {
                        filter {
                            eq("application_id", applicationId)
                        }
                        order(column = "created_at", order = Order.DESCENDING)
                    }
                    .decodeList<ApplicationTaskDto>()

                val entities = tasks.map { it.toEntity() }
                taskDao.insertTasks(entities)
                tasks
            } catch (e: Exception) {
                Log.e("TeacherTasksRepo", "Error fetching tasks online: ${e.message}", e)
                getLocalTasksByApplication(applicationId)
            }
        }
        return getLocalTasksByApplication(applicationId)
    }

    private suspend fun getLocalTasksByApplication(applicationId: String): List<ApplicationTaskDto> {
        return try {
            val localTasks = taskDao.getTasksByApplication(applicationId)
            localTasks.map { it.toApplicationTaskDto() }
        } catch (e: Exception) {
            Log.e("TeacherTasksRepo", "Error fetching tasks locally: ${e.message}", e)
            emptyList()
        }
    }
}

private fun ApplicationTaskDto.toEntity() = TaskEntity(
    id = id,
    applicationId = applicationId,
    title = title,
    description = description,
    status = status,
    priority = priority,
    dueDate = dueDate,
    createdAt = createdAt,
    completedAt = completedAt,
    updatedAt = updatedAt,
    syncStatus = "synced"
)

private fun TaskEntity.toApplicationTaskDto() = ApplicationTaskDto(
    id = id,
    applicationId = applicationId,
    title = title,
    description = description,
    status = status,
    priority = priority,
    dueDate = dueDate,
    completedAt = completedAt,
    createdAt = createdAt,
    updatedAt = updatedAt
)
