package com.gws.auto.mobile.android.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleSettingsScreen(viewModel: ScheduleSettingsViewModel, onSave: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val workflows by viewModel.workflows.collectAsState()
    var isScheduleTypeMenuExpanded by remember { mutableStateOf(false) }
    var isWorkflowMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (viewModel.uiState.value.selectedWorkflowId.isNotBlank()) "スケジュール編集" else "スケジュール新規作成") })
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it).padding(16.dp)) {
            item {
                // Workflow Selector
                ExposedDropdownMenuBox(
                    expanded = isWorkflowMenuExpanded,
                    onExpandedChange = { isWorkflowMenuExpanded = !isWorkflowMenuExpanded },
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        readOnly = true,
                        value = workflows.find { it.id == uiState.selectedWorkflowId }?.name ?: "",
                        onValueChange = {},
                        label = { Text("ワークフロー") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isWorkflowMenuExpanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = isWorkflowMenuExpanded,
                        onDismissRequest = { isWorkflowMenuExpanded = false },
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

                // Schedule Type Selector
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

                // --- Conditional UI based on Schedule Type ---
                when (uiState.scheduleType) {
                    "時間毎" -> {
                        OutlinedTextField(
                            value = uiState.hourlyInterval.toString(),
                            onValueChange = { value -> viewModel.setHourlyInterval(value.toIntOrNull() ?: 1) },
                            label = { Text("間隔 (時間)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    "日毎" -> {
                         OutlinedTextField(
                            value = uiState.dailyTime.toString(),
                            onValueChange = { /* Implement Time Picker */ },
                            label = { Text("時刻") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    "週毎" -> {
                        Text("曜日を選択")
                        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                            listOf("月", "火", "水", "木", "金", "土", "日").forEach { day ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { viewModel.toggleWeeklyDay(day) }) {
                                    Text(day)
                                    Checkbox(
                                        checked = uiState.weeklyDays.contains(day),
                                        onCheckedChange = { viewModel.toggleWeeklyDay(day) }
                                    )
                                }
                            }
                        }
                        OutlinedTextField(
                            value = uiState.weeklyTime.toString(),
                            onValueChange = { /* Implement Time Picker */ },
                            label = { Text("時刻") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    "月毎" -> {
                        // Basic input for monthly days, can be improved with a proper picker
                        OutlinedTextField(
                            value = uiState.monthlyDays.joinToString(","),
                            onValueChange = { value ->
                                val days = value.split(',').mapNotNull { it.trim().toIntOrNull() }.toSet()
                                // A more robust update mechanism would be needed here
                            },
                            label = { Text("日付 (カンマ区切り)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                         OutlinedTextField(
                            value = uiState.monthlyTime.toString(),
                            onValueChange = { /* Implement Time Picker */ },
                            label = { Text("時刻") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    "年毎" -> {
                        OutlinedTextField(
                            value = "${uiState.yearlyMonth}月 ${uiState.yearlyDayOfMonth}日",
                            onValueChange = { },
                            label = { Text("日付") },
                            modifier = Modifier.fillMaxWidth()
                        )
                         OutlinedTextField(
                            value = uiState.yearlyTime.toString(),
                            onValueChange = { /* Implement Time Picker */ },
                            label = { Text("時刻") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.saveSchedule()
                        onSave()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("保存")
                }
            }
        }
    }
}
