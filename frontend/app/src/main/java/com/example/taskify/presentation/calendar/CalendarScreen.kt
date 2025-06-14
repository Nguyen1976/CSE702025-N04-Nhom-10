package com.example.taskify.presentation.calendar

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskify.R
import com.example.taskify.data.viewmodel.TaskViewModel
import com.example.taskify.domain.model.taskModel.SubtaskResponse
import com.example.taskify.domain.model.taskModel.TaskResponse
import com.example.taskify.domain.model.themeModel.ThemeOption
import com.example.taskify.presentation.main.toColor
import com.example.taskify.ui.theme.TaskifyTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarScreen(
    theme: ThemeOption,
    taskViewModel: TaskViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit = {}
) {
    val context = LocalContext.current
    val tasks by taskViewModel.taskList.collectAsState()
    val isSuccessLoading by taskViewModel.isSuccessLoading.collectAsState()
    val updateIsSuccessResult by taskViewModel.updateIsSuccessResult.collectAsState()

    if (isSuccessLoading) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("Loading...") },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = theme.toColor())
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Please wait")
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    LaunchedEffect(updateIsSuccessResult) {
        val result = updateIsSuccessResult
        if (result != null) {
            result.onSuccess {
                Toast.makeText(context, "Task completed!", Toast.LENGTH_SHORT).show()
            }.onFailure { error ->
                Toast.makeText(context, "Failed to update task: ${error.message}", Toast.LENGTH_SHORT).show()
            }
            taskViewModel.resetUpdateIsSuccessResult()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF5F5F5))
            .padding(16.dp)
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Daily Tasks",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(32.dp))

        CalendarSection(
            theme = theme,
            tasks = tasks,
            onUpdateTask = { task ->
                taskViewModel.updateTaskIsSuccess(task, !task.isSuccess)
            }
        )
    }
}

fun String.toLocalDateOrNull(): LocalDate? {
    return try {
        LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (e: Exception) {
        null
    }
}

@Composable
fun CalendarSection(
    theme: ThemeOption,
    tasks: List<TaskResponse>,
    onUpdateTask: (TaskResponse) -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }

    // tạo listState điều khiển scroll
    val listState = rememberLazyListState()
    val daysInMonth = selectedMonth.lengthOfMonth()
    val startDate = selectedMonth.atDay(1)
    val dates = (0 until daysInMonth).map { startDate.plusDays(it.toLong()) }

    val filteredTasks = remember(selectedDate, tasks) {
        tasks.filter {
            it.taskDate.toLocalDateOrNull() == selectedDate
        }
    }

    // scroll khi selectedDate or selectedMonth thay đổi
    LaunchedEffect(selectedDate, selectedMonth) {
        // ngày đc chọn
        val index = dates.indexOfFirst { it == selectedDate }.coerceAtLeast(0)
        listState.animateScrollToItem((index - 2).coerceAtLeast(0))
    }

    Column {
        MonthSelector(
            theme = theme,
            selectedDate = selectedDate,
            onMonthSelected = {
                selectedMonth = it
                selectedDate = it.atDay(1)
            },
            onTodayClick = {
                selectedDate = LocalDate.now()
                selectedMonth = YearMonth.now()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ScrollableCalendar(
            theme = theme,
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it },
            currentMonth = selectedMonth,
            listState = listState
        )

        Spacer(modifier = Modifier.height(12.dp))

        LineGray(modifier = Modifier.padding(horizontal = 6.dp))

        Spacer(modifier = Modifier.height(12.dp))

        DateLabel(selectedDate)

        Spacer(modifier = Modifier.height(16.dp))

        LineGray(modifier = Modifier.padding(horizontal = 6.dp))

        Spacer(modifier = Modifier.height(16.dp))

        TaskList(tasks = filteredTasks, theme = theme, onUpdateTask = onUpdateTask)
    }
}

@Composable
fun LineGray(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color = Color(0xFFEEEEEE))
    )
}

@Composable
fun MonthSelector(
    theme: ThemeOption,
    selectedDate: LocalDate,
    onMonthSelected: (YearMonth) -> Unit,
    onTodayClick: () -> Unit
) {
    val color = theme.toColor()

    var expanded by remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
    val currentYear = java.time.Year.now().value
    val months = (1..12).map { month ->
        YearMonth.of(currentYear, month)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedDate.format(formatter),
                style = MaterialTheme.typography.titleMedium
            )
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier
                    .clickable { expanded = true }
            )
        }

        Text(
            "Today",
            color = color,
            fontSize = 16.sp,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onTodayClick() }
        )
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        months.forEach { month ->
            DropdownMenuItem(
                text = { Text(month.format(formatter)) },
                onClick = {
                    expanded = false
                    onMonthSelected(month)
                }
            )
        }
    }
}

@Composable
fun ScrollableCalendar(
    theme: ThemeOption,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    currentMonth: YearMonth,
    listState: LazyListState
) {
    val color = theme.toColor()

    val daysInMonth = currentMonth.lengthOfMonth()
    val startDate = currentMonth.atDay(1)
    val dates = (0 until daysInMonth).map { startDate.plusDays(it.toLong()) }

    LazyRow(state = listState) {
        items(dates) { date ->
            val isSelected = date == selectedDate
            Column(
                modifier = Modifier
                    .width(60.dp)
                    .padding(horizontal = 4.dp)
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) color else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onDateSelected(date) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.dayOfWeek.name.take(1),
                    color = if (isSelected) Color.White else Color.Gray,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = date.dayOfMonth.toString(),
                    color = if (isSelected) Color.White else Color.Black,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun DateLabel(selectedDate: LocalDate) {
    val today = LocalDate.now()

    val label = if (selectedDate == today) {
        "Today. ${selectedDate.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}"
    } else {
        "${selectedDate.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}. ${selectedDate.format(
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))}"
    }

    Text(
        text = label,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 8.dp, start = 8.dp),
        fontSize = 16.sp,
        color = Color(0xFF1B1C1F)
    )
}

fun String.toTimeOnly(): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("HH:mm[:ss]")
        val time = java.time.LocalTime.parse(this, formatter)
        time.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: Exception) {
        this
    }
}

@Composable
fun TaskList(
    tasks: List<TaskResponse>,
    theme: ThemeOption,
    onUpdateTask: (TaskResponse) -> Unit
) {
    val color = theme.toColor()

    if (tasks.isEmpty()) {
        Text(
            "No tasks for this day!",
            color = Color.Gray,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 16.dp)
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(tasks) { task ->
                val completed = task.isSuccess
                val subtasks = task.subtasks

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_group),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = color)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = task.title ?: "",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Black,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = { onUpdateTask(task) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if(completed) color else Color.White
                            ),
                            modifier = Modifier
                                .then(
                                    if (!completed) {
                                        Modifier.border(
                                            0.5.dp,
                                            Color.LightGray,
                                            RoundedCornerShape(4.dp)
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                                .height(28.dp)
                                .wrapContentWidth(),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(
                                horizontal = 6.dp,
                                vertical = 2.dp
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Done",
                                    fontSize = 14.sp,
                                    lineHeight = 1.sp,
                                    color = if (completed) Color.White else Color.Black
                                )

                                Spacer(modifier = Modifier.width(2.dp))

                                if(completed) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_alarm),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color(0xFFFF486A)),
                            modifier = Modifier.size(15.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = task.taskTime.toTimeOnly(),
                            fontSize = 14.sp,
                            color = Color(0xFFFF486A)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Image(
                            painter = painterResource(id = R.drawable.ic_direct_inbox2),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = (task.subtasks?.size ?: 0).toString(),
                            fontSize = 14.sp,
                            color = Color(0xFF767E8C),
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LineGray()

                    Spacer(modifier = Modifier.height(6.dp))

                    subtasks.forEach { subtask ->
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = subtask.title + ": " + subtask.subtaskDes,
                                fontSize = 15.sp,
                                color = Color.Black.copy(alpha = 0.6f),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            LineGray()
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskListPreview() {
    val theme = ThemeOption.Teal
    val task = listOf(
        TaskResponse(
            _id = "",
            userId = "",
            title = "Meeting with Client",
            description = "My client",
            createAt = "",
            taskTime = "19:00",
            taskDate = "2025-06-04",
            type = "Meeting",
            isSuccess = false,
            subtasks = listOf(
                SubtaskResponse(
                    title = "Test",
                    subtaskDes = "Test des"
                ),
                SubtaskResponse(
                    title = "Test",
                    subtaskDes = "Test des"
                ),
                SubtaskResponse(
                    title = "Test",
                    subtaskDes = "Test des"
                )
            )
        ),
        TaskResponse(
            _id = "",
            userId = "",
            title = "Meeting with Client",
            description = "My client",
            createAt = "",
            taskTime = "19:00",
            taskDate = "2025-06-04",
            type = "Meeting",
            isSuccess = true,
            subtasks = listOf(
                SubtaskResponse(
                    title = "Test",
                    subtaskDes = "Test des"
                ),
                SubtaskResponse(
                    title = "Test",
                    subtaskDes = "Test des"
                ),
                SubtaskResponse(
                    title = "Test",
                    subtaskDes = "Test des"
                )
            )
        )
    )

    TaskifyTheme {
        TaskList(task, theme, onUpdateTask = {})
    }
}

//Card(
//modifier = Modifier
//.fillMaxWidth()
//.padding(vertical = 4.dp, horizontal = 8.dp),
//elevation = CardDefaults.cardElevation(2.dp)
//) {
//    Column(modifier = Modifier.padding(12.dp)) {
//        Text(
//            text = task.title,
//            fontSize = 16.sp,
//            fontWeight = FontWeight.Bold
//        )
//
//        if (task.description.isNotBlank()) {
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(
//                text = task.description,
//                fontSize = 14.sp,
//                color = Color.DarkGray
//            )
//        }
//
//        Spacer(modifier = Modifier.height(6.dp))
//        Text(
//            text = "Time: ${task.taskTime.toTimeOnly()}",
//            fontSize = 13.sp,
//            color = Color.Gray
//        )
//
//        if (task.subtasks.isNotEmpty()) {
//            Spacer(modifier = Modifier.height(6.dp))
//            Text("Subtasks:", fontWeight = FontWeight.Medium)
//            task.subtasks.forEach { sub ->
//                Text(
//                    "- ${sub.title}: ${sub.subtaskDes}",
//                    fontSize = 13.sp
//                )
//            }
//        }
//    }
//}