package com.example.taskify.presentation.tasks

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.icu.util.Calendar
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskify.R
import com.example.taskify.data.viewmodel.TaskViewModel
import com.example.taskify.domain.model.taskModel.SubtaskResponse
import com.example.taskify.domain.model.taskModel.TaskResponse
import com.example.taskify.domain.model.themeModel.ThemeOption
import com.example.taskify.presentation.main.toColor
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

@AndroidEntryPoint
class TaskEditActivity : AppCompatActivity() {

    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task_edit)

        val initialId = intent.getStringExtra("_id") ?: ""
        val initialUserId = intent.getStringExtra("userId") ?: ""
        val initialTitle = intent.getStringExtra("title") ?: ""
        val initialDescription = intent.getStringExtra("description") ?: ""
        val initialCreateAt = intent.getStringExtra("createAt") ?: ""
        val taskDateString = intent.getStringExtra("taskDate")
        val taskTimeString = intent.getStringExtra("taskTime")
        val initialType = intent.getStringExtra("type") ?: ""
        val initialIsSuccess = intent.getBooleanExtra("isSuccess", false)
        val themeName = intent.getStringExtra("theme")

        val initialDate = taskDateString?.let { LocalDate.parse(it) } ?: LocalDate.now()
        val initialTime = taskTimeString?.let { LocalTime.parse(it) } ?: LocalTime.now()
        val theme = ThemeOption.valueOf(themeName ?: ThemeOption.Teal.name)

        setContent {
            val updateTaskResult = taskViewModel.updateTaskResult.collectAsState()
            val context = LocalContext.current

            val _id by remember { mutableStateOf(initialId) }
            val userId by remember { mutableStateOf(initialUserId) }
            var title by remember { mutableStateOf(initialTitle) }
            var description by remember { mutableStateOf(initialDescription) }
            val createAt by remember { mutableStateOf(initialCreateAt) }
            var taskDate by remember { mutableStateOf(initialDate) }
            var taskTime by remember { mutableStateOf(initialTime) }
            var type by remember { mutableStateOf(initialType) }
            val subtask: List<SubtaskResponse> = intent.getParcelableArrayListExtra("subtask") ?: emptyList()
            val isSuccess by remember { mutableStateOf(initialIsSuccess) }

            LaunchedEffect(updateTaskResult.value) {
                updateTaskResult.value?.let { result ->
                    result.onSuccess {
                        Toast.makeText(context, "Task updated successfully", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }.onFailure {
                        Toast.makeText(context, "Failed to update task, please try again later!", Toast.LENGTH_SHORT).show()
                    }
                    taskViewModel.resetUpdateTaskResult()
                }
            }

            TaskEditScreen(
                title = title,
                onTitleChange = { title = it },
                description = description,
                onDescriptionChange = { description = it },
                taskDate = taskDate,
                selectedDate = taskDate.toString(),
                onTaskDateChange = { taskDate = it },
                taskTime = taskTime,
                selectedTime = taskTime.toString(),
                onTaskTimeChange = { taskTime = it },
                type = type,
                onTypeChange = { type = it },
                theme = theme,
                onBackClick = { finish() },
                onApplyChange = {
                    val updatedTask = TaskResponse(
                        _id = _id,
                        userId = userId,
                        title = title,
                        description = description,
                        createAt = createAt,
                        taskDate = taskDate.toString(),
                        taskTime = taskTime.toString(),
                        type = type,
                        isSuccess = isSuccess,
                        subtasks = subtask
                    )
                    taskViewModel.updateTask(updatedTask)
                }
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
    selectedTime: String,
    onTaskTimeChange: (LocalTime?) -> Unit,
    type: String,
    onTypeChange: (String) -> Unit,
    theme: ThemeOption,
    onBackClick: () -> Unit,
    onApplyChange: () -> Unit
) {
    Scaffold(
        topBar = { TopBar( onBackClick ) },
        bottomBar = {
            Button(
                onClick = { onApplyChange() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = theme.toColor()
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Apply Change",
                    fontSize = 16.sp
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFFF5F5F5))
                .padding(horizontal = 16.dp)
                .padding(paddingValues)
        ) {
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
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .padding(bottom = 8.dp)
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(color = Color(0xFFDDDDDD))
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DatePickerItem(
                        label = "Task date",
                        selectedDate = selectedDate,
                        taskDate = taskDate,
                        onTaskDateChange = onTaskDateChange,
                        theme = theme
                    )

                    TimePickerItem(
                        label = "Task time",
                        selectedTime = selectedTime,
                        taskTime = taskTime,
                        onTaskTimeChange = onTaskTimeChange,
                        theme = theme
                    )
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(color = Color(0xFFDDDDDD))
                )
            }

            item {
                TypeSelector(
                    selectedType = type,
                    onTypeChange = onTypeChange,
                    theme = theme
                )
            }
        }
    }
}

@Composable
fun TopBar(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBackClick() }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                "Edit task",
                fontSize = 20.sp,
                color = Color.Black
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .height(1.dp)
                .fillMaxWidth()
                .background(color = Color(0xFFDDDDDD))
        )
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
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
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
        onBackClick = {},
        title = "Học Compose",
        onTitleChange = {},
        description = "Làm UI bằng Jetpack Compose",
        onDescriptionChange = {},
        taskDate = LocalDate.of(2025, 5, 30),
        selectedDate = "30-05-2025",
        onTaskDateChange = {},
        taskTime = LocalTime.of(9, 0),
        selectedTime = "19:00",
        onTaskTimeChange = {},
        type = "Meeting",
        onTypeChange = {},
        onApplyChange = {},
        theme = ThemeOption.Teal
    )
}

@Composable
fun DatePickerItem(
    label: String,
    selectedDate: String,
    taskDate: LocalDate?,
    onTaskDateChange: (LocalDate?) -> Unit,
    theme: ThemeOption
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val color = theme.toColor()

    val configuration = Configuration(context.resources.configuration).apply {
        setLocale(Locale.ENGLISH)
    }
    val englishContext = ContextThemeWrapper(context, context.theme)
    englishContext.applyOverrideConfiguration(configuration)

    Column(
        modifier = Modifier.width(180.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            fontSize = 17.sp,
        )

        Row(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
                .border(1.dp, theme.toColor(), RoundedCornerShape(8.dp))
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

                    val englishSymbols = DateFormatSymbols(Locale.ENGLISH)

                    val dateFormat = java.text.SimpleDateFormat("EEE, MMM d", Locale.ENGLISH).apply {
                        dateFormatSymbols = englishSymbols
                    }
                    val selectedDate = java.util.Calendar
                        .getInstance()
                        .apply {
                            set(year, month, day)
                        }.time
                    datePickerDialog.setTitle(dateFormat.format(selectedDate))

                    datePickerDialog.datePicker.init(year, month, day) { _, y, m, d ->
                        val updatedDate = java.util.Calendar
                            .getInstance()
                            .apply {
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
                tint = color,
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

@Composable
fun TimePickerItem(
    label: String,
    selectedTime: String,
    taskTime: LocalTime?,
    onTaskTimeChange: (LocalTime?) -> Unit,
    theme: ThemeOption
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val color = theme.toColor()

    Column(
        modifier = Modifier.width(180.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            fontSize = 17.sp,
            modifier = Modifier.padding(start = 10.dp)
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
                .border(1.dp, theme.toColor(), RoundedCornerShape(8.dp))
                .clickable {
                    val hour = taskTime?.hour ?: calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = taskTime?.minute ?: calendar.get(Calendar.MINUTE)

                    TimePickerDialog(
                        context,
                        { _, h, m ->
                            val newTime = LocalTime.of(h, m)
                            onTaskTimeChange(newTime)
                        },
                        hour,
                        minute,
                        true
                    ).show()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AccessTime,
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(24.dp)
            )

            Text(
                text = selectedTime,
                modifier = Modifier.padding(horizontal = 12.dp),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun TypeSelector(
    selectedType: String,
    onTypeChange: (String) -> Unit,
    theme: ThemeOption
) {
    val color = theme.toColor()
    val taskTypes = listOf("Meeting", "Entertainment", "Study", "Work")

    Column {
        Text(
            text = "Type",
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        taskTypes.forEach { type ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTypeChange(type) }
            ) {
                RadioButton(
                    selected = selectedType == type,
                    onClick = { onTypeChange(type) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = color
                    ),
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = type,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.6f)
            )

            Text(
                "Add type",
                fontSize = 16.sp,
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}