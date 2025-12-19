package com.example.project_design.data.db

import com.example.project_design.TodayStat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

data class HomeUiState(
    val todayStats: List<TodayStat> = emptyList()
)

class HomeRepository(
    private val stepDao: StepDao,
    private val pointDao: PointDao
) {

    val homeState: Flow<HomeUiState> = combine(
        stepDao.observeAll(),
        pointDao.observeAll()
    ) { steps: List<StepEntity>, points: List<PointEntity> ->

        val totalSteps = steps.sumOf { it.steps }
        val totalDistance = steps.sumOf { it.distanceKm }
        val totalPoints = points.lastOrNull()?.total ?: 0
        val today = java.time.LocalDate.now().toString()
        val todayRow = steps.firstOrNull { it.date == today }

        val todaySteps = todayRow?.steps ?: 0


        HomeUiState(
            todayStats = listOf(
                TodayStat("歩数", todaySteps.toString()),
                TodayStat("ポイント", "${totalPoints} pt"),
                TodayStat("距離", "%.2f km".format(totalDistance)),
                TodayStat("消費カロリー", estimateKcal(totalDistance).toString()) // 心拍の代わり
            )
        )
    }

    suspend fun addSteps(date: String, steps: Int, distanceKm: Double) {
        stepDao.upsert(
            StepEntity(
                date = date,
                steps = steps,
                distanceKm = distanceKm
            )
        )
    }

    suspend fun addPoints(reason: String, delta: Int) {
        val currentTotal = pointDao.observeAll()
            .map { list -> list.lastOrNull()?.total ?: 0 }
            .first()

        val newTotal = currentTotal + delta

        pointDao.insert(
            PointEntity(
                reason = reason,
                delta = delta,
                total = newTotal,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // ひとまず距離(km)→kcalの超ざっくり概算（歩行の目安）
    private fun estimateKcal(distanceKm: Double): Int {
        // 1kmあたり約50〜70kcalくらいが目安。とりあえず 60kcal/km
        return (distanceKm * 60.0).toInt()
    }
}
