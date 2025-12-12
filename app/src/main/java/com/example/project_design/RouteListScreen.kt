package com.example.project_design

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter

// どのKMLを開くか識別するためのidを追加
data class WalkCourse(
    val id: String,          // "togashi", "hakusan" など
    val title: String,
    val distance: String,
    val time: String,
    val imageUrl: String
)

@Composable
fun RouteListScreen(nav: NavHostController) {

    val list = listOf(
        WalkCourse(
            id = "togashi",
            title = "富樫の里コース",
            distance = "約4.3km",
            time = "約60分",
            imageUrl = "https://picsum.photos/300/200?10"
        ),
        WalkCourse(
            id = "hakusan",
            title = "白山やまなみコース",
            distance = "約3.8km",
            time = "約50分",
            imageUrl = "https://picsum.photos/300/200?11"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
            }
            Text("健康のみち", style = MaterialTheme.typography.titleLarge)
        }

        Spacer(Modifier.height(16.dp))

        list.forEach { course ->
            WalkCourseItem(
                c = course,
                onClick = {
                    // どのコースかをパラメータで渡す
                    nav.navigate(Screen.Map.route(course.id))
                }
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}


@Composable
fun WalkCourseItem(
    c: WalkCourse,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF6F6F6), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(c.title, fontSize = 18.sp)
            Text(
                "距離: ${c.distance} | 所要時間: ${c.time}",
                fontSize = 13.sp,
                color = Color.Gray
            )
            Spacer(Modifier.height(6.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("このルートを選択", color = Color.Black)
            }
        }

        Spacer(Modifier.width(8.dp))

        Image(
            painter = rememberAsyncImagePainter(c.imageUrl),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )
    }
}
