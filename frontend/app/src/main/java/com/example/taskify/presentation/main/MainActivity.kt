package com.example.taskify.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.taskify.R
import com.example.taskify.data.themeStorage.ThemeDataStore
import com.example.taskify.presentation.auth.dashboard.DashboardActivity
import com.example.taskify.presentation.base.BaseActivity
import com.example.taskify.presentation.calendar.CalendarScreen
import com.example.taskify.presentation.filter.FilterScreen
import com.example.taskify.presentation.settings.SettingScreen
import com.example.taskify.presentation.tasks.TasksScreen
import com.example.taskify.presentation.tasktheme.ThemeSectionActivity
import com.example.taskify.ui.theme.TaskifyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {
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
                                    0 -> MainScreen()
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
fun MainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFE0E0E0))
            .padding(16.dp)
    ) {  }
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
        NavigationBar(containerColor = Color.White) {
            tabs.forEachIndexed { index, tab ->
                val selected = pagerState.currentPage == index

                Box(
                    modifier = Modifier
                        .weight(1f)
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

@Preview(showBackground = true)
@Composable
fun PreviewBottomBarWithIndicator() {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { TabItem.values().size }
    )

    TaskifyTheme {
        BottomBarWithIndicator(
            tabs = TabItem.values(),
            pagerState = pagerState,
            onTabSelected = {}
        )
    }
}