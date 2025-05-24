package com.example.taskify.presentation.tasks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskify.R
import com.example.taskify.domain.model.taskModel.TaskResponse
import com.example.taskify.domain.model.themeModel.ThemeOption
import com.example.taskify.presentation.main.toColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator

@Composable
fun TasksScreen(
    theme: ThemeOption,
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    val tasks by taskViewModel.taskList.collectAsState()
    val isLoading by taskViewModel.isLoading.collectAsState() // handle

    LaunchedEffect(Unit) {
        taskViewModel.getTasks()
    }

//    if (isLoading) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            CircularProgressIndicator(
//                color = Color(0xFF24A19C)
//            )
//        }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF5F5F5))
            .padding(16.dp)
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Inbox",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        if(tasks.isEmpty()) {
            Text(
                "No tasks yet!!!",
                color = Color(0xFF767E8C),
                fontSize = 20.sp
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(theme = theme, task = task, onUpdateTask = {})
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    theme: ThemeOption,
    task: TaskResponse,
    onUpdateTask: () -> Unit
) {
    val color = theme.toColor()
    val subTaskCount = task.subTasks.size

    var expanded by remember { mutableStateOf(false) }

    fun formatTaskDate(dateString: String?): String {
        return try {
            dateString?.let {
                val parsedDate = LocalDate.parse(it)
                parsedDate.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH))
            } ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.1.dp),
    ) {
        Column(
            modifier = Modifier
                .background(color = Color.White)
                .height(150.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .background(color = color),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.type,
                        fontSize = 15.sp,
                        color = Color.White
                    )

                    Box { // Bao quanh icon + dropdown menu
                        Icon(
                            Icons.Filled.MoreHoriz,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    expanded = true
                                }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            "Delete Subtask",
                                            color = Color.Black
                                        )
                                    }
                                },
                                onClick = {
                                    onUpdateTask()
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            "Edit Subtask",
                                            color = Color.Black
                                        )
                                    }
                                },
                                onClick = {
                                    onUpdateTask()
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_group),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = color)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .background(Color(0xFFEEEEEE))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_alarm),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color(0xFFFF486A)),
                    modifier = Modifier.size(15.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = task.taskTime,
                    fontSize = 14.sp,
                    color = Color(0xFFFF486A)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_direct_inbox2),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = subTaskCount.toString(),
                    fontSize = 14.sp,
                    color = Color(0xFF767E8C)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = formatTaskDate(task.taskDate),
                    fontSize = 14.sp,
                    color = Color(0xFF767E8C)
                )
            }
        }
    }
}