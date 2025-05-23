package com.example.taskify.presentation.filter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskify.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
fun FilterScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var snackbarJob: Job? by remember { mutableStateOf(null) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(8.dp)
            ) { data ->
                Snackbar(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    containerColor = Color(0xFF1E9584),
                    contentColor = Color.White,
                    action = {
                        TextButton(
                            onClick = { data.dismiss() }
                        ) {
                            Text(
                                text = data.visuals.actionLabel ?: "OK",
                                color = Color.Yellow
                            )
                        }
                    }
                ) {
                    Text(
                        text = data.visuals.message,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFFF5F5F5))
                .padding(paddingValues)
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Filter & Labels",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Filter your task",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable {
                            snackbarJob?.cancel()
                            snackbarJob = scope.launch {
                                delay(500L)
                                snackbarHostState.showSnackbar("Add filter clicked")
                            }
                        }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            val filter = getPainterFromAny(R.drawable.filter)
            val collision = getPainterFromAny(R.drawable.collision)
            val okHand = getPainterFromAny(R.drawable.ok_hand)
            val settingLabel = getPainterFromAny(R.drawable.settingfilter)
            val taskSquare = getPainterFromAny(R.drawable.task_square)

            FilterItem(painter = filter, text = "Assigned to me")
            Spacer(modifier = Modifier.height(12.dp))
            FilterItem(painter = collision, text = "Priority 1", subText = "1")
            Spacer(modifier = Modifier.height(12.dp))
            FilterItem(painter = okHand, text = "Priority 3", subText = "1")
            Spacer(modifier = Modifier.height(12.dp))
            FilterItem(painter = settingLabel, text = "Manage Filter")

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Labels",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable {
                            snackbarJob?.cancel()
                            snackbarJob = scope.launch {
                                delay(500L)
                                snackbarHostState.showSnackbar(
                                    message = "Growing features",
                                    actionLabel = "OK",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            FilterItem(painter = taskSquare, text = "Masana label")
            Spacer(modifier = Modifier.height(12.dp))
            FilterItem(painter = settingLabel, text = "Manage labels")
        }
    }
}

@Composable
fun FilterItem(
    painter: Painter,
    text: String,
    subText: String = "",
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            fontSize = 16.sp,
            color = Color(0xFF767E8C)
        )

        Spacer(modifier = Modifier.weight(1f))

        if (subText != "") {
            Text(
                subText,
                fontSize = 14.sp,
                color = Color(0xFF767E8C)
            )
        }
    }
}

@Composable
fun getPainterFromAny(resId: Int): Painter {
    return painterResource(id = resId)
}