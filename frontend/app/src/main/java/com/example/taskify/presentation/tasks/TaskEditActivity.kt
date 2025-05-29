package com.example.taskify.presentation.tasks

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.taskify.R
import com.example.taskify.ui.theme.TaskifyTheme
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

class TaskEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task_edit)

        val initialTitle = intent.getStringExtra("title") ?: ""
        val initialDescription = intent.getStringExtra("description") ?: ""
        val taskDateString = intent.getStringExtra("taskDate")
        val taskTimeString = intent.getStringExtra("taskTime")
        val initialType = intent.getStringExtra("type") ?: ""

        val initialDate = taskDateString?.let { LocalDate.parse(it) } ?: LocalDate.now()
        val initialTime = taskTimeString?.let { LocalTime.parse(it) } ?: LocalTime.now()

        setContent {
            var title by remember { mutableStateOf(initialTitle) }
            var description by remember { mutableStateOf(initialDescription) }
            var taskDate by remember { mutableStateOf(initialDate) }
            var taskTime by remember { mutableStateOf(initialTime) }
            var type by remember { mutableStateOf(initialType) }

            TaskEditScreen(
                title = title,
                onTitleChange = { title = it },
                description = description,
                onDescriptionChange = { description = it },
                taskDate = taskDate,
                selectedDate = taskDate.toString(),
                onTaskDateChange = { taskDate = it },
                taskTime = taskTime,
                selectedTime = taskTime,
                onTaskTimeChange = { taskTime = it },
                type = type
            )
        }

    }
}

@Composable
fun TaskEditScreen(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    taskDate: LocalDate,
    selectedDate: String,
    onTaskDateChange: (LocalDate?) -> Unit,
    taskTime: LocalTime,
    selectedTime: LocalTime,
    onTaskTimeChange: (LocalTime?) -> Unit,
    type: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        item { /** Top bar **/ }
        item {
            TaskTextField(
                name = "Title",
                value = title,
                onValueChange = onTitleChange
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            TaskTextField(
                name = "Description",
                value = description,
                onValueChange = onDescriptionChange
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            DatePickerItem(
                label = "Task date",
                selectedDate = selectedDate,
                taskDate = taskDate,
                onTaskDateChange = onTaskDateChange
            )
        }
    }
}

@Composable
fun TaskTextField(
    name: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(color = Color.White, shape = RoundedCornerShape(4.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester),
                decorationBox = { innerTextField ->
                    innerTextField()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTaskEditScreen() {
    TaskEditScreen(
        title = "Học Compose",
        onTitleChange = {},
        description = "Làm UI bằng Jetpack Compose",
        onDescriptionChange = {},
        taskDate = LocalDate.of(2025, 5, 30),
        selectedDate = "30/05/2025",
        onTaskDateChange = {},
        taskTime = LocalTime.of(9, 0),
        selectedTime = LocalTime.of(9, 0),
        onTaskTimeChange = {},
        type = "Công việc"
    )
}

@Composable
fun DatePickerItem(
    label: String,
    selectedDate: String,
    taskDate: LocalDate?,
    onTaskDateChange: (LocalDate?) -> Unit,
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier.width(180.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
        )

        Row(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
                .padding(8.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    val now = LocalDate.now()
                    val year = taskDate?.year ?: now.year
                    val month = taskDate?.monthValue?.minus(1) ?: now.monthValue - 1
                    val day = taskDate?.dayOfMonth ?: now.dayOfMonth

                    val datePickerDialog = DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            onTaskDateChange(LocalDate.of(y, m + 1, d))
                        },
                        year,
                        month,
                        day
                    )

                    val dateFormat = java.text.SimpleDateFormat("EEE, MMM d", Locale.ENGLISH)
                    val selectedDate = java.util.Calendar.getInstance().apply {
                        set(year, month, day)
                    }.time
                    datePickerDialog.setTitle(dateFormat.format(selectedDate))

                    datePickerDialog.datePicker.init(year, month, day) { _, y, m, d ->
                        val updatedDate = java.util.Calendar.getInstance().apply {
                            set(y, m, d)
                        }.time
                        datePickerDialog.setTitle(dateFormat.format(updatedDate))
                    }

                    datePickerDialog.show()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.DateRange,
                contentDescription = null,
//                tint = colorResource(R.color.blue),
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(24.dp)
            )

            Text(
                text = selectedDate,
                modifier = Modifier.padding(horizontal = 12.dp),
//                color = colorResource(R.color.blue),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}