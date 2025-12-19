package com.example.project_design.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(step: StepEntity)

    @Query("SELECT * FROM steps ORDER BY date DESC")
    fun observeAll(): Flow<List<StepEntity>>
    @Query("SELECT * FROM steps WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): StepEntity?

}
