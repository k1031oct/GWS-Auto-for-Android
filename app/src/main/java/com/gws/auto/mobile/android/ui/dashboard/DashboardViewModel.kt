package com.gws.auto.mobile.android.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.HistoryRepository
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.model.History
import com.gws.auto.mobile.android.domain.model.Workflow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

// Helper data classes to manage intermediate combined flows
private data class PeriodStats(
    val statsMonth: StatsSummary,
    val statsPrevMonth: StatsSummary,
    val statsDay: StatsSummary,
    val statsPrevDay: StatsSummary
)

private data class RepositoryData(
    val workflowCounts: List<WorkflowExecutionCount>,
    val allWorkflows: List<Workflow>,
    val allHistory: List<History>
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private val _refreshTrigger = MutableStateFlow(Unit)

    // Intermediate flow for period statistics
    private val periodStatsFlow: Flow<PeriodStats> = combine(
        getStatsForMonth(LocalDate.now()),
        getStatsForMonth(LocalDate.now().minusMonths(1)),
        getStatsForDay(LocalDate.now()),
        getStatsForDay(LocalDate.now().minusDays(1))
    ) { statsMonth, statsPrevMonth, statsDay, statsPrevDay ->
        PeriodStats(statsMonth, statsPrevMonth, statsDay, statsPrevDay)
    }.stateIn(viewModelScope, SharingStarted.Lazily, PeriodStats(StatsSummary(0, 0, 0), StatsSummary(0, 0, 0), StatsSummary(0, 0, 0), StatsSummary(0, 0, 0)))

    // Intermediate flow for repository data
    private val repoDataFlow: Flow<RepositoryData> = combine(
        historyRepository.getWorkflowExecutionCounts(),
        workflowRepository.getAllWorkflows(),
        historyRepository.getAllHistory()
    ) { workflowCounts, allWorkflows, allHistory ->
        RepositoryData(workflowCounts, allWorkflows, allHistory)
    }.stateIn(viewModelScope, SharingStarted.Lazily, RepositoryData(emptyList(), emptyList(), emptyList()))

    // Final combined UI state
    val uiState: StateFlow<DashboardUiState> = combine(
        _refreshTrigger,
        periodStatsFlow,
        repoDataFlow
    ) { _, periodStats, repoData ->
        val moduleStats = calculateModuleStats(repoData.allWorkflows, repoData.allHistory)

        DashboardUiState(
            // Monthly Stats
            totalCountMonth = periodStats.statsMonth.totalCount,
            errorCountMonth = periodStats.statsMonth.errorCount,
            totalDurationMonth = periodStats.statsMonth.totalDuration,
            totalCountMonthChange = calculateChange(periodStats.statsMonth.totalCount.toLong(), periodStats.statsPrevMonth.totalCount.toLong()),
            errorCountMonthChange = calculateChange(periodStats.statsMonth.errorCount.toLong(), periodStats.statsPrevMonth.errorCount.toLong()),
            totalDurationMonthChange = calculateChange(periodStats.statsMonth.totalDuration, periodStats.statsPrevMonth.totalDuration),

            // Daily Stats
            totalCountDay = periodStats.statsDay.totalCount,
            errorCountDay = periodStats.statsDay.errorCount,
            totalDurationDay = periodStats.statsDay.totalDuration,
            totalCountDayChange = calculateChange(periodStats.statsDay.totalCount.toLong(), periodStats.statsPrevDay.totalCount.toLong()),
            errorCountDayChange = calculateChange(periodStats.statsDay.errorCount.toLong(), periodStats.statsPrevDay.errorCount.toLong()),
            totalDurationDayChange = calculateChange(periodStats.statsDay.totalDuration, periodStats.statsPrevDay.totalDuration),

            // Workflow Stats
            workflowExecutionCounts = repoData.workflowCounts,

            // Module Stats
            moduleUsageCount = repoData.allHistory.size,
            moduleErrorCount = repoData.allHistory.count { it.status != "Success" },
            moduleStats = moduleStats
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = DashboardUiState() // Provide a safe initial state
    )

    fun refresh() {
        _refreshTrigger.value = Unit
    }

    private fun getStatsForMonth(date: LocalDate): Flow<StatsSummary> = historyRepository.getStatsForPeriod(
        date.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        date.plusMonths(1).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    ).stateIn(viewModelScope, SharingStarted.Lazily, StatsSummary(0, 0, 0))

    private fun getStatsForDay(date: LocalDate): Flow<StatsSummary> = historyRepository.getStatsForPeriod(
        date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    ).stateIn(viewModelScope, SharingStarted.Lazily, StatsSummary(0, 0, 0))

    private fun calculateChange(current: Long, previous: Long): Float {
        if (previous == 0L) return 0f
        return (current - previous).toFloat() / previous.toFloat() * 100
    }

    private fun calculateModuleStats(workflows: List<Workflow>, history: List<History>): List<ModuleStat> {
        val moduleUsage = mutableMapOf<String, Int>()
        val moduleErrors = mutableMapOf<String, Int>()

        for (h in history) {
            val workflow = workflows.find { it.id == h.workflowId }
            workflow?.modules?.forEach { module ->
                moduleUsage[module.type] = (moduleUsage[module.type] ?: 0) + 1
                if (h.status != "Success") { // A simplified assumption
                    moduleErrors[module.type] = (moduleErrors[module.type] ?: 0) + 1
                }
            }
        }

        return moduleUsage.map { (name, usage) ->
            ModuleStat(name, usage, moduleErrors.getOrDefault(name, 0))
        }.sortedByDescending { it.usageCount }
    }
}
