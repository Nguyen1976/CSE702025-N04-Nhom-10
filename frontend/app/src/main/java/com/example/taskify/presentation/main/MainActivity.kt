package com.example.taskify.presentation.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.taskify.R
import com.example.taskify.data.themeStorage.ThemeDataStore
import com.example.taskify.domain.model.taskModel.TaskRequest
import com.example.taskify.domain.model.themeModel.ThemeOption
import com.example.taskify.presentation.auth.dashboard.DashboardActivity
import com.example.taskify.presentation.base.BaseActivity
import com.example.taskify.presentation.calendar.CalendarScreen
import com.example.taskify.presentation.filter.FilterScreen
import com.example.taskify.presentation.settings.SettingScreen
import com.example.taskify.presentation.tasks.TaskViewModel
import com.example.taskify.presentation.tasks.TasksScreen
import com.example.taskify.presentation.tasktheme.ThemeSectionActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val taskViewModel: TaskViewModel by viewModels()
    private val showInputPanel = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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

                val coroutineScope = rememberCoroutineScope()
                val tabs = TabItem.values()
                val pagerState = rememberPagerState { tabs.size }

                val context = LocalContext.current
                val taskResult by taskViewModel.taskResult.collectAsState()
                val isLoading by taskViewModel.isLoading.collectAsState()

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

                LaunchedEffect(taskResult) {
                    taskResult?.let { result ->
                        result.onSuccess {
                            Toast.makeText(context, "Task created successfully!", Toast.LENGTH_SHORT).show()
                        }.onFailure {
                            Toast.makeText(context, "An error occurred while creating the task, please try again!", Toast.LENGTH_SHORT).show()
                        }
                        taskViewModel.resetTaskResult()
                    }
                }

                MaterialTheme {
                    Scaffold(
                        containerColor = Color.Transparent,
                        bottomBar = {
                            BottomBarWithIndicator(
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
                                    showInputPanel = showInputPanel
                                )
                                1 -> TasksScreen(theme = theme ?: ThemeOption.Teal)
                                2 -> key(page) { CalendarScreen() }
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

                    LaunchedEffect(taskResult) {
                        if (taskResult?.isSuccess == true) {
                            showInputPanel.value = false
                            title = ""
                            description = ""
                            taskDate = null
                            taskTime = null
                            selectedType = null
                            isLoadingLocal = false
                        } else if (taskResult?.isFailure == true) {
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
                                        createAt = LocalDateTime.now(),
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

@Composable
fun MainScreen(
    theme: ThemeOption,
    showInputPanel: MutableState<Boolean>
) {
    val color = theme.toColor()
    val formatterDate = "Today. " + LocalDate.now().format(
        DateTimeFormatter.ofPattern("EEE dd MMM yyyy", Locale.ENGLISH)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
                .padding(top = 48.dp)
        ) {
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
        }
    }
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

@Composable
fun BottomBarWithIndicator(
    tabs: Array<TabItem>,
    pagerState: PagerState,
    onTabSelected: (Int) -> Unit
) {
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
                        colorFilter = if (selected) ColorFilter.tint(Color(0xFF24A19C)) else ColorFilter.tint(Color(0xFFA0AAB8))
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
                .background(Color(0xFF24A19C))
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