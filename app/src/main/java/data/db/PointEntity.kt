package com.example.project_design.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "points")
data class PointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val reason: String,
    val delta: Int,        // 何ポイント増えた/減ったか
    val total: Int,        // その時点の合計ポイント
    val timestamp: Long    // UnixTime millis
)
