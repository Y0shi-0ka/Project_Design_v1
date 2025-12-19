package com.example.project_design

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.project_design.ui.theme.Project_DesignTheme

class MainActivity : ComponentActivity() {

    private val requestActivityRecognitionPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* granted -> Unit */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Android 10+ は ACTIVITY_RECOGNITION の実行時許可が必要
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                requestActivityRecognitionPermission.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }

        setContent {
            Project_DesignTheme {
                AppNavHost()
            }
        }
    }
}
