package com.example.project_design.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "steps")
data class StepEntity(
    @PrimaryKey val date: String,
    val steps: Int,
    val distanceKm: Double
)
