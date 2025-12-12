package com.example.project_design.data.db

import com.example.project_design.TodayStat
import com.example.project_design.Activity
import com.example.project_design.RecommendedWorkout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlin.math.round

// UI用の状態
data class HomeUiState(
    val todayStats: List<TodayStat> = emptyList(),
    val activities: List<Activity> = emptyList(),
    val recommendedWorkouts: List<RecommendedWorkout> = emptyList()
)

class HomeRepository(
    private val stepDao: StepDao,
    private val pointDao: PointDao
) {

    // DB→UI用データに変換したFlow
    val homeState: Flow<HomeUiState> = combine(
        stepDao.observeAll(),
        pointDao.observeAll()
    ) { steps, points ->

        val totalSteps = steps.sumOf { it.steps }
        val totalDistance = steps.sumOf { it.distanceKm }
        val totalPoints = points.lastOrNull()?.total ?: 0

        HomeUiState(
            todayStats = listOf(
                TodayStat("歩数", totalSteps.toString(), change = null),
                TodayStat("ポイント", "${totalPoints} pt", change = null),
                TodayStat(
                    "距離",
                    "%.2f km".format(totalDistance),
                    change = null
                ),
                TodayStat("心拍数", "72 bpm", status = "正常") // ここはまだダミー
            ),
            activities = emptyList(),             // あとでActivityテーブル作って埋める
            recommendedWorkouts = emptyList()     // ここも後で
        )
    }

    // 歩数追加用の簡単メソッド
    suspend fun addSteps(date: String, steps: Int, distanceKm: Double) {
        val entity = StepEntity(
            date = date,
            steps = steps,
            distanceKm = distanceKm
        )
        stepDao.insert(entity)
    }

    // ポイント追加用（残高計算もここで）
    suspend fun addPoints(reason: String, delta: Int) {
        val currentTotal = pointDao.observeAll()
            .map { list -> list.lastOrNull()?.total ?: 0 }
            .first()  // Flow から一回だけ値を取る

        val newTotal = currentTotal + delta
        val entity = PointEntity(
            reason = reason,
            delta = delta,
            total = newTotal,
            timestamp = System.currentTimeMillis()
        )
        pointDao.insert(entity)
    }
}
