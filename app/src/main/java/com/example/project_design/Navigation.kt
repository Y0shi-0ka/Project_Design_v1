package com.example.project_design

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.padding


// 画面のルート定義
sealed class Screen(val route: String, val label: String) {
    object Home : Screen("home", "ホーム")
    object RouteList : Screen("route_list", "ルート")
    object Settings : Screen("settings", "設定")
    object Map : Screen("map", "マップ")
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.RouteList.route) { RouteListScreen(navController) }
            composable(Screen.Settings.route) { SettingScreen(navController) }
            composable(Screen.Map.route) { MapScreen(navController) }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(Screen.Home, Screen.RouteList, Screen.Settings)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentDestination.isTopLevelDestinationInHierarchy(screen.route),
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    when (screen) {
                        Screen.Home -> Icon(Icons.Filled.Home, contentDescription = screen.label)
                        Screen.RouteList -> Icon(Icons.Filled.Map, contentDescription = screen.label)
                        Screen.Settings -> Icon(Icons.Filled.Settings, contentDescription = screen.label)
                        Screen.Map -> Icon(Icons.Filled.Map, contentDescription = screen.label)
                    }
                },
                label = { Text(screen.label) }
            )
        }
    }
}

// 現在地が選択中か判定する拡張
private fun NavDestination?.isTopLevelDestinationInHierarchy(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}
