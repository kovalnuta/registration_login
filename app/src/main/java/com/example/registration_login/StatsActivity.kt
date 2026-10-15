package com.example.registration_login

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class StatsActivity : ComponentActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("myLogs", "--- StatsActivity onCreate ---")
        dbHelper = DBHelper(this)
        setContent {
            StatsScreen(dbHelper)
        }
    }
}

@Composable
fun StatsScreen(dbHelper: DBHelper) {
    val stats = dbHelper.getAllStats()

    Log.d("myLogs", "stats rows = ${stats.size}")
    for ((name, count) in stats) {
        Log.d("myLogs", "user = $name, count = $count")
    }

    val pinkPrimary = Color(0xFFE91E63)
    val pinkDark = Color(0xFFC2185B)
    val pinkLight = Color(0xFFF8BBD0)

    Box(modifier = Modifier.fillMaxSize()) {
        // Фон — картинка салона
        Image(
            painter = painterResource(id = R.drawable.salon),
            contentDescription = "Фон салона",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Затемняющий слой
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "KovalBeauty",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Статистика входов",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.92f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Имя пользователя — количество",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = pinkDark,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    if (stats.isEmpty()) {
                        Text(
                            text = "Пока никто не входил",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        // Список статистики внутри карточки
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            stats.forEach { pair ->
                                val name = pair.first
                                val count = pair.second

                                // Строка статистики
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(pinkLight.copy(alpha = 0.4f))
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = name,
                                        fontSize = 16.sp,
                                        color = pinkDark,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = count.toString(),
                                        fontSize = 18.sp,
                                        color = pinkPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}