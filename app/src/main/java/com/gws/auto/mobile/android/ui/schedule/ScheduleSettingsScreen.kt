package com.gws.auto.mobile.android.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleSettingsScreen(viewModel: ScheduleSettingsViewModel, onSave: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val workflows by viewModel.workflows.collectAsState()
    var isScheduleTypeMenuExpanded by remember { mutableStateOf(false) }
    var isWorkflowMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("スケジュール設定") })
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

                // Day of Week Checkboxes (for Weekly)
                if (uiState.scheduleType == "週毎") {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("曜日を選択")
                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
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
                }
                Spacer(modifier = Modifier.height(16.dp))
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
