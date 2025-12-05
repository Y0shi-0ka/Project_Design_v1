package com.example.project_design

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SettingScreen(nav: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
            }
            Text("設定", style = MaterialTheme.typography.titleLarge)
        }

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(48.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text("山田 健太")
                Text("k.yamada@email.com", color = androidx.compose.ui.graphics.Color.Gray)
            }
        }

        Spacer(Modifier.height(20.dp))

        SettingItem("プロフィールを編集", Icons.Default.Person)
        SettingItem("パスワードの変更", Icons.Default.Lock)

        Spacer(Modifier.height(10.dp))
        Text("プライバシー", fontSize = 14.sp, color = androidx.compose.ui.graphics.Color.Gray)

        SettingItem("プライバシーポリシー", Icons.Default.Info)
        SettingItem("テーマの変更", Icons.Default.Palette)
        SettingItem("データの削除", Icons.Default.Delete)

        Spacer(Modifier.height(10.dp))
        Text("通知", fontSize = 14.sp, color = androidx.compose.ui.graphics.Color.Gray)

        SwitchSettingItem("アプリの通知")
        SwitchSettingItem("メールの通知")
    }
}

@Composable
fun SettingItem(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(12.dp))
        Text(title)
    }
}

@Composable
fun SwitchSettingItem(title: String) {
    var checked by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = { checked = it })
    }
}
