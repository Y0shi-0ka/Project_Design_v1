package com.example.project_design.data.db

class StepRepository(
    private val stepDao: StepDao
) {
    suspend fun saveToday(date: String, steps: Int, distanceKm: Double) {
        stepDao.upsert(
            StepEntity(
                date = date,
                steps = steps,
                distanceKm = distanceKm
            )
        )
    }

    suspend fun getToday(date: String): StepEntity? {
        return stepDao.getByDate(date)
    }
}
