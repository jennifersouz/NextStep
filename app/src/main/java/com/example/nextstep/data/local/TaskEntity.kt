package com.example.nextstep.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "application_tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "application_id") val applicationId: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "status") val status: String = "pending",
    @ColumnInfo(name = "priority") val priority: String? = "medium",
    @ColumnInfo(name = "due_date") val dueDate: String? = null,
    @ColumnInfo(name = "created_by_profile_id") val createdByProfileId: String? = null,
    @ColumnInfo(name = "assigned_to_profile_id") val assignedToProfileId: String? = null,
    @ColumnInfo(name = "completed_at") val completedAt: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "synced",
    @ColumnInfo(name = "student_name") val studentName: String? = null,
    @ColumnInfo(name = "offer_title") val offerTitle: String? = null
)
