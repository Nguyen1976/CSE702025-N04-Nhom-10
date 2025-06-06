package com.example.taskify.presentation.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskify.R

class HelpCenterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_help_center2)

        setContent {
            HelpCenterScreen { finish() }
        }
    }
}

@Composable
fun HelpCenterScreen(
    onBackClick: () -> Unit
) {
    var text by rememberSaveable { mutableStateOf("")}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(18.dp)
            .padding(top = 48.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                tint = Color(0xFF1B1C1F).copy(alpha = 0.8f),
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onBackClick() }
            )

            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Help Center",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = text,
            onValueChange = {text = it},
            placeholder = {
                Text(
                    "Search Topic",
                    fontSize = 16.sp,
                    color = Color(0xFFA9B0C5),
                )
            },
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color(0xFFE0E5ED),
                focusedContainerColor = Color(0xFFE0E5ED),
                disabledContainerColor = Color(0xFFE0E5ED),
                cursorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(56.dp)
                .background(color = Color(0xFFE0E5ED), shape = RoundedCornerShape(8.dp))
                .border(0.5.dp, color = Color(0xFFA9B0C5), RoundedCornerShape(8.dp))
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OptionList(R.drawable.platforms, "Platforms are used")
                OptionList(R.drawable.question, "Usage question")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OptionList(R.drawable.application, "Application usage")
                OptionList(R.drawable.update_time, "Update Time App")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OptionList(R.drawable.cross, "Cross Platform App")
                OptionList(R.drawable.reminder, "Update reminder")
            }
        }
    }
}

@Composable
fun OptionList(
    @DrawableRes iconResId: Int,
    text: String
) {
    val painter = painterResource(id = iconResId)

    Box(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .height(145.dp)
            .width(170.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color = Color(0xFFF6F7F9)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = text,
                fontSize = 14.sp,
                color = Color(0xFF767E8C)
            )
        }
    }
}