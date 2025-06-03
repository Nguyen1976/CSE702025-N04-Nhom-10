package com.example.taskify.presentation.tasks

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskify.R
import com.example.taskify.components.dragDrop.rememberDragDropListState
import com.example.taskify.data.viewmodel.TaskViewModel
import com.example.taskify.domain.model.taskModel.SubtaskResponse
import com.example.taskify.domain.model.taskModel.TaskResponse
import com.example.taskify.domain.model.themeModel.ThemeOption
import com.example.taskify.presentation.main.toColor
import com.example.taskify.ui.theme.TaskifyTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TasksScreen(
    theme: ThemeOption,
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    val color = theme.toColor()

    val tasks by taskViewModel.taskList.collectAsState()
    val isLoading by taskViewModel.isLoading.collectAsState()
    val isSubtaskLoading by taskViewModel.isSubtaskLoading.collectAsState()
    val context = LocalContext.current

    val errorMessage by taskViewModel.errorMessage.collectAsState()
    val updateTaskResult = taskViewModel.updateTaskResult.collectAsState()
    val deleteTaskResult = taskViewModel.deleteTaskResult.collectAsState()
    val isDragDropUpdate by taskViewModel.isDragDropUpdate.collectAsState()

    val editTaskLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            taskViewModel.getTasks()
        }
    }

    var selectedTask by remember { mutableStateOf<TaskResponse?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(tasks) {
        selectedTask = selectedTask?.let { oldSelected ->
            tasks.find { it._id == oldSelected._id }
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

    LaunchedEffect(updateTaskResult.value, isDragDropUpdate) {
        val result = updateTaskResult.value
        if (result != null) {
            if (!isDragDropUpdate) {
                result.onSuccess { updatedTask ->
                    Toast.makeText(context, "Task updated successfully", Toast.LENGTH_SHORT).show()
                }.onFailure { error ->
                    Toast.makeText(context, "Failed to update task: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
            taskViewModel.resetUpdateTaskResult()
        }
    }

    LaunchedEffect(deleteTaskResult.value) {
        deleteTaskResult.value?.let { result ->
            result.onSuccess {
                Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(context, "Failed to delete task, please try again later!", Toast.LENGTH_SHORT).show()
            }
            taskViewModel.resetUpdateTaskResult()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = color)
                    }
                }
                tasks.isEmpty() -> {
                    Text(
                        "No tasks yet!!!",
                        color = Color(0xFF767E8C),
                        fontSize = 20.sp
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(tasks, key = { it._id }) { task ->
                            TaskCard(
                                theme = theme,
                                task = task,
                                onDeleteTask = { taskViewModel.deleteTask(task._id) },
                                onDetailTask = {
                                    selectedTask = task
                                    showBottomSheet = true
                                }
                            )
                        }
                    }
                }
            }

            if (showBottomSheet && selectedTask != null) {
                TaskBottomSheet(
                    task = selectedTask!!,
                    theme = theme,
                    subtasks = selectedTask!!.subtasks ?: emptyList(),
                    onEditTask = {
                        selectedTask?.let { task ->
                            val intent = Intent(context, TaskEditActivity::class.java).apply {
                                putExtra("_id", task._id)
                                putExtra("userId", task.userId)
                                putExtra("title", task.title)
                                putExtra("description", task.description)
                                putExtra("createAt", task.createAt)
                                putExtra("taskDate", task.taskDate.toString())
                                putExtra("taskTime", task.taskTime.toString())
                                putExtra("type", task.type)
                                putExtra("isSuccess", task.isSuccess)
                                val subtaskList = task.subtasks ?: emptyList()
                                putParcelableArrayListExtra("subtask", ArrayList(subtaskList))
                                putExtra("theme", theme.name)
                            }
                            editTaskLauncher.launch(intent)
                        }
                    },
                    onUpdateTask = { newSubtask: SubtaskResponse ->
                        selectedTask?.let { currentTask ->
                            val updatedSubtasks = (currentTask.subtasks ?: emptyList()).toMutableList()
                            updatedSubtasks.add(newSubtask)
                            val updatedTask = currentTask.copy(subtasks = updatedSubtasks)
                            selectedTask = updatedTask
                            taskViewModel.updateTask(updatedTask)
                        }
                    },
                    onDeleteSubtask = { index ->
                        selectedTask?.let { task ->
                            taskViewModel.deleteSubtask(task, index)
                        }
                    },
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    onMove = { fromIndex, toIndex ->
                        selectedTask?.let { task ->
                            val updatedSubtasks = (task.subtasks ?: emptyList()).toMutableList()
                            if (fromIndex in updatedSubtasks.indices && toIndex in updatedSubtasks.indices) {
                                val item = updatedSubtasks.removeAt(fromIndex)
                                updatedSubtasks.add(toIndex, item)
                                val updatedTask = task.copy(subtasks = updatedSubtasks)
                                selectedTask = updatedTask
                                taskViewModel.updateTaskFromDragDrop(updatedTask)
                            }
                        }
                    },
                    lazyListState = rememberLazyListState()
                )
            }
        }
    }

    LoadingDialog(theme = theme, show = isSubtaskLoading)
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
    val formattedTime = LocalTime.parse(task.taskTime).format(DateTimeFormatter.ofPattern("HH:mm"))

    Log.d("Task_time", "$formattedTime")

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

    val icon: ImageVector = when (task.type) {
        "Meeting" -> Icons.Default.Groups
        "Entertainment" -> Icons.Default.SportsEsports
        "Study" -> Icons.Default.AutoStories
        "Work" -> Icons.Default.Work
        else -> Icons.Default.Info
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = Color.White)

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = task.type,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))

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

                // Giới hạn title bằng weight
                Text(
                    text = task.title ?: "",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.Black,
                    maxLines = 1,
                    modifier = Modifier.weight(1f) // 👈 Chìa khóa nằm ở đây
                )

                // Icon nằm cố định bên phải
                Spacer(modifier = Modifier.width(12.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_complete),
                    contentDescription = null,
                    colorFilter = if (task.isSuccess)
                        ColorFilter.tint(color)
                    else
                        ColorFilter.tint(Color(0xFFA0AAB8)),
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
                    text = formattedTime,
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
                    text = (task.subtasks?.size ?: 0).toString(),
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
fun LoadingDialog(
    theme: ThemeOption,
    show: Boolean,
    onDismissRequest: () -> Unit = {}
) {
    if (show) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBottomSheet(
    task: TaskResponse,
    theme: ThemeOption,
    onEditTask: () -> Unit,
    onUpdateTask: (SubtaskResponse) -> Unit,
    onDeleteSubtask: (index: Int) -> Unit,
    subtasks: List<SubtaskResponse> = emptyList(),
    onDismissRequest: () -> Unit,
    onMove: (Int, Int) -> Unit,
    lazyListState: LazyListState
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
            onDismissRequest = onDismissRequest,
            subtasks = subtasks,
            onMove = onMove,
            lazyListState = lazyListState
        )
    }
}

@Composable
fun TaskBottomSheetContent(
    task: TaskResponse,
    theme: ThemeOption,
    onEditTask: () -> Unit,
    onUpdateTask: (SubtaskResponse) -> Unit,
    onDeleteSubtask: (index: Int) -> Unit,
    onDismissRequest: () -> Unit,
    subtasks: List<SubtaskResponse>,
    onMove: (Int, Int) -> Unit,
    lazyListState: LazyListState
) {
    val color = theme.toColor()
    val backgroundColor = theme.toColor().copy(alpha = 0.08f)
    val formattedTime = LocalTime.parse(task.taskTime).format(DateTimeFormatter.ofPattern("HH:mm"))
    var showAddDialog by remember { mutableStateOf(false) }

    var newTitle by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val dragDropListState = rememberDragDropListState(
        onMove = onMove,
        lazyListState = lazyListState
    )

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
                    text = task.title ?: "",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = task.description ?: "",
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
                        text = formattedTime,
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
                state = dragDropListState.lazyListState,
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { offset -> dragDropListState.onDragStart(offset) },
                            onDrag = { change, offset ->
                                change.consumeAllChanges()
                                dragDropListState.onDrag(offset)

                                val scrollAmount = dragDropListState.checkForOverScroll()
                                if (scrollAmount != 0f) {
                                    dragDropListState.overscrollJob?.cancel()
                                    dragDropListState.overscrollJob = scope.launch {
                                        dragDropListState.lazyListState.scrollBy(scrollAmount)
                                    }
                                }
                            },
                            onDragEnd = { dragDropListState.onDragInterrupted() },
                            onDragCancel = { dragDropListState.onDragInterrupted() }
                        )
                    }
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                itemsIndexed(subtasks) {index, subtask ->
                    val isDragging = index == dragDropListState.currentIndexOfDraggedItem
                    val translationY = if (isDragging) dragDropListState.elementDisplacement ?: 0f else 0f
                    val bgColor = if (isDragging) Color(0xFFF0F0F0) else Color.White

                    Column(
                        modifier = Modifier
                            .graphicsLayer { this.translationY = translationY }
                            .background(bgColor, shape = RoundedCornerShape(8.dp))
                            .fillMaxWidth()
                    ) {
                        SubTaskItem(
                            subtask = subtask,
                            onDeleteSubtask = { onDeleteSubtask(index) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { showAddDialog = true },
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

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add New Sub-task", fontSize = 18.sp, color = color) },
                text = {
                    Column {
                        BasicTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                .padding(8.dp),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                if (newTitle.isEmpty()) Text("Title", color = Color.Gray)
                                innerTextField()
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicTextField(
                            value = newDescription,
                            onValueChange = { newDescription = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                .padding(8.dp),
                            decorationBox = { innerTextField ->
                                if (newDescription.isEmpty()) Text("Description", color = Color.Gray)
                                innerTextField()
                            }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newSubtask = SubtaskResponse(
                                title = newTitle,
                                subtaskDes = newDescription
                            )
                            onUpdateTask(newSubtask)
                            showAddDialog = false
                            newTitle = ""
                            newDescription = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = color
                        ),
                        shape = RoundedCornerShape(10.dp),
                        enabled = newTitle.isNotBlank() && newDescription.isNotBlank()
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showAddDialog = false }
                    ) {
                        Text("Cancel", color = color)
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun SubTaskItem(
    subtask: SubtaskResponse,
    onDeleteSubtask: () -> Unit,
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

                Icon(
                    Icons.Default.DeleteSweep,
                    contentDescription = null,
                    tint = Color(0xFFFF3333),
                    modifier = Modifier
                        .clickable { onDeleteSubtask() }
                )
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
private fun SubtaskPreview() {
    TaskifyTheme {
        SubTaskItem(
            subtask = SubtaskResponse(title = "3 ranks", subtaskDes = "Play valorant with " +
                    "friends Play valorant with friends Play valorant with friends"),
            onDeleteSubtask = {},
        )
    }
}