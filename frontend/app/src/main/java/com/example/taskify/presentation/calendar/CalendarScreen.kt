package com.example.taskify.presentation.calendar

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF5F5F5))
            .padding(16.dp)
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Upcoming",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(32.dp))

        CalendarSection()
    }
}

@Composable
fun CalendarSection() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }

    // tạo listState điều khiển scroll
    val listState = rememberLazyListState()
    val daysInMonth = selectedMonth.lengthOfMonth()
    val startDate = selectedMonth.atDay(1)
    val dates = (0 until daysInMonth).map { startDate.plusDays(it.toLong()) }

    // scroll khi selectedDate or selectedMonth thay đổi
    LaunchedEffect(selectedDate, selectedMonth) {
        // ngày đc chọn
        val index = dates.indexOfFirst { it == selectedDate }.coerceAtLeast(0)
        listState.animateScrollToItem((index - 2).coerceAtLeast(0))
    }

    Column {
        MonthSelector(
            selectedDate = selectedDate,
            onMonthSelected = {
                selectedMonth = it
                selectedDate = it.atDay(1)
            },
            onTodayClick = {
                selectedDate = LocalDate.now()
                selectedMonth = YearMonth.now()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ScrollableCalendar(
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it },
            currentMonth = selectedMonth,
            listState = listState
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(color = Color(0xFFEEEEEE))
        )

        Spacer(modifier = Modifier.height(12.dp))

        DateLabel(selectedDate)

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
                .height(1.dp)
                .background(color = Color(0xFFEEEEEE))
        )
    }
}

@Composable
fun MonthSelector(
    selectedDate: LocalDate,
    onMonthSelected: (YearMonth) -> Unit,
    onTodayClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
    val currentYear = java.time.Year.now().value
    val months = (1..12).map { month ->
        YearMonth.of(currentYear, month)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedDate.format(formatter),
                style = MaterialTheme.typography.titleMedium
            )
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier
                    .clickable { expanded = true }
            )
        }

        Text(
            "Today",
            color = Color(0xFF24A19C),
            fontSize = 16.sp,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onTodayClick() }
        )
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        months.forEach { month ->
            DropdownMenuItem(
                text = { Text(month.format(formatter)) },
                onClick = {
                    expanded = false
                    onMonthSelected(month)
                }
            )
        }
    }
}

@Composable
fun ScrollableCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    currentMonth: YearMonth,
    listState: LazyListState
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val startDate = currentMonth.atDay(1)
    val dates = (0 until daysInMonth).map { startDate.plusDays(it.toLong()) }

    LazyRow(state = listState) {
        items(dates) { date ->
            val isSelected = date == selectedDate
            Column(
                modifier = Modifier
                    .width(60.dp)
                    .padding(horizontal = 4.dp)
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Color(0xFF24A19C) else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onDateSelected(date) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.dayOfWeek.name.take(1),
                    color = if (isSelected) Color.White else Color.Gray,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = date.dayOfMonth.toString(),
                    color = if (isSelected) Color.White else Color.Black,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun DateLabel(selectedDate: LocalDate) {
    val today = LocalDate.now()

    val label = if (selectedDate == today) {
        "Today. ${selectedDate.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}"
    } else {
        "${selectedDate.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}. ${selectedDate.format(
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))}"
    }

    Text(
        text = label,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 8.dp, start = 8.dp),
        fontSize = 16.sp,
        color = Color(0xFF1B1C1F)
    )
}