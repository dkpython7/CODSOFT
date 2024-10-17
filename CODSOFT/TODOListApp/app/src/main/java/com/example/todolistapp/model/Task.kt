package com.example.todolistapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var title: String,
    var subtitle: String,
    var description: String?,
    var priority: Int, // 1 = High, 2 = Medium, 3 = Low
    var dueDate: Long?, // Store as timestamp
    var isCompleted: Boolean = false
)

