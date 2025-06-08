package com.example.taskify.presentation.settings

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskify.R
import com.example.taskify.data.viewmodel.TaskViewModel
import com.example.taskify.domain.model.taskModel.TaskResponse
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@AndroidEntryPoint
class ProductivityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_productivity)

        setContent {
            ProductivityScreen()
        }
    }
}

@Composable
fun ProductivityScreen(taskViewModel: TaskViewModel = hiltViewModel()) {
    val tasksState = taskViewModel.taskList.collectAsState()

    LaunchedEffect(Unit) {
        taskViewModel.getTasks()
    }

    Log.d("ChartDebug", "Task size: ${tasksState.value.size}")

    Column {
        Text(
            text = "Monthly Progress",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )

        MonthlyProgressBarChart(tasks = tasksState.value)
    }
}

@Composable
fun MonthlyProgressBarChart(
    tasks: List<TaskResponse>
) {
    val stats = calculateMonthlyStats(tasks)
    val currentMonth = LocalDate.now().monthValue
    val visibleMonths = (1..currentMonth).map { Month.of(it) }
    val scrollState = rememberLazyListState()

    LaunchedEffect(Unit) {
        scrollState.scrollToItem(visibleMonths.size - 1)
    }

    val chartHeight = 200.dp
    val labelHeight = 24.dp
    val maxY = stats.values.maxOfOrNull { it.total }?.toFloat()?.coerceAtLeast(1f) ?: 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp, start = 8.dp, end = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .height(chartHeight + labelHeight)
                    .fillMaxWidth()
            ) {
                // Trục Y
                Column(
                    modifier = Modifier
                        .width(40.dp)
                        .height(chartHeight),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    val step = maxY / 4
                    (4 downTo 0).forEach {
                        val label = (step * it).toInt()
                        Text(
                            text = label.toString(),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(chartHeight + labelHeight)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(chartHeight)
                    ) {
                        val dashWidth = 10f
                        val dashGap = 6f
                        val paintColor = Color.Gray.copy(alpha = 0.3f)

                        val step = size.height / 4f
                        for (i in 0..4) {
                            val y = step * i
                            drawLine(
                                color = paintColor,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(
                                        dashWidth,
                                        dashGap
                                    ), 0f
                                )
                            )
                        }
                    }

                    LazyRow(
                        state = scrollState,
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .height(chartHeight + labelHeight)
                    ) {
                        items(visibleMonths, key = { it.value }) { month ->
                            val label = month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                            val stat = stats[month]
                            val completed = stat?.completed?.toFloat() ?: 0f
                            val total = stat?.total?.toFloat() ?: 0f
                            val incomplete = (total - completed).coerceAtLeast(0f)

                            if (total == 0f) {
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .width(60.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Spacer(modifier = Modifier.height(chartHeight))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = label, fontSize = 14.sp, color = Color.Gray)
                                }
                            } else {
                                val completedHeight = (completed / maxY * chartHeight.value).dp
                                val incompleteHeight = (incomplete / maxY * chartHeight.value).dp

                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .width(60.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .height(chartHeight)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(26.dp)
                                                .height(completedHeight)
                                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                .background(Color(0xFF2AB6AF))
                                        )
                                        Box(
                                            modifier = Modifier
                                                .width(26.dp)
                                                .height(incompleteHeight)
                                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                .background(Color(0xFF9CE1DE))
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = label, fontSize = 14.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class MonthlyStats(val completed: Int, val total: Int)

fun calculateMonthlyStats(tasks: List<TaskResponse>): Map<Month, MonthlyStats> {
    val result = mutableMapOf<Month, MonthlyStats>()

    for (i in 1..12) {
        result[Month.of(i)] = MonthlyStats(0, 0)
    }

    tasks.forEach { task ->
        val date = task.toLocalDate()
        if (date != null) {
            val month = date.month
            val currentStats = result[month]!!
            result[month] = if (task.isSuccess) {
                MonthlyStats(currentStats.completed + 1, currentStats.total + 1)
            } else {
                MonthlyStats(currentStats.completed, currentStats.total + 1)
            }
        }
    }

    return result
}

fun TaskResponse.toLocalDate(): LocalDate? {
    return try {
        LocalDate.parse(this.taskDate, DateTimeFormatter.ISO_DATE)
    } catch (e: Exception) {
        null
    }
}