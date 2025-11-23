package com.gws.auto.mobile.android.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.Switch
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScheduleSettingsScreen(viewModel: ScheduleSettingsViewModel, onSave: () -> Unit, onCancel: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val workflows by viewModel.workflows.collectAsState()
    var isScheduleTypeMenuExpanded by remember { mutableStateOf(false) }
    var isWorkflowMenuExpanded by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // --- Dialogs ---
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(initialHour = uiState.dailyTime.hour, initialMinute = uiState.dailyTime.minute)
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onConfirm = {
                val newTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                when (uiState.scheduleType) {
                    "日毎" -> viewModel.setDailyTime(newTime)
                    "週毎" -> viewModel.setWeeklyTime(newTime)
                    "月毎" -> viewModel.setMonthlyTime(newTime)
                    "年毎" -> viewModel.setYearlyTime(newTime)
                }
                showTimePicker = false
            }
        ) { 
            TimePicker(state = timePickerState)
        }
    }
    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = Instant.now().toEpochMilli())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        viewModel.setYearlyMonth(date.monthValue)
                        viewModel.setYearlyDayOfMonth(date.dayOfMonth)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("キャンセル") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // --- Screen Content ---
    // GWSAutoForAndroidTheme removed to allow outer theme (from Activity) to take effect
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(if (viewModel.uiState.value.selectedWorkflowId.isNotBlank()) "スケジュール編集" else "スケジュール新規作成") })
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                ExposedDropdownMenuBox(
                    expanded = isWorkflowMenuExpanded,
                    onExpandedChange = { isWorkflowMenuExpanded = !isWorkflowMenuExpanded },
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        readOnly = true,
                        value = workflows.find { wf -> wf.id == uiState.selectedWorkflowId }?.name ?: "",
                        onValueChange = {},
                        label = { Text("ワークフロー") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isWorkflowMenuExpanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = isWorkflowMenuExpanded,
                        onDismissRequest = { isWorkflowMenuExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        workflows.forEach { workflow ->
                            DropdownMenuItem(
                                text = { Text(workflow.name) },
                                onClick = {
                                    viewModel.onWorkflowSelected(workflow.id)
                                    isWorkflowMenuExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = isScheduleTypeMenuExpanded,
                    onExpandedChange = { isScheduleTypeMenuExpanded = !isScheduleTypeMenuExpanded },
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        readOnly = true,
                        value = uiState.scheduleType,
                        onValueChange = {},
                        label = { Text("繰り返し") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isScheduleTypeMenuExpanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = isScheduleTypeMenuExpanded,
                        onDismissRequest = { isScheduleTypeMenuExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        listOf("時間毎", "日毎", "週毎", "月毎", "年毎").forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    viewModel.onScheduleTypeChange(type)
                                    isScheduleTypeMenuExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Conditional UI ---
                when (uiState.scheduleType) {
                    "時間毎" -> {
                        Text("間隔: ${uiState.hourlyInterval} 時間")
                        Slider(
                            value = uiState.hourlyInterval.toFloat(),
                            onValueChange = { value -> viewModel.setHourlyInterval(value.toInt()) },
                            valueRange = 1f..24f,
                            steps = 23
                        )
                    }
                    "日毎" -> {
                        OutlinedButton(onClick = { showTimePicker = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(uiState.dailyTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Switch(
                                checked = uiState.skipHolidays,
                                onCheckedChange = { viewModel.toggleSkipHolidays() }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("祝日の場合は翌日に実行", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    "週毎" -> {
                        Text("曜日を選択")
                        Spacer(modifier = Modifier.height(8.dp))
                        // Single row with all weekdays
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Monday
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = uiState.weeklyDays.contains("月"),
                                    onCheckedChange = { viewModel.toggleWeeklyDay("月") }
                                )
                                Text("月", style = MaterialTheme.typography.labelSmall)
                            }
                            // Tuesday
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = uiState.weeklyDays.contains("火"),
                                    onCheckedChange = { viewModel.toggleWeeklyDay("火") }
                                )
                                Text("火", style = MaterialTheme.typography.labelSmall)
                            }
                            // Wednesday
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = uiState.weeklyDays.contains("水"),
                                    onCheckedChange = { viewModel.toggleWeeklyDay("水") }
                                )
                                Text("水", style = MaterialTheme.typography.labelSmall)
                            }
                            // Thursday
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = uiState.weeklyDays.contains("木"),
                                    onCheckedChange = { viewModel.toggleWeeklyDay("木") }
                                )
                                Text("木", style = MaterialTheme.typography.labelSmall)
                            }
                            // Friday
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = uiState.weeklyDays.contains("金"),
                                    onCheckedChange = { viewModel.toggleWeeklyDay("金") }
                                )
                                Text("金", style = MaterialTheme.typography.labelSmall)
                            }
                            // Saturday
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = uiState.weeklyDays.contains("土"),
                                    onCheckedChange = { viewModel.toggleWeeklyDay("土") }
                                )
                                Text("土", style = MaterialTheme.typography.labelSmall)
                            }
                            // Sunday
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = uiState.weeklyDays.contains("日"),
                                    onCheckedChange = { viewModel.toggleWeeklyDay("日") }
                                )
                                Text("日", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = { showTimePicker = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(uiState.weeklyTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Switch(
                                checked = uiState.skipHolidays,
                                onCheckedChange = { viewModel.toggleSkipHolidays() }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("祝日の場合は翌日に実行", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    "月毎" -> {
                        var isMonthlyDayMenuExpanded by remember { mutableStateOf(false) }
                        Text("日付を選択 (複数選択可)")
                        ExposedDropdownMenuBox(
                            expanded = isMonthlyDayMenuExpanded,
                            onExpandedChange = { isMonthlyDayMenuExpanded = !isMonthlyDayMenuExpanded },
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                readOnly = true,
                                value = uiState.monthlyDays.sorted().joinToString(", "),
                                onValueChange = {},
                                label = { Text("日付") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMonthlyDayMenuExpanded) },
                            )
                            ExposedDropdownMenu(
                                expanded = isMonthlyDayMenuExpanded,
                                onDismissRequest = { isMonthlyDayMenuExpanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {

                                (1..31).forEach { day ->
                                    DropdownMenuItem(
                                        text = { Text(day.toString()) },
                                        onClick = { viewModel.toggleMonthlyDay(day) },
                                        trailingIcon = {
                                            if (uiState.monthlyDays.contains(day)) {
                                                Icon(Icons.Default.Check, contentDescription = "Selected")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = { showTimePicker = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(uiState.monthlyTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Switch(
                                checked = uiState.skipHolidays,
                                onCheckedChange = { viewModel.toggleSkipHolidays() }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("祝日の場合は翌日に実行", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    "年毎" -> {
                        OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("${uiState.yearlyMonth}月 ${uiState.yearlyDayOfMonth}日")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = { showTimePicker = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(uiState.yearlyTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onCancel) {
                        Text("キャンセル")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.saveSchedule(); onSave() },
                        enabled = uiState.selectedWorkflowId.isNotBlank()
                    ) {
                        Text("保存")
                    }
                }
            }
        }

    // } removed
}

@Composable
fun TimePickerDialog(onDismissRequest: () -> Unit, onConfirm: () -> Unit, content: @Composable () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismissRequest) { Text("キャンセル") }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onConfirm) { Text("OK") }
                }
            }
        }
    }
}
