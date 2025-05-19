package com.example.taskify.presentation.tasktheme

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.taskify.R
import com.example.taskify.components.ButtonSection
import com.example.taskify.data.themeStorage.ThemeDataStore
import com.example.taskify.domain.model.themeModel.ThemeOption
import com.example.taskify.presentation.main.MainActivity
import kotlinx.coroutines.launch

class ThemeSectionActivity : AppCompatActivity() {

    private var selectedTheme = mutableStateOf(ThemeOption.Teal)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ThemeSectionScreen(
//                selectedTheme = selectedTheme.value,
                onThemeSelected = {
                    selectedTheme.value = it
                },
                onConfirm = {
                    lifecycleScope.launch {
                        ThemeDataStore.saveTheme(this@ThemeSectionActivity, selectedTheme.value)
                        startActivity(Intent(this@ThemeSectionActivity, MainActivity::class.java))
                        finish()
                    }
                }
            )
        }
    }
}

@Composable
fun ThemeSectionScreen(
//    selectedTheme: ThemeOption,
    onThemeSelected: (ThemeOption) -> Unit,
    onConfirm: () -> Unit
) {
    val themes = listOf(ThemeOption.Teal, ThemeOption.Blue, ThemeOption.Red, ThemeOption.Blue)
    var selectedTheme by remember { mutableStateOf(themes.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F3FA))
            .padding(16.dp)
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Create to do list",
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Choose your to do list color theme:",
            fontSize = 14.sp,
            color = Color(0xFF767E8C)
        )

        Spacer(modifier = Modifier.height(28.dp))

        themes.forEach { theme ->
            ThemeItem(theme = theme, isSelected = theme == selectedTheme) {
                onThemeSelected(theme)
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        ButtonSection(
            onClick = {
                onConfirm()
            },
            text = "Open Taskify"
        )
    }
}

@Composable
fun ThemeItem(
    theme: ThemeOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = when (theme) {
        ThemeOption.Teal -> Color(0xFF26A69A)
        ThemeOption.Black -> Color(0xFF1B1C1F)
        ThemeOption.Red -> Color(0xFFEA4335)
        ThemeOption.Blue -> Color(0xFF1877F2)
    }

    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (card, icon) = createRefs()

        Card(
//        elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .constrainAs(card) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .clickable { onClick() }
        ) {
            Column(
                modifier = Modifier.background(Color.White).height(104.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .background(color)
                )

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFE7ECF5), shape = CircleShape)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(6.dp)
                                .background(Color(0xFFE7ECF5), shape = RoundedCornerShape(4.dp))
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(6.dp)
                                .background(Color(0xFFE7ECF5), shape = RoundedCornerShape(4.dp))
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(6.dp)
                                .background(Color(0xFFE7ECF5), shape = RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
        }

        if(isSelected) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(color, shape = CircleShape)
                    .clip(CircleShape)
                    .border(1.dp, color, CircleShape)
                    .constrainAs(icon) {
                        start.linkTo(card.start, margin = (-10).dp)
                        top.linkTo(card.top, margin = (-12).dp)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp).clip(CircleShape),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewThemeSelectionScreen() {
    // Bạn có thể cung cấp giá trị mặc định cho selectedTheme
    ThemeSectionScreen(
//        selectedTheme = ThemeOption.Teal,
        onThemeSelected = {},
        onConfirm = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewThemeItemAllThemes() {
    Column {
        ThemeItem(theme = ThemeOption.Teal, isSelected = false, onClick = {})
        Spacer(Modifier.height(8.dp))
        ThemeItem(theme = ThemeOption.Black, isSelected = true, onClick = {})
        Spacer(Modifier.height(8.dp))
        ThemeItem(theme = ThemeOption.Red, isSelected = false, onClick = {})
        Spacer(Modifier.height(8.dp))
        ThemeItem(theme = ThemeOption.Blue, isSelected = true, onClick = {})
    }
}