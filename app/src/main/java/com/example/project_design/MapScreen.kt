package com.example.project_design

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color


@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(nav: NavHostController) {

    Box(modifier = Modifier.fillMaxSize()) {

        // Google Map 本体
        val cameraPosition = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                LatLng(36.561325, 136.656205), 12f
            )
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPosition
        )

        // 左上に重ねる戻るボタン
        IconButton(
            onClick = { nav.navigate(Screen.RouteList.route) },
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
    }
}