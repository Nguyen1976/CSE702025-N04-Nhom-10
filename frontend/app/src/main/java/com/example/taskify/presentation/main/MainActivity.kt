package com.example.taskify.presentation.main

import android.Manifest
import android.content.Intent
import android.hardware.lights.Light
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.example.taskify.R
import com.example.taskify.data.themeStorage.ThemeDataStore
import com.example.taskify.data.viewmodel.TaskViewModel
import com.example.taskify.data.viewmodel.UserViewModel
import com.example.taskify.domain.model.taskModel.SubtaskResponse
import com.example.taskify.domain.model.taskModel.TaskRequest
import com.example.taskify.domain.model.taskModel.TaskResponse
import com.example.taskify.domain.model.themeModel.ThemeOption
import com.example.taskify.notification.NotificationHelper
import com.example.taskify.presentation.auth.dashboard.DashboardActivity
import com.example.taskify.presentation.base.BaseActivity
import com.example.taskify.presentation.calendar.CalendarScreen
import com.example.taskify.presentation.calendar.LineGray
import com.example.taskify.presentation.calendar.toTimeOnly
import com.example.taskify.presentation.filter.FilterScreen
import com.example.taskify.presentation.settings.SettingScreen
import com.example.taskify.presentation.tasks.TasksScreen
import com.example.taskify.presentation.tasktheme.ThemeSectionActivity
import com.example.taskify.ui.theme.TaskifyTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val taskViewModel: TaskViewModel by viewModels()
    private val showInputPanel = mutableStateOf(false)

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.createTaskNotificationChannel(this)

        lifecycleScope.launch {
            val token = tokenManager.getAccessToken()
            if (token.isNullOrEmpty()) {
                val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                return@launch
            }

            val isChosen = ThemeDataStore.isThemeChosen(this@MainActivity)
            if (!isChosen) {
                startActivity(Intent(this@MainActivity, ThemeSectionActivity::class.java))
                finish()
                return@launch
            }

            setContent {
                // Lấy theme từ Flow
                val themeFlow = ThemeDataStore.getSavedTheme(this@MainActivity)
                val theme by themeFlow.collectAsState(initial = ThemeOption.Teal)

                val tasksState = taskViewModel.taskList.collectAsState()
                val coroutineScope = rememberCoroutineScope()
                val tabs = TabItem.values()
                val pagerState = rememberPagerState { tabs.size }
                var previousPage by remember { mutableStateOf(pagerState.currentPage) }

                val context = LocalContext.current
                val createTaskResult = taskViewModel.createTaskResult.collectAsState()
                val isLoading by taskViewModel.isLoading.collectAsState()

                val postNotificationPermission =
                    rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

                LaunchedEffect(key1 = true) {
                    if (!postNotificationPermission.status.isGranted) {
                        postNotificationPermission.launchPermissionRequest()
                    }
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                LaunchedEffect(createTaskResult.value) {
                    createTaskResult.value?.let { result ->
                        result.onSuccess {
                            Toast.makeText(context, "Task created successfully!", Toast.LENGTH_SHORT).show()
                            taskViewModel.resetCreateTaskResult()
                            taskViewModel.getTasks()
                        }.onFailure {
                            Toast.makeText(context, "Error occurred while creating the task, please try again!", Toast.LENGTH_SHORT).show()
                            taskViewModel.resetCreateTaskResult()
                        }
                    }
                }

                LaunchedEffect(pagerState.currentPage) {
                    val current = pagerState.currentPage
                    if (current == 1 && previousPage != current) {
                        taskViewModel.getTasks()
                    }
                    previousPage = current
                }

                MaterialTheme {
                    Scaffold(
                        containerColor = Color.Transparent,
                        bottomBar = {
                            BottomBarWithIndicator(
                                theme = theme ?: ThemeOption.Teal,
                                tabs = tabs,
                                pagerState = pagerState,
                                onTabSelected = { index ->
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            )
                        }
                    ) { padding ->
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = padding.calculateBottomPadding()),
                            userScrollEnabled = !showInputPanel.value
                        ) { page ->
                            when (page) {
                                0 -> MainScreen(
                                    theme = theme ?: ThemeOption.Teal,
                                    taskViewModel = taskViewModel,
                                    showInputPanel = showInputPanel,
                                    tasks = tasksState.value
                                )
                                1 -> TasksScreen(theme = theme ?: ThemeOption.Teal)
                                2 -> key(page) { CalendarScreen(theme = theme ?: ThemeOption.Teal) }
                                3 -> key(page) { FilterScreen() }
                                4 -> SettingScreen()
                            }
                        }
                    }

                    var title by remember { mutableStateOf("") }
                    var description by remember { mutableStateOf("") }
                    var isSuccess by remember { mutableStateOf(false) }
                    var taskDate by remember { mutableStateOf<LocalDate?>(null) }
                    var taskTime by remember { mutableStateOf<LocalTime?>(null) }
                    var selectedType by remember { mutableStateOf<String?>(null) }
                    var isLoadingLocal by remember { mutableStateOf(false) }

                    val context = LocalContext.current

                    LaunchedEffect(createTaskResult.value) {
                        val result = createTaskResult.value
                        if (result?.isSuccess == true) {
                            showInputPanel.value = false
                            title = ""
                            description = ""
                            taskDate = null
                            taskTime = null
                            selectedType = null
                            isLoadingLocal = false
                        } else if (result?.isFailure == true) {
                            isLoadingLocal = false
                        }
                    }

                    LaunchedEffect(showInputPanel.value) {
                        if (showInputPanel.value) {
                            title = ""
                            description = ""
                            taskDate = null
                            taskTime = null
                            selectedType = null
                            isSuccess = false
                            isLoadingLocal = false
                        }
                    }

                    if (showInputPanel.value) {
                        TaskInputPanel(
                            title = title,
                            onTitleChange = { title = it },
                            description = description,
                            onDescriptionChange = { description = it },
                            taskDate = taskDate,
                            onTaskDateChange = { taskDate = it },
                            taskTime = taskTime,
                            onTaskTimeChange = { taskTime = it },
                            selectedType = selectedType,
                            onTypeSelected = { selectedType = it },
                            isSuccess = isSuccess,
                            isLoading = isLoadingLocal,
                            onDismiss = { showInputPanel.value = false },
                            onSend = {
                                if (title.isNotBlank() && taskDate != null && taskTime != null && selectedType != null) {
                                    isLoadingLocal = true
                                    val taskRequest = TaskRequest(
                                        title = title,
                                        description = description,
                                        taskDate = taskDate!!,
                                        taskTime = taskTime!!,
                                        type = selectedType!!,
                                        isSuccess = isSuccess
                                    )
                                    taskViewModel.createTask(taskRequest)
                                } else {
                                    Toast.makeText(context, "Please fill in all the information!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (showInputPanel.value) {
            showInputPanel.value = false
        } else {
            super.onBackPressed()
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    userViewModel: UserViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel(),
    tasks: List<TaskResponse>,
    theme: ThemeOption,
    showInputPanel: MutableState<Boolean>,
    onDismissRequest: () -> Unit = {}
) {
    val context = LocalContext.current

    val isSuccessLoading by taskViewModel.isSuccessLoading.collectAsState()
    val updateIsSuccessResult by taskViewModel.updateIsSuccessResult.collectAsState()

    val user by userViewModel.userState.collectAsState()
    val today = LocalDate.now().toString()
    val tasksToday = tasks.filter { it.taskDate == today }

    val color = theme.toColor()
    val formatterDate = "Today. " + LocalDate.now().format(
        DateTimeFormatter.ofPattern("EEE dd MMM yyyy", Locale.ENGLISH)
    )

    val postNotificationPermission = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

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

    LaunchedEffect(postNotificationPermission.status.isGranted) {
        if (!postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        } else {
            tasks.forEach { task ->
                NotificationHelper.scheduleTaskReminder(
                    context = context,
                    taskId = task._id,
                    taskTitle = task.title,
                    taskDate = task.taskDate,
                    taskTime = task.taskTime
                )
            }
        }
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

    LaunchedEffect(Unit) {
        userViewModel.getUserFromLocal()
        userViewModel.loadCurrentUser()
        taskViewModel.getTasks()
        NotificationHelper.createTaskNotificationChannel(context)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
                .padding(top = 48.dp)
        ) {
            Text(
                text = "\uD83D\uDC4B Hello, ${user?.username ?: "there"}! Ready to conquer today?",
                fontSize = 17.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Today", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Best platform for creating to-do lists",
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(0.1.dp),
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .height(150.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .background(color = color)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .background(color = color, RoundedCornerShape(4.dp))
                                .clickable { showInputPanel.value = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+", fontSize = 22.sp, color = Color.White)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text("Tap plus to create a new task", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Box(
                        Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .background(Color(0xFFEEEEEE))
                    )

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Add your task", fontSize = 12.sp, color = Color.Black.copy(alpha = 0.6f))
                        Text(formatterDate, fontSize = 12.sp, color = Color.Black.copy(alpha = 0.6f))
                    }
                }
            }

            LineGray(modifier = Modifier.padding(top = 32.dp))

            Spacer(modifier = Modifier.height(16.dp))

            if(tasksToday.isEmpty()) {
                Text(
                    "There are no tasks today!",
                    color = Color(0xFF767E8C),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    "Tiny tasks, big wins. Let’s roll!",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(tasksToday) {task ->
                        TaskListForToday(
                            task = task,
                            theme = theme,
                            onUpdateTask = {
                                taskViewModel.updateTaskIsSuccess(task, !task.isSuccess)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskListForToday(
    task: TaskResponse,
    theme: ThemeOption,
    onUpdateTask: (TaskResponse) -> Unit
) {
    val color = theme.toColor()
    val completed = task.isSuccess

    Box(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .wrapContentHeight(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.type,
                    fontSize = 15.sp,
                    color = Color.Black
                )

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

            Spacer(modifier = Modifier.height(8.dp))

            LineGray()

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = task.title,
                fontSize = 15.sp,
                color = Color.Black
            )
            Text(
                text = task.description,
                fontSize = 13.sp,
                color = Color.Black
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_alarm),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color(0xFFFF486A)),
                    modifier = Modifier.size(13.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = task.taskTime.toTimeOnly(),
                    fontSize = 12.sp,
                    color = Color(0xFFFF486A)
                )
            }
        }
    }

}

@Composable
fun BottomBarWithIndicator(
    theme: ThemeOption,
    tabs: Array<TabItem>,
    pagerState: PagerState,
    onTabSelected: (Int) -> Unit
) {
    val color = theme.toColor()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val tabCount = tabs.size
    val tabWidth = screenWidth / tabCount
    val indicatorWidth = 32.dp

    val indicatorOffsetFraction by remember {
        derivedStateOf {
            pagerState.currentPage + pagerState.currentPageOffsetFraction
        }
    }

    val indicatorOffset by animateDpAsState(
        targetValue = tabWidth * indicatorOffsetFraction + (tabWidth - indicatorWidth) / 2,
        label = "indicatorOffset"
    )

    Box(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
    ) {
        NavigationBar(
            containerColor = Color.White,
            modifier = Modifier
                .padding(horizontal = 3.dp)
                .padding(end = 2.dp)
                .height(84.dp)
        ) {
            tabs.forEachIndexed { index, tab ->
                val selected = pagerState.currentPage == index

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 10.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = tab.iconRes),
                        contentDescription = tab.label,
                        modifier = Modifier.size(26.dp),
                        colorFilter = if (selected) ColorFilter.tint(color) else ColorFilter.tint(Color(0xFFA0AAB8))
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(indicatorWidth)
                .height(3.dp)
                .align(Alignment.TopStart)
                .background(color)
        )
    }
}

enum class TabItem(@DrawableRes val iconRes: Int, val label: String) {
    Home(R.drawable.ic_home, "Home"),
    Tasks(R.drawable.ic_direct_inbox, "Tasks"),
    Filter(R.drawable.ic_calendar, "Filter"),
    Calendar(R.drawable.ic_category, "Calendar"),
    Settings(R.drawable.ic_setting, "Settings")
}

fun ThemeOption.toColor(): Color = when (this) {
    ThemeOption.Teal -> Color(0xFF26A69A)
    ThemeOption.Black -> Color(0xFF1B1C1F)
    ThemeOption.Red -> Color(0xFFEA4335)
    ThemeOption.Blue -> Color(0xFF1877F2)
    ThemeOption.LightRed -> Color(0xFFE57373)
    ThemeOption.LightBlue -> Color(0xFF42A5F5)
    ThemeOption.LightGreen -> Color(0xFF81C784)
    ThemeOption.LightOrange -> Color(0xFFFFB74D)
    ThemeOption.DarkCharcoal -> Color(0xFF212121)
    ThemeOption.BabyPink -> Color(0xFFF8BBD0)
    ThemeOption.LightYellow -> Color(0xFFFFF176)
    ThemeOption.MediumBlue -> Color(0xFF2196F3)
    ThemeOption.LightPurple -> Color(0xFFBA68C8)
    ThemeOption.SlateGray -> Color(0xFF546E7A)
    ThemeOption.LightCyan -> Color(0xFF4DD0E1)
    ThemeOption.MintGreen -> Color(0xFF4DB6AC)
    ThemeOption.HotPink -> Color(0xFFF06292)
    ThemeOption.VividOrange -> Color(0xFFFF5722)
}