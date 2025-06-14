package com.example.taskify.presentation.settings

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.taskify.R
import com.example.taskify.components.ButtonSection
import com.example.taskify.data.themeStorage.ThemeDataStore
import com.example.taskify.domain.model.themeModel.ThemeOption
import com.example.taskify.presentation.main.toColor
import kotlinx.coroutines.launch

class ChooseThemeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choose_theme)

        setContent {
            val selectedTheme = remember { mutableStateOf(ThemeOption.Teal) }

            ChooseThemeScreen(
                selectedTheme = selectedTheme.value,
                onThemeSelected = {
                    selectedTheme.value = it
                },
                onConfirm = {
                    lifecycleScope.launch {
                        ThemeDataStore.saveTheme(this@ChooseThemeActivity, selectedTheme.value)
                        Toast.makeText(this@ChooseThemeActivity, "Theme changed successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            )
        }
    }
}

@Composable
fun ChooseThemeScreen(
    selectedTheme: ThemeOption,
    onThemeSelected: (ThemeOption) -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF5F5F5))
            .padding(16.dp)
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val themes = ThemeOption.values().toList()
        val color = selectedTheme.toColor()

        Text(
            "Theme",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
//            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(themes) { theme ->
                ThemeSettingItem(theme = theme, isSelected = theme == selectedTheme) {
                    onThemeSelected(theme)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ButtonSection(
            onClick = {
                onConfirm()
            },
            text = "Apply Change",
            colors = ButtonDefaults.buttonColors(
                containerColor = color
            )
        )
    }
}

@Composable
fun ThemeSettingItem(
    theme: ThemeOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = when (theme) {
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
        else -> Color.Gray
    }

    ConstraintLayout(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
    ) {
        val (card, icon) = createRefs()

        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .constrainAs(card) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClick() }
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