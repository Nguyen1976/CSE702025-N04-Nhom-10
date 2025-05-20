package com.example.taskify.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun TaskInputPanel(
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
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onDismiss() }
                .background(Color.Black.copy(alpha = 0.4f))
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .imePadding()
                .absolutePadding(16.dp)
        ) {
            Column {
                TextField(
                    value = title,
                    onValueChange = onTitleChange,
                    placeholder = { Text("eg: Meeting with client") },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    placeholder = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    var showTypeDialog by remember { mutableStateOf(false) }

                    IconButton(onClick = { showTypeDialog = true }) {
                        Icon(Icons.Default.AttachFile, contentDescription = null)
                    }

                    IconButton(onClick = {
                        val now = LocalDate.now()
                        val year = taskDate?.year ?: now.year
                        val month = taskDate?.monthValue?.minus(1) ?: now.monthValue - 1
                        val day = taskDate?.dayOfMonth ?: now.dayOfMonth

                        android.app.DatePickerDialog(context, { _, y, m, d ->
                            onTaskDateChange(LocalDate.of(y, m + 1, d))
                        }, year, month, day).show()
                    }) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }

                    IconButton(onClick = {
                        val nowTime = LocalTime.now()
                        val hour = taskTime?.hour ?: nowTime.hour
                        val minute = taskTime?.minute ?: nowTime.minute

                        android.app.TimePickerDialog(context, { _, h, m ->
                            onTaskTimeChange(LocalTime.of(h, m))
                        }, hour, minute, true).show()
                    }) {
                        Icon(Icons.Default.AccessTime, contentDescription = null)
                    }

                    IconButton(onClick = { /* Add flag action if needed */ }) {
                        Icon(Icons.Default.Flag, contentDescription = null)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = onSend,
                        enabled = title.isNotBlank() && taskDate != null && taskTime != null
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = null,
                            tint = if (title.isNotBlank() && taskDate != null && taskTime != null) Color.Green else Color.Gray
                        )
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
                                        modifier = Modifier.clickable { /* TODO: add new type */ }
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
                }

                Spacer(Modifier.height(12.dp))

                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    listOf("😊", "😅", "😇", "🥰", "🙌", "👋", "😰", "✌️").forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { onDescriptionChange(description + emoji) }
                        )
                    }
                }
            }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }
}