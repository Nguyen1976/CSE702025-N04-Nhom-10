package com.example.taskify.presentation.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun TaskInputPanel2(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    taskDate: LocalDate?,
    onTaskDateChange: (LocalDate?) -> Unit,
    taskTime: LocalTime?,
    onTaskTimeChange: (LocalTime?) -> Unit,
    selectedType: String?,
    onTypeSelected: (String) -> Unit,
    isSuccess: Boolean,
    onDismiss: () -> Unit,
    onSend: () -> Unit
) {
    val emojis = remember { listOf("😊", "😎", "😅", "🥰", "🙌", "😴", "✌️") }
    val focusRequester = remember { FocusRequester() }
    var showTypeDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    BackHandler(enabled = true) {
        coroutineScope.launch {
            focusManager.clearFocus()
            keyboardController?.hide()
            onDismiss()
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    if (isSuccess) {
        LaunchedEffect(Unit) {
            onDismiss()
        }
    }

    if (showTypeDialog) {
        AlertDialog(
            onDismissRequest = { showTypeDialog = false },
            title = { Text("Type") },
            text = {
                Column {
                    val types = listOf("Meeting", "Entertainment", "Study", "Work")
                    types.forEach { item ->
                        Text(
                            text = item,
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onTypeSelected(item)
                                    showTypeDialog = false
                                }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { /* TODO: Add new type */ }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Gray)
                        Text(" Add type...", color = Color.Gray)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTypeDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(onClick = onDismiss)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .wrapContentHeight() // Chỉ chiếm không gian cần thiết
                .background(Color.White)
                .padding(horizontal = 16.dp)
                .imePadding() // Chỉ sử dụng imePadding để điều chỉnh khi bàn phím xuất hiện
        ) {
            TextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = { Text("eg : Meeting with client") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = description,
                onValueChange = onDescriptionChange,
                placeholder = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    val now = LocalDate.now()
                    val year = taskDate?.year ?: now.year
                    val month = taskDate?.monthValue?.minus(1) ?: now.monthValue - 1
                    val day = taskDate?.dayOfMonth ?: now.dayOfMonth

                    android.app.DatePickerDialog(context, { _, y, m, d ->
                        onTaskDateChange(LocalDate.of(y, m + 1, d))
                    }, year, month, day).show()
                }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                }

                IconButton(onClick = {
                    val nowTime = LocalTime.now()
                    val hour = taskTime?.hour ?: nowTime.hour
                    val minute = taskTime?.minute ?: nowTime.minute

                    android.app.TimePickerDialog(context, { _, h, m ->
                        onTaskTimeChange(LocalTime.of(h, m))
                    }, hour, minute, true).show()
                }) {
                    Icon(Icons.Default.AccessTime, contentDescription = "Pick Time")
                }

                IconButton(onClick = {
                    showTypeDialog = true
                }) {
                    Icon(Icons.Default.Label, contentDescription = "Pick Type")
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = onSend) {
                    Icon(Icons.Default.Send, contentDescription = "Send Task")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                emojis.forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                onDescriptionChange(description + emoji)
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp)) // Thêm khoảng cách nhỏ ở dưới để tránh sát mép
        }
    }
}