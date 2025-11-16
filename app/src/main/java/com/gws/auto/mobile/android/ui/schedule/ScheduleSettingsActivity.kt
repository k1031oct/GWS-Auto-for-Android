package com.gws.auto.mobile.android.ui.schedule

import android.app.Activity
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme
import com.gws.auto.mobile.android.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@AndroidEntryPoint
class ScheduleSettingsActivity : ComponentActivity() {
    private val viewModel: ScheduleSettingsViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val theme by themeViewModel.theme.collectAsState()
            val highlightColor by themeViewModel.highlightColor.collectAsState()

            GWSAutoForAndroidTheme(
                theme = theme,
                highlightColor = highlightColor
            ) {
                ScheduleSettingsScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleSettingsScreen(viewModel: ScheduleSettingsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val scheduleTypes = listOf("時間毎", "日毎", "週毎", "月毎", "年毎")
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("予約実行の設定") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = uiState.scheduleType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("繰り返し") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    scheduleTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                viewModel.onScheduleTypeChange(type)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (uiState.scheduleType) {
                    "時間毎" -> HourlySettings(
                        interval = uiState.hourlyInterval,
                        onIntervalChange = { viewModel.setHourlyInterval(it) }
                    )
                    "日毎" -> DailySettings(
                        time = uiState.dailyTime,
                        onTimeChange = { viewModel.setDailyTime(it) }
                    )
                    "週毎" -> WeeklySettings(
                        selectedDays = uiState.weeklyDays,
                        onDayToggle = { viewModel.toggleWeeklyDay(it) },
                        time = uiState.weeklyTime,
                        onTimeChange = { viewModel.setWeeklyTime(it) },
                        firstDayOfWeek = uiState.firstDayOfWeek
                    )
                    "月毎" -> MonthlySettings(
                        selectedDays = uiState.monthlyDays,
                        onDayToggle = { viewModel.toggleMonthlyDay(it) },
                        time = uiState.monthlyTime,
                        onTimeChange = { viewModel.setMonthlyTime(it) }
                    )
                    "年毎" -> YearlySettings(
                        selectedMonth = uiState.yearlyMonth,
                        onMonthChange = { viewModel.setYearlyMonth(it) },
                        selectedDay = uiState.yearlyDayOfMonth,
                        onDayChange = { viewModel.setYearlyDayOfMonth(it) },
                        time = uiState.yearlyTime,
                        onTimeChange = { viewModel.setYearlyTime(it) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = { (context as? Activity)?.finish() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("キャンセル")
                }
                Button(
                    onClick = {
                        viewModel.saveSchedule()
                        (context as? Activity)?.finish()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("保存")
                }
            }
        }
    }
}

@Composable
fun HourlySettings(interval: Int, onIntervalChange: (Int) -> Unit) {
    Column {
        Text("実行間隔: ${interval}時間毎")
        Slider(
            value = interval.toFloat(),
            onValueChange = { onIntervalChange(it.toInt()) },
            valueRange = 1f..12f,
            steps = 10
        )
    }
}

@Composable
fun DailySettings(time: LocalTime, onTimeChange: (LocalTime) -> Unit) {
    val context = LocalContext.current
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    fun showTimePicker() {
        TimePickerDialog(
            context,
            { _, hour, minute -> onTimeChange(LocalTime.of(hour, minute)) },
            time.hour,
            time.minute,
            true
        ).show()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = time.format(timeFormatter),
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.clickable { showTimePicker() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklySettings(
    selectedDays: Set<String>,
    onDayToggle: (String) -> Unit,
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
    firstDayOfWeek: String
) {
    val daysOfWeek = remember(firstDayOfWeek) {
        val week = listOf("日", "月", "火", "水", "木", "金", "土")
        if (firstDayOfWeek.equals("Monday", ignoreCase = true)) {
            week.subList(1, week.size) + week[0]
        } else {
            week
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("実行する曜日")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            daysOfWeek.forEach { day ->
                FilterChip(
                    selected = day in selectedDays,
                    onClick = { onDayToggle(day) },
                    label = { Text(day) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        DailySettings(time, onTimeChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlySettings(
    selectedDays: Set<Int>,
    onDayToggle: (Int) -> Unit,
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit
) {
    val daysInMonth = (1..31).toList()

    Column(modifier = Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("実行する日")
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(daysInMonth.size) { index ->
                val day = daysInMonth[index]
                FilterChip(
                    selected = day in selectedDays,
                    onClick = { onDayToggle(day) },
                    label = { Text(day.toString()) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        DailySettings(time, onTimeChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearlySettings(
    selectedMonth: Int,
    onMonthChange: (Int) -> Unit,
    selectedDay: Int,
    onDayChange: (Int) -> Unit,
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit
) {
    val months = Month.entries.map { it.getDisplayName(TextStyle.FULL, Locale.getDefault()) }
    var monthExpanded by remember { mutableStateOf(false) }
    val daysInMonth = YearMonth.of(2024, selectedMonth).lengthOfMonth()

    Column(modifier = Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
        ExposedDropdownMenuBox(
            expanded = monthExpanded,
            onExpandedChange = { monthExpanded = !monthExpanded }
        ) {
            TextField(
                value = months[selectedMonth - 1],
                onValueChange = {},
                readOnly = true,
                label = { Text("月") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = monthExpanded,
                onDismissRequest = { monthExpanded = false }
            ) {
                months.forEachIndexed { index, monthName ->
                    DropdownMenuItem(
                        text = { Text(monthName) },
                        onClick = {
                            onMonthChange(index + 1)
                            monthExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("実行する日")
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(daysInMonth) { dayOfMonth ->
                val day = dayOfMonth + 1
                FilterChip(
                    selected = day == selectedDay,
                    onClick = { onDayChange(day) },
                    label = { Text(day.toString()) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        DailySettings(time, onTimeChange)
    }
}
