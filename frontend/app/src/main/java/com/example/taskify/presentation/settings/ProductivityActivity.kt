package com.example.taskify.presentation.settings

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskify.R
import com.example.taskify.components.ButtonSection
import com.example.taskify.data.viewmodel.TaskViewModel
import com.example.taskify.data.viewmodel.UserViewModel
import com.example.taskify.domain.model.taskModel.TaskResponse
import com.example.taskify.presentation.calendar.LineGray
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
            ProductivityScreen( onBackClick = { finish() } )
        }
    }
}

@Composable
fun ProductivityScreen(
    taskViewModel: TaskViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val tasksState = taskViewModel.taskList.collectAsState()
    val user by userViewModel.userState.collectAsState()

    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    val formatterDate = today.format(formatter)

    LaunchedEffect(Unit) {
        taskViewModel.getTasks()
        userViewModel.getUserFromLocal()
        userViewModel.loadCurrentUser()
    }

    Log.d("ChartDebug", "Task size: ${tasksState.value.size}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
            .padding(top = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                tint = Color(0xFF1B1C1F).copy(alpha = 0.8f),
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onBackClick() }
            )

            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Productivity",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.person),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )

            if(user != null) {
                val currentUser = user!!

                Text(
                    text = currentUser.username,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentUser.email,
                    fontSize = 15.sp,
                    color = Color(0xFF767E8C)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .height(56.dp)
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE0E5ED)),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row( verticalAlignment = Alignment.CenterVertically ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color(0xFF767E8C),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatterDate,
                    fontSize = 14.sp,
                    color = Color(0xFF767E8C)
                )
            }

            Box(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(Color(0xFFE0E5ED))
            )

            Row( verticalAlignment = Alignment.CenterVertically ) {
                Icon(
                    Icons.Default.BarChart,
                    contentDescription = null,
                    tint = Color(0xFF767E8C),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "My Report",
                    fontSize = 14.sp,
                    color = Color(0xFF767E8C)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Report Progress",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Icon(
                Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFF1B1C1F).copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
        }

        LineGray(modifier = Modifier.padding(horizontal = 16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Statistic Goals",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Icon(
                Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFF1B1C1F).copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
        }

        LineGray(modifier = Modifier.padding(horizontal = 16.dp))

        MonthlyProgressBarChart(tasks = tasksState.value)

        ButtonSection(
            onClick = {},
            text = "More Statistic",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
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

    val density = LocalDensity.current
    val chartHeightPx = with(density) { chartHeight.toPx() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 8.dp, end = 8.dp)
        ) {
            Text(
                text = "Monthly Progress",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(top = 12.dp, start = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .height(chartHeight + labelHeight)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 4.dp)
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
                                    floatArrayOf(dashWidth, dashGap), 0f
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
                                val completedHeightPx = (completed / maxY * chartHeightPx)
                                val completedHeight = with(density) { completedHeightPx.toDp() }

                                val incompleteHeightPx = (incomplete / maxY * chartHeightPx)
                                val incompleteHeight = with(density) { incompleteHeightPx.toDp() }

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
                                                .width(25.dp)
                                                .height(completedHeight)
                                                .clip(
                                                    RoundedCornerShape(
                                                        topStart = 4.dp,
                                                        topEnd = 4.dp
                                                    )
                                                )
                                                .background(Color(0xFF2AB6AF))
                                        )
                                        Box(
                                            modifier = Modifier
                                                .width(25.dp)
                                                .height(incompleteHeight)
                                                .clip(
                                                    RoundedCornerShape(
                                                        topStart = 4.dp,
                                                        topEnd = 4.dp
                                                    )
                                                )
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