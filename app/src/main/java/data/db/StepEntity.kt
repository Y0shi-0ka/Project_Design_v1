package com.example.project_design.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "steps")
data class StepEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val date: String,          // 例: "2025-12-05"
    val steps: Int,
    val distanceKm: Double
)
