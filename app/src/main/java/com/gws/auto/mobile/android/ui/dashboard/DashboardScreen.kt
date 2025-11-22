package com.gws.auto.mobile.android.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.gws.auto.mobile.android.R
import java.util.concurrent.TimeUnit

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onRefreshClicked: () -> Unit,
    onAnnouncementClicked: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            // No FAB in Dashboard, but maybe actions could be here?
            // The original layout had buttons at the top right.
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onRefreshClicked) {
                    Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.refresh))
                }
                IconButton(onClick = onAnnouncementClicked) {
                    Icon(Icons.Default.Campaign, contentDescription = "Announcements")
                }
            }

            Text(
                text = stringResource(R.string.dashboard_summary_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryCard(
                    title = stringResource(R.string.dashboard_total_runs),
                    value = uiState.totalCountMonth.toString(),
                    dayChange = uiState.totalCountDayChange,
                    monthChange = uiState.totalCountMonthChange,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = stringResource(R.string.dashboard_errors),
                    value = uiState.errorCountMonth.toString(),
                    dayChange = uiState.errorCountDayChange,
                    monthChange = uiState.errorCountMonthChange,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = stringResource(R.string.dashboard_total_time),
                    value = formatDuration(uiState.totalDurationMonth),
                    dayChange = uiState.totalDurationDayChange,
                    monthChange = uiState.totalDurationMonthChange,
                    modifier = Modifier.weight(1f)
                )
            }

            // Workflow Statistics
            Text(
                text = stringResource(R.string.dashboard_workflow_stats_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            PieChartComposable(
                totalCount = uiState.totalCountMonth,
                errorCount = uiState.errorCountMonth,
                centerText = stringResource(R.string.dashboard_workflow_error_rate),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            BarChartComposable(
                entries = uiState.workflowExecutionCounts.mapIndexed { index, it -> BarEntry(index.toFloat(), it.executionCount.toFloat()) },
                labels = uiState.workflowExecutionCounts.map { it.workflowName },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            // Module Statistics
            Text(
                text = stringResource(R.string.dashboard_module_stats_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            PieChartComposable(
                totalCount = uiState.moduleUsageCount,
                errorCount = uiState.moduleErrorCount,
                centerText = stringResource(R.string.dashboard_module_error_rate),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            BarChartComposable(
                entries = uiState.moduleStats.mapIndexed { index, it -> BarEntry(index.toFloat(), it.usageCount.toFloat()) },
                labels = uiState.moduleStats.map { it.moduleName },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            // Workflow Stats List
            WorkflowStatsList(uiState.workflowExecutionCounts)

            // Module Stats List
            ModuleStatsList(uiState.moduleStats)
        }
    }
}

@Composable
fun WorkflowStatsList(items: List<WorkflowExecutionCount>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.workflowName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = item.executionCount.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ModuleStatsList(items: List<ModuleStat>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.moduleName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Used: ${item.usageCount} times",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Errors: ${item.errorCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (item.errorCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    dayChange: Float,
    monthChange: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            ChangeText(change = dayChange, label = "vs Yesterday")
            ChangeText(change = monthChange, label = "vs Last Month")
        }
    }
}

@Composable
fun ChangeText(change: Float, label: String) {
    val sign = if (change >= 0) "+" else ""
    val color = if (change > 0) MaterialTheme.colorScheme.primary 
                else if (change < 0) MaterialTheme.colorScheme.error 
                else MaterialTheme.colorScheme.onSurfaceVariant
    
    Text(
        text = String.format("%s%.1f%% %s", sign, change, label),
        style = MaterialTheme.typography.bodySmall,
        color = color
    )
}

private fun formatDuration(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    return "${hours}h ${minutes}m"
}

@Composable
fun PieChartComposable(
    totalCount: Int,
    errorCount: Int,
    centerText: String,
    modifier: Modifier = Modifier
) {
    if (totalCount == 0) return

    val successLabel = stringResource(R.string.execution_status_success)
    val failureLabel = stringResource(R.string.execution_status_failure)
    val colorPrimary = MaterialTheme.colorScheme.primary.toArgb()
    val colorError = MaterialTheme.colorScheme.error.toArgb()
    val colorSurface = MaterialTheme.colorScheme.surface.toArgb()
    val colorOnSurface = MaterialTheme.colorScheme.onSurface.toArgb()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            com.github.mikephil.charting.charts.PieChart(context).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                setHoleColor(colorSurface)
                setCenterTextColor(colorOnSurface)
                holeRadius = 58f
                transparentCircleRadius = 61f
                setCenterTextSize(16f)
                legend.isEnabled = false
            }
        },
        update = { chart ->
            chart.centerText = centerText
            chart.setHoleColor(colorSurface)
            chart.setCenterTextColor(colorOnSurface)

            val success = totalCount - errorCount
            val entries = listOf(
                PieEntry(success.toFloat(), successLabel),
                PieEntry(errorCount.toFloat(), failureLabel)
            )

            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(colorPrimary, colorError)
                setDrawValues(true)
                valueTextColor = android.graphics.Color.WHITE
                valueTextSize = 12f
            }

            chart.data = PieData(dataSet)
            chart.invalidate()
            chart.animateY(1400)
        }
    )
}

@Composable
fun BarChartComposable(
    entries: List<BarEntry>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    if (entries.isEmpty()) return

    val label = stringResource(R.string.dashboard_executions)
    val colorOnSurface = MaterialTheme.colorScheme.onSurface.toArgb()
    // Material Colors for bars
    val colors = ColorTemplate.MATERIAL_COLORS.toList()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            com.github.mikephil.charting.charts.BarChart(context).apply {
                description.isEnabled = false
                setFitBars(true)
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    labelRotationAngle = -45f
                }
                axisLeft.apply {
                    setDrawGridLines(false)
                    axisMinimum = 0f
                }
                axisRight.isEnabled = false
                legend.isEnabled = true
            }
        },
        update = { chart ->
            chart.xAxis.textColor = colorOnSurface
            chart.axisLeft.textColor = colorOnSurface
            chart.legend.textColor = colorOnSurface
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

            val dataSet = BarDataSet(entries, label).apply {
                this.colors = colors
                setDrawValues(true)
                valueTextColor = colorOnSurface
                valueTextSize = 10f
            }

            chart.data = BarData(dataSet)
            chart.invalidate()
            chart.animateY(1400)
        }
    )
}
