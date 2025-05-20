package com.example.taskify.presentation.main

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.example.taskify.ui.theme.TaskifyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val viewModel: TaskViewModel by viewModels()

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

            // Hiển thị UI chính
            val isChosen = ThemeDataStore.isThemeChosen(this@MainActivity)
            if (!isChosen) {
                startActivity(Intent(this@MainActivity, ThemeSectionActivity::class.java))
                finish()
            } else {
                val theme = ThemeDataStore.getSavedTheme(this@MainActivity)

                setContent {
                    val coroutineScope = rememberCoroutineScope()
                    val tabs = TabItem.values()
                    val pagerState = rememberPagerState { tabs.size }
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
                                    .padding(bottom = padding.calculateBottomPadding())
                            ) { page ->
                                when (page) {
                                    0 -> MainScreen(theme = theme ?: ThemeOption.Teal, viewModel)
                                    1 -> TasksScreen()
                                    2 -> FilterScreen()
                                    3 -> CalendarScreen()
                                    4 -> SettingScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(theme: ThemeOption, viewModel: TaskViewModel) {
    val color = when (theme) {
        ThemeOption.Teal -> Color(0xFF26A69A)
        ThemeOption.Black -> Color(0xFF1B1C1F)
        ThemeOption.Red -> Color(0xFFEA4335)
        ThemeOption.Blue -> Color(0xFF1877F2)
    }

    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEE dd MMM yyyy", Locale.ENGLISH)
    val formatterDate = "Today. " + today.format(formatter)

    val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE) // Format as yyyy-MM-dd
    val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) // Format as HH:mm
    var selectedDate by remember { mutableStateOf(currentDate) }
    var selectedTime by remember { mutableStateOf(currentTime) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    var taskDate by remember { mutableStateOf<LocalDate?>(null) }
    var taskTime by remember { mutableStateOf<LocalTime?>(null) }
    var selectedType by remember { mutableStateOf<String?>(null) }

    var showInputPanel by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF5F5F5))
            .padding(16.dp)
            .padding(top = 48.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "Today",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                "Best platform for creating to-do lists",
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { },
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
                            .clickable { showInputPanel = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "+",
                            fontSize = 22.sp,
                            lineHeight = 1.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        "Tap plus to create a new task",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .background(color = Color(0xFFEEEEEE))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Add your task",
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )

                    Text(
                        text = formatterDate,
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                }
            }
        }

        if (showInputPanel) {
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
                isSuccess = false,  // xử lý success/error theo viewModel.taskResult
                onDismiss = { /* xử lý đóng panel */ },
                onSend = {
                    val taskRequest = TaskRequest(
                        title = title,
                        description = description,
                        createAt = LocalDateTime.now(),
                        taskDate = taskDate!!,
                        taskTime = taskTime!!,
                        type = selectedType!!,
                        isSuccess = isSuccess
                    )
                    viewModel.createTask(taskRequest)
                }
            )
        }
    }
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

    // Lấy offset tính bằng số tab + offset fraction (0..1)
    val indicatorOffsetFraction by remember {
        derivedStateOf {
            pagerState.currentPage + pagerState.currentPageOffsetFraction
        }
    }

    // Tính vị trí thanh trượt, chính giữa icon
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
    Home(R.drawable.home, "Home"),
    Tasks(R.drawable.direct_inbox, "Tasks"),
    Filter(R.drawable.calendar, "Filter"),
    Calendar(R.drawable.category, "Calendar"),
    Settings(R.drawable.setting, "Settings")
}