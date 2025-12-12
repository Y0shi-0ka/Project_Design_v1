package com.example.project_design.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PointDao {

    @Query("SELECT * FROM points ORDER BY timestamp ASC")
    fun observeAll(): Flow<List<PointEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(point: PointEntity)

    @Query("DELETE FROM points")
    suspend fun clear()
}
