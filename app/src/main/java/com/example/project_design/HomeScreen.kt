@file:Suppress("unused")

package com.example.project_design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState

// ========== データクラス ==========

data class TodayStat(
    val label: String,
    val value: String,
    val change: String? = null,
    val status: String? = null
)

data class Activity(
    val title: String,
    val timeText: String,   // 例: "今日, 2:30 PM"
    val duration: String,   // 例: "32 分"
    val distance: String?,  // 例: "5.2 km" / null
    val calories: String    // 例: "245 cal"
)

data class RecommendedWorkout(
    val title: String,
    val duration: String,
    val intensity: String
)

// ========== メインの画面 ==========

@Composable
fun HomeScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("今日", "概要")

    // ---- ここで ViewModel から DB の状態を取得する ----
    val homeViewModel: HomeViewModel = viewModel()
    val uiState by homeViewModel.uiState.collectAsState()

    // todayStats は DB から。まだ何も入っていないときだけダミー表示にする
    val todayStats: List<TodayStat> =
        if (uiState.todayStats.isNotEmpty()) {
            uiState.todayStats
        } else {
            listOf(
                TodayStat("歩数", "8,432", "+12%"),
                TodayStat("カロリー", "420", "+8%"),
                TodayStat("心拍数", "72 bpm", status = "正常"),
                TodayStat("距離", "5.2 km", "+15%")
            )
        }

    // ↓ここはまだDBを作っていないので従来通りダミーのまま
    val activities = listOf(
        Activity("富樫の里コース", "今日, 2:30 PM", "32 分", "5.2 km", "245 cal"),
        Activity("白山やまなみコース", "昨日, 9:15 AM", "45 分", "8.6 km", "310 cal")
    )

    val recommendedWorkouts = listOf(
        RecommendedWorkout("富樫の里コースウォーキング", "30 分", "中程度"),
        RecommendedWorkout("白山やまなみコースハイキング", "45 分", "やや高強度")
    )

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // 開発用：1回押すとDBにダミーデータを入れられる（確認が済んだら消してOK）
                /*
                Button(onClick = { homeViewModel.insertDemoData() }) {
                    Text("ダミーデータ投入")
                }
                Spacer(Modifier.height(8.dp))
                */

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.background
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> TodayTabContent(
                modifier = Modifier.padding(innerPadding),
                todayStats = todayStats,
                activities = activities,
                recommendedWorkouts = recommendedWorkouts
            )
            1 -> SummaryTabContent(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

// ========== 「今日」タブの中身 ==========

@Composable
fun TodayTabContent(
    modifier: Modifier = Modifier,
    todayStats: List<TodayStat>,
    activities: List<Activity>,
    recommendedWorkouts: List<RecommendedWorkout>
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 今日の統計
        item {
            Text(
                text = "今日の統計",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    todayStats.take(2).forEach { stat ->
                        TodayStatCard(
                            modifier = Modifier.weight(1f),
                            stat = stat
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    todayStats.drop(2).forEach { stat ->
                        TodayStatCard(
                            modifier = Modifier.weight(1f),
                            stat = stat
                        )
                    }
                }
            }
        }

        // 最近のアクティビティ
        item {
            Text(
                text = "最近のアクティビティ",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        items(activities) { activity ->
            ActivityCard(activity = activity)
        }

        // おすすめのワークアウト
        item {
            Text(
                text = "おすすめのワークアウト",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recommendedWorkouts) { workout ->
                    RecommendedWorkoutCard(workout = workout)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ========== 統計カード ==========

@Composable
fun TodayStatCard(
    modifier: Modifier = Modifier,
    stat: TodayStat
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stat.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = stat.value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            stat.change?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
            stat.status?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        }
    }
}

// ========== アクティビティカード ==========

@Composable
fun ActivityCard(activity: Activity) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = activity.timeText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = activity.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = activity.duration,
                    style = MaterialTheme.typography.bodyMedium
                )
                activity.distance?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = activity.calories,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// ========== おすすめワークアウトカード ==========

@Composable
fun RecommendedWorkoutCard(workout: RecommendedWorkout) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        modifier = Modifier.width(220.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Image",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Text(
                text = workout.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = workout.duration,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = workout.intensity,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.secondary
                )
            )

            Button(
                onClick = { /* TODO: Start Workout */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Start Workout",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// ========== 「概要」タブ ==========

@Composable
fun SummaryTabContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "概要タブの内容をここに入れる",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}