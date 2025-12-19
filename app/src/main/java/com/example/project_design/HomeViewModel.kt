package com.example.project_design

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_design.data.db.AppDatabase
import com.example.project_design.data.db.HomeRepository
import com.example.project_design.data.db.HomeUiState
import com.example.project_design.data.db.StepRepository
import com.example.project_design.data.sensor.StepCounterManager
import com.example.project_design.data.store.StepBaselineStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val homeRepository: HomeRepository
    private val stepRepository: StepRepository

    val uiState: StateFlow<HomeUiState>

    // 歩数センサー
    private val stepCounterManager = StepCounterManager(application)

    // baseline（端末起動後累積の基準値）
    private var baselineStepsSinceBoot: Long? = null

    // baseline 永続化
    private val baselineStore = StepBaselineStore(application)

    // 保存の間引き（長め）
    private val SAVE_INTERVAL_MS = 30_000L
    private var lastSavedAtMs: Long = 0L
    private var lastSavedSteps: Int = -1

    init {
        val db = AppDatabase.getInstance(application)

        homeRepository = HomeRepository(
            stepDao = db.stepDao(),
            pointDao = db.pointDao()
        )

        stepRepository = StepRepository(
            stepDao = db.stepDao()
        )

        uiState = homeRepository.homeState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

        if (stepCounterManager.hasStepSensor) {
            startStepSensor()
        }
    }

    private fun startStepSensor() {
        stepCounterManager.start()

        viewModelScope.launch {
            stepCounterManager.totalStepsSinceBoot.collectLatest { totalSinceBoot ->

                val today = LocalDate.now().toString()

                // 1) まだbaselineが無ければ、DataStoreから復元を試みる（初回のみ）
                if (baselineStepsSinceBoot == null) {
                    baselineStepsSinceBoot = baselineStore.getBaseline(today)
                }

                // 2) baselineが無ければ、今回の値を「今日の基準」として保存して終了
                if (baselineStepsSinceBoot == null) {
                    baselineStepsSinceBoot = totalSinceBoot
                    baselineStore.setBaseline(today, totalSinceBoot)
                    return@collectLatest
                }

                val todaySteps =
                    (totalStepsSinceBootToToday(totalSinceBoot, baselineStepsSinceBoot!!))
                        .coerceAtLeast(0)
                        .toInt()

                val now = System.currentTimeMillis()

                // 30秒に1回 ＋ 歩数が変わった時だけ保存
                val shouldSave =
                    (todaySteps != lastSavedSteps) &&
                            (now - lastSavedAtMs >= SAVE_INTERVAL_MS)

                if (!shouldSave) return@collectLatest

                lastSavedSteps = todaySteps
                lastSavedAtMs = now

                stepRepository.saveToday(
                    date = today,
                    steps = todaySteps,
                    distanceKm = 0.0
                )
            }
        }
    }

    private fun totalStepsSinceBootToToday(totalSinceBoot: Long, baseline: Long): Long {
        return totalSinceBoot - baseline
    }

    override fun onCleared() {
        stepCounterManager.stop()
        super.onCleared()
    }

    fun insertDemoData() {
        viewModelScope.launch {
            homeRepository.addSteps(
                date = "2025-12-12",
                steps = 8432,
                distanceKm = 5.2
            )
            homeRepository.addPoints(
                reason = "demo",
                delta = 420
            )
        }
    }
}
