package com.example.taskify.presentation.main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContextWrapper
import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.taskify.R
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

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
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSend: () -> Unit
) {
    val emojis = remember { listOf("\uD83D\uDE00", "\uD83E\uDD11", "\uD83D\uDE07", "\uD83E\uDD70", "\uD83D\uDE4C", "\uD83D\uDC4B", "\uD83D\uDE30", "✌\uFE0F") }
    val focusRequester = remember { FocusRequester() }
    var showTypeDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    val interactionSource = remember { MutableInteractionSource() }

    val englishContext = remember {
        object : ContextWrapper(context) {
            override fun getResources(): android.content.res.Resources {
                val res = super.getResources()
                val config = Configuration(res.configuration).apply {
                    setLocale(Locale.ENGLISH)
                }
                return createConfigurationContext(config).resources
            }
        }
    }

    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH)
    }

    fun formatTaskDate(date: LocalDate?): String {
        return date?.format(dateFormatter) ?: ""
    }

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

    LaunchedEffect(isLoading) {
        if (isLoading) {
            focusManager.clearFocus()
            keyboardController?.hide()
        }
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
                    val icons = listOf(Icons.Default.Groups, Icons.Default.SportsEsports, Icons.Default.AutoStories, Icons.Default.Work)
                    val types = listOf("Meeting", "Entertainment", "Study", "Work")
                    icons.zip(types).forEach { (icon, item) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onTypeSelected(item)
                                    showTypeDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = item,
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = item,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            Toast.makeText(context, "Growing features", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Gray, modifier = Modifier.size(24.dp))
                        Text(" Add type...", color = Color.Gray, fontSize = 16.sp)
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
                .background(Color.Black.copy(alpha = 0.8f))
                .pointerInput(Unit) {
                    detectTapGestures {
                        onDismiss()
                    }
                }
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White)
                .padding(horizontal = 16.dp)
                .imePadding()
                .pointerInput(Unit) {}
        ) {
            BasicTextField(
                value = title,
                onValueChange = onTitleChange,
                singleLine = true,
                textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .focusRequester(focusRequester),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(Color.White)
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (title.isEmpty()) {
                            Text(
                                text = "eg : Meeting with client",
                                color = Color(0xFFA9B0C5),
                                fontSize = 15.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )

            BasicTextField(
                value = description,
                onValueChange = onDescriptionChange,
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 15.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                maxLines = 3,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(40.dp)
                            .padding(horizontal = 12.dp)
                            .background(Color.White),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (description.isEmpty()) {
                            Text(
                                text = "Description",
                                color = Color(0xFFA9B0C5),
                                fontSize = 15.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_direct_inbox),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { showTypeDialog = true },
                    colorFilter = if (selectedType != null)
                        ColorFilter.tint(Color(0xFF24A19C))
                    else
                        ColorFilter.tint(Color(0xFFA0AAB8))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Image(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            val now = LocalDate.now()
                            val year = taskDate?.year ?: now.year
                            val month = taskDate?.monthValue?.minus(1) ?: now.monthValue - 1
                            val day = taskDate?.dayOfMonth ?: now.dayOfMonth

                            val datePickerDialog = DatePickerDialog(
                                englishContext,
                                { _, y, m, d ->
                                    onTaskDateChange(LocalDate.of(y, m + 1, d))
                                },
                                year,
                                month,
                                day
                            )

                            val dateFormat = java.text.SimpleDateFormat("EEE, MMM d", Locale.ENGLISH)
                            val selectedDate = Calendar.getInstance().apply {
                                set(year, month, day)
                            }.time
                            datePickerDialog.setTitle(dateFormat.format(selectedDate))

                            datePickerDialog.datePicker.init(year, month, day) { _, y, m, d ->
                                val updatedDate = Calendar.getInstance().apply {
                                    set(y, m, d)
                                }.time
                                datePickerDialog.setTitle(dateFormat.format(updatedDate))
                            }

                            datePickerDialog.show()
                        },
                    colorFilter = if (taskDate != null)
                        ColorFilter.tint(Color(0xFF24A19C))
                    else
                        ColorFilter.tint(Color(0xFFA0AAB8))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Image(
                    painter = painterResource(R.drawable.ic_clock),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            val nowTime = LocalTime.now()
                            val hour = taskTime?.hour ?: nowTime.hour
                            val minute = taskTime?.minute ?: nowTime.minute

                            TimePickerDialog(
                                englishContext,
                                { _, h, m ->
                                    onTaskTimeChange(LocalTime.of(h, m))
                                },
                                hour,
                                minute,
                                true
                            ).show()
                        },
                    colorFilter = if (taskTime != null)
                        ColorFilter.tint(Color(0xFF24A19C))
                    else
                        ColorFilter.tint(Color(0xFFA0AAB8))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Image(
                    painter = painterResource(R.drawable.ic_flag),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { }
                )

                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            enabled = !isLoading
                        ) { onSend() }
                )
            }

            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = Color(0xFFEEEEEE))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .zIndex(2f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp,
                    color = Color.White
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun TaskInputPanel2Preview() {
//    // Dummy state for preview
//    var title by remember { mutableStateOf("Sample Task") }
//    var description by remember { mutableStateOf("This is a description.") }
//    var taskDate by remember { mutableStateOf(LocalDate.now()) }
//    var taskTime by remember { mutableStateOf(LocalTime.of(14, 30)) }
//    var selectedType by remember { mutableStateOf("Work") }
//    var isSuccess by remember { mutableStateOf(false) }
//
//    // UI preview
//    TaskInputPanel(
//        title = title,
//        onTitleChange = { title = it },
//        description = description,
//        onDescriptionChange = { description = it },
//        taskDate = taskDate,
//        onTaskDateChange = { taskDate = it },
//        taskTime = taskTime,
//        onTaskTimeChange = { taskTime = it },
//        selectedType = selectedType,
//        onTypeSelected = { selectedType = it },
//        isSuccess = isSuccess,
//        onDismiss = { /* no-op */ },
//        onSend = { /* no-op */ },
//        isLoading = false
//    )
//}