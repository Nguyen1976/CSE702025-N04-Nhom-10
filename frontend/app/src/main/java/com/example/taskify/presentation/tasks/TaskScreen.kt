package com.example.taskify.presentation.tasks

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskify.R
import com.example.taskify.domain.model.taskModel.SubtaskResponse
import com.example.taskify.domain.model.taskModel.TaskResponse
import com.example.taskify.domain.model.themeModel.ThemeOption
import com.example.taskify.presentation.main.toColor
import com.example.taskify.ui.theme.TaskifyTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TasksScreen(
    theme: ThemeOption,
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    val tasks by taskViewModel.taskList.collectAsState()
    val isLoading by taskViewModel.isLoading.collectAsState() // handle
    val isSubtaskLoading by taskViewModel.isSubtaskLoading.collectAsState()

    val context = LocalContext.current
    val errorMessage by taskViewModel.errorMessage.collectAsState()

    var selectedTask by remember { mutableStateOf<TaskResponse?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(tasks) {
        selectedTask = selectedTask?.let { oldSelected ->
            tasks.find { it.id == oldSelected.id }
        }
    }

    LaunchedEffect(Unit) {
        taskViewModel.getTasks()
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            taskViewModel.clearErrorMessage()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF24A19C))
                }
            } else if (tasks.isEmpty()) {
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
                        TaskCard(
                            theme = theme,
                            task = task,
                            onDeleteTask = {
                                taskViewModel.deleteTask(task.id)
                            },
                            onDetailTask = {
                                selectedTask = task
                                showBottomSheet = true
                            }
                        )
                    }
                }

                if (showBottomSheet && selectedTask != null) {
                    Log.d("DEBUG", "Show bottom sheet with task: ${selectedTask!!.title}")
                    TaskBottomSheet(
                        task = selectedTask!!,
                        theme = theme,
                        subtasks = selectedTask!!.subTasks,
                        onEditTask = { /** composable task input panel **/ },
                        onUpdateTask = { taskViewModel.updateTask(selectedTask!!) },
                        onDeleteSubtask = { index ->
                            selectedTask?.let { currentTask ->
                                val updatedSubtasks = currentTask.subTasks.toMutableList()
                                if (index in updatedSubtasks.indices) {
                                    updatedSubtasks.removeAt(index)
                                    selectedTask = currentTask.copy(subTasks = updatedSubtasks)
                                    taskViewModel.updateTask(selectedTask!!)
                                }
                            }},
                        onEditSubtask = { /** composable subtask input panel **/ },
                        onDismissRequest = {
                            showBottomSheet = false
                            selectedTask = null
                        }
                    )
                }
            }

        }
    }

    LoadingDialog(show = isSubtaskLoading)

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}

@Composable
fun TaskCard(
    theme: ThemeOption,
    task: TaskResponse,
    onDeleteTask: () -> Unit,
    onDetailTask: () -> Unit,
) {
    Log.d("DEBUG_TASK", "Task: $task")

    val color = theme.toColor()

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

                    Box {
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
                                            modifier = Modifier
                                                .size(28.dp)
                                                .padding(end = 8.dp)
                                        )
                                        Text(
                                            "Delete Task",
                                            color = Color.Black
                                        )
                                    }
                                },
                                onClick = {
                                    onDeleteTask()
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Info,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier
                                                .size(28.dp)
                                                .padding(end = 8.dp)
                                        )
                                        Text(
                                            "View Detail",
                                            color = Color.Black
                                        )
                                    }
                                },
                                onClick = {
                                    onDetailTask()
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
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.weight(1f))
                Log.d("DEBUGTASK", "Task: ${task}")

                Image(
                    painter = painterResource(id = R.drawable.ic_complete),
                    contentDescription = null,
                    colorFilter = if (task.isSuccess)
                        ColorFilter.tint(color)
                    else ColorFilter.tint(Color(0xFFA0AAB8)),
                    modifier = Modifier.size(24.dp)
                )
            }

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
                    text = task.subTasks.size.toString(),
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

@Composable
fun LoadingDialog(show: Boolean, onDismissRequest: () -> Unit = {}) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("Loading...") },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Please wait")
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBottomSheet(
    task: TaskResponse,
    theme: ThemeOption,
    onEditTask: () -> Unit,
    onUpdateTask: () -> Unit,
    onDeleteSubtask: (index: Int) -> Unit,
    onEditSubtask: () -> Unit,
    subtasks: List<SubtaskResponse>,
    onDismissRequest: () -> Unit
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = Color.White
    ) {
        TaskBottomSheetContent(
            task = task,
            theme = theme,
            onEditTask = onEditTask,
            onUpdateTask = onUpdateTask,
            onDeleteSubtask = onDeleteSubtask,
            onEditSubtask = onEditSubtask,
            onDismissRequest = onDismissRequest,
            subtasks = subtasks
        )
    }
}

@Composable
fun TaskBottomSheetContent(
    task: TaskResponse,
    theme: ThemeOption,
    onEditTask: () -> Unit,
    onUpdateTask: () -> Unit,
    onDeleteSubtask: (index: Int) -> Unit,
    onEditSubtask: () -> Unit,
    onDismissRequest: () -> Unit,
    subtasks: List<SubtaskResponse>
) {
    val color = theme.toColor()
    val backgroundColor = theme.toColor().copy(alpha = 0.08f)

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

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .height(600.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Detail Task",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Icon(
                Icons.Default.Close,
                contentDescription = null,
                tint = Color(0xFF767E8C),
                modifier = Modifier.clickable { onDismissRequest() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_group),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = color),
                modifier = Modifier
                    .padding(top = 2.dp)
                    .align(Alignment.Top)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = task.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = task.description,
                    fontSize = 14.sp,
                    color = Color(0xFF767E8C)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
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

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = formatTaskDate(task.taskDate),
                        fontSize = 14.sp,
                        color = Color(0xFF767E8C)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onEditTask() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Edit Task"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(Color(0xFFEEEEEE))
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Sub-task",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
        )

        if (subtasks.isEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "No sub-task yet!",
                color = Color(0xFF767E8C),
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                itemsIndexed(subtasks) {index, subtask ->
                    SubTaskItem(
                        subtask = subtask,
                        onDeleteSubtask = { onDeleteSubtask(index) },
                        onEditSubtask = { onEditSubtask() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = color
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    "Add Sub-task",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = color
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun SubTaskItem(
    subtask: SubtaskResponse,
    onDeleteSubtask: () -> Unit,
    onEditSubtask: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Log.d("DEBUG_TASK", "Subtask: $subtask")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_group),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = Color(0xFF767E8C)),
            modifier = Modifier
                .padding(top = 2.dp)
                .align(Alignment.Top)
        )

        Spacer(modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = subtask.title,
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )

                Box {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = null,
                        tint = Color.Black.copy(0.8f),
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
                                        modifier = Modifier
                                            .size(28.dp)
                                            .padding(end = 8.dp)
                                    )
                                    Text(
                                        "Delete Sub-task",
                                        color = Color.Black
                                    )
                                }
                            },
                            onClick = {
                                onDeleteSubtask()
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .padding(end = 8.dp)
                                    )
                                    Text(
                                        "Edit Sub-task",
                                        color = Color.Black
                                    )
                                }
                            },
                            onClick = {
                                onEditSubtask()
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtask.subtaskDes,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Color(0xFF767E8C)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskBottomSheetPreview() {
    val task = TaskResponse(
        id = "1",
        userId = "user123",
        title = "Play video games tonight",
        description = "Play valorant with friends Play valorant with friends Play valorant with friends",
        createAt = "2025-05-25 19:30:00",
        taskDate = "2025-05-25",
        taskTime = "20:30:00",
        type = "Entertainment",
        isSuccess = false,
        subTasks = listOf(
            SubtaskResponse(
                title = "3 ranks",
                subtaskDes = "Play valorant with friends Play valorant with friends Play valorant with friends"
            ),
            SubtaskResponse(title = "2 ranks", subtaskDes = "Play yoru")
        )
    )

    val theme = ThemeOption.Teal

    TaskBottomSheetContent(
        task = task,
        theme = theme,
        onUpdateTask = {},
        onEditTask = {},
        onDeleteSubtask = {},
        onEditSubtask = {},
        onDismissRequest = {},
        subtasks = listOf(
            SubtaskResponse(title = "3 ranks", subtaskDes = "Play omen"),
            SubtaskResponse(title = "2 ranks", subtaskDes = "Play yoru")
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun SubtaskPreview() {
    TaskifyTheme {
        SubTaskItem(
            subtask = SubtaskResponse(title = "3 ranks", subtaskDes = "Play valorant with " +
                    "friends Play valorant with friends Play valorant with friends"),
            onDeleteSubtask = {},
            onEditSubtask = {}
        )
    }
}