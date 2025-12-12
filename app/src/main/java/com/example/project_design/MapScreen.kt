package com.example.project_design

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.project_design.data.route.KmlRouteRepository
import com.example.project_design.data.route.StaticRoute
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(
    nav: NavHostController,
    courseId: String
) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // ─── コース別にKMLを読み分け ────────────────────────────────
    val repository = remember { KmlRouteRepository(context) }

    val (fileName, routeId, routeName) = remember(courseId) {
        when (courseId) {
            "hakusan" -> Triple(
                "hakusan.kml",
                "hakusan_yamanami",
                "白山やまなみコース"
            )
            else -> Triple(
                "togashi.kml",
                "togashi_no_sato",
                "富樫の里コース"
            )
        }
    }

    // KML読み込みをIOスレッドで実行
    val routeState by produceState<StaticRoute?>(initialValue = null, courseId) {
        value = withContext(Dispatchers.IO) {
            repository.loadRouteFromAssets(
                fileName = fileName,
                id = routeId,
                name = routeName
            )
        }
    }

    // まだロード中ならローディングUIだけ表示して終了
    val route = routeState
    if (route == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
        return
    }

    val startPoint: LatLng = route.points.firstOrNull()
        ?: LatLng(36.561325, 136.656205)

    // ─── 状態管理 ─────────────────────────────────────────────
    var hasLocationPermission by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var isTracking by remember { mutableStateOf(false) }
    var progressIndex by remember { mutableStateOf(0) }

    // ─── 権限リクエスト ─────────────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // ─── 距離計算ヘルパー ─────────────────────────────────────
    fun distanceMeters(a: LatLng, b: LatLng): Float {
        val res = FloatArray(1)
        Location.distanceBetween(a.latitude, a.longitude, b.latitude, b.longitude, res)
        return res[0]
    }

    val scope = rememberCoroutineScope()

    // ─── 位置更新コールバック ────────────────────────────────
    val locationCallback = remember(route.id) {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                val pos = LatLng(loc.latitude, loc.longitude)
                currentLocation = pos

                if (isTracking && route.points.isNotEmpty()) {
                    // 重めの最近傍計算は別スレッド(Dispatchers.Default)へ
                    scope.launch(Dispatchers.Default) {
                        val nearestIndex = route.points.indices.minByOrNull { idx ->
                            distanceMeters(pos, route.points[idx])
                        } ?: return@launch

                        val d = distanceMeters(pos, route.points[nearestIndex])
                        if (d < 40f && nearestIndex > progressIndex) {
                            // UIの更新だけメインスレッドへ戻す
                            withContext(Dispatchers.Main) {
                                progressIndex = nearestIndex
                            }
                        }
                    }
                }
            }
        }
    }

    // ─── 位置情報の開始／停止 ────────────────────────────────
    DisposableEffect(hasLocationPermission, route.id) {
        if (hasLocationPermission) {
            val fineGranted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (fineGranted) {
                val request = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    2000L // 2秒ごとに更新
                ).build()

                fusedLocationClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    // ─── 通過済み・未通過ルートの分割 ─────────────────────────
    val safeIndex = progressIndex.coerceAtMost(route.points.size - 1).coerceAtLeast(0)

    val walkedPoints: List<LatLng> =
        if (route.points.isEmpty()) emptyList()
        else route.points.take(safeIndex + 1)

    val remainingPoints: List<LatLng> =
        if (route.points.isEmpty()) emptyList()
        else route.points.drop(safeIndex)

    // ─── UI ────────────────────────────────────────────────
    Box(modifier = Modifier.fillMaxSize()) {
        val cameraPosition = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(startPoint, 15f)
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPosition,
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
            uiSettings = MapUiSettings(myLocationButtonEnabled = true)
        ) {
            if (remainingPoints.size >= 2) {
                Polyline(points = remainingPoints, color = Color.LightGray, width = 14f)
            }

            if (walkedPoints.size >= 2) {
                Polyline(points = walkedPoints, color = Color(0xFF4CAF50), width = 16f)
            }

            currentLocation?.let { pos ->
                Marker(state = MarkerState(position = pos), title = "現在地")
            }
        }

        // 戻るボタン
        IconButton(
            onClick = { nav.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(
                    Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "戻る",
                tint = Color.Black
            )
        }

        // スタート／ストップ ボタン
        Button(
            onClick = { isTracking = !isTracking },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            Text(if (isTracking) "ストップ" else "スタート")
        }
    }
}
