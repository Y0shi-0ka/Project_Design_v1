package com.example.project_design

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_design.data.db.AppDatabase
import com.example.project_design.data.db.HomeRepository
import com.example.project_design.data.db.HomeUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: HomeRepository
    val uiState: StateFlow<HomeUiState>

    init {
        val db = AppDatabase.getInstance(application)
        repository = HomeRepository(
            stepDao = db.stepDao(),
            pointDao = db.pointDao()
        )

        uiState = repository.homeState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState()
        )
    }

    // 開発・テスト用：1クリックでダミーデータ追加
    fun insertDemoData() {
        viewModelScope.launch {
            repository.addSteps(
                date = "2025-12-12",
                steps = 8432,
                distanceKm = 5.2
            )
            repository.addPoints(
                reason = "demo",
                delta = 420
            )
        }
    }
}
