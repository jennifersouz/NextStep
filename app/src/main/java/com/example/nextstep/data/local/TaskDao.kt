package com.example.nextstep.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("SELECT * FROM application_tasks ORDER BY created_at DESC")
    suspend fun getAllTasks(): List<TaskEntity>

    @Query("SELECT * FROM application_tasks WHERE application_id = :applicationId ORDER BY created_at DESC")
    suspend fun getTasksByApplication(applicationId: String): List<TaskEntity>

    @Query("SELECT * FROM application_tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity?

    @Query("SELECT * FROM application_tasks WHERE sync_status = 'pending_sync'")
    suspend fun getPendingSyncTasks(): List<TaskEntity>

    @Query("UPDATE application_tasks SET sync_status = 'synced' WHERE id = :taskId")
    suspend fun markAsSynced(taskId: String)

    @Query("UPDATE application_tasks SET status = :status, updated_at = :updatedAt WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: String, status: String, updatedAt: String)

    @Query("DELETE FROM application_tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)

    @Query("DELETE FROM application_tasks")
    suspend fun deleteAllTasks()
}
