package com.example.project_design

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.project_design.ui.theme.Project_DesignTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Project_DesignTheme {
                AppNavHost()   // ← Navigation.kt で定義
            }
        }
    }
}
