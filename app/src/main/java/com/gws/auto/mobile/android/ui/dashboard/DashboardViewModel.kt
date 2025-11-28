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

    private val _searchQuery = MutableStateFlow("")

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Final combined UI state
    // Final combined UI state
    val uiState: StateFlow<DashboardUiState> = combine(
        _refreshTrigger,
        historyRepository.getAllHistory(),
        workflowRepository.getAllWorkflows(),
        _searchQuery
    ) { _, allHistory, allWorkflows, query ->
        
        val filteredHistory = if (query.isBlank()) {
            allHistory
        } else {
            val workflowMap = allWorkflows.associateBy { it.id }
            allHistory.filter { history ->
                val workflow = workflowMap[history.workflowId]
                val matchName = history.workflowName.contains(query, ignoreCase = true)
                val matchModule = workflow?.modules?.any { it.type.contains(query, ignoreCase = true) } == true
                matchName || matchModule
            }
        }

        val now = LocalDate.now()
        val statsMonth = calculateStatsForPeriod(filteredHistory, now.withDayOfMonth(1), now.plusMonths(1).withDayOfMonth(1))
        val statsPrevMonth = calculateStatsForPeriod(filteredHistory, now.minusMonths(1).withDayOfMonth(1), now.withDayOfMonth(1))
        val statsDay = calculateStatsForPeriod(filteredHistory, now, now.plusDays(1))
        val statsPrevDay = calculateStatsForPeriod(filteredHistory, now.minusDays(1), now)
        
        val moduleStats = calculateModuleStats(allWorkflows, filteredHistory)
        
        // Recalculate workflow counts based on filtered history
        val workflowCounts = filteredHistory.groupingBy { it.workflowName }
            .eachCount()
            .map { (name, count) -> WorkflowExecutionCount(name, count) }
            .sortedByDescending { it.executionCount }
            .take(10)

        DashboardUiState(
            // Monthly Stats
            totalCountMonth = statsMonth.totalCount,
            errorCountMonth = statsMonth.errorCount,
            totalDurationMonth = statsMonth.totalDuration,
            totalCountMonthChange = calculateChange(statsMonth.totalCount.toLong(), statsPrevMonth.totalCount.toLong()),
            errorCountMonthChange = calculateChange(statsMonth.errorCount.toLong(), statsPrevMonth.errorCount.toLong()),
            totalDurationMonthChange = calculateChange(statsMonth.totalDuration, statsPrevMonth.totalDuration),

            // Daily Stats
            totalCountDay = statsDay.totalCount,
            errorCountDay = statsDay.errorCount,
            totalDurationDay = statsDay.totalDuration,
            totalCountDayChange = calculateChange(statsDay.totalCount.toLong(), statsPrevDay.totalCount.toLong()),
            errorCountDayChange = calculateChange(statsDay.errorCount.toLong(), statsPrevDay.errorCount.toLong()),
            totalDurationDayChange = calculateChange(statsDay.totalDuration, statsPrevDay.totalDuration),

            // Workflow Stats
            workflowExecutionCounts = workflowCounts,

            // Module Stats
            moduleUsageCount = moduleStats.sumOf { it.usageCount },
            moduleErrorCount = moduleStats.sumOf { it.errorCount },
            moduleStats = moduleStats
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = DashboardUiState()
    )

    fun refresh() {
        _refreshTrigger.value = Unit
    }

    private fun calculateStatsForPeriod(history: List<History>, start: LocalDate, end: LocalDate): StatsSummary {
        val startMilli = start.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMilli = end.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val periodHistory = history.filter { it.executedAt.time in startMilli until endMilli }

        return StatsSummary(
            totalCount = periodHistory.size,
            errorCount = periodHistory.count { it.status == "Failure" },
            totalDuration = periodHistory.sumOf { it.durationMs }
        )
    }

    private fun calculateChange(current: Long, previous: Long): Float {
        if (previous == 0L) return 0f
        return (current - previous).toFloat() / previous.toFloat() * 100
    }

    private fun calculateModuleStats(workflows: List<Workflow>, history: List<History>): List<ModuleStat> {
        val moduleUsage = mutableMapOf<String, Int>()
        val moduleErrors = mutableMapOf<String, Int>()

        for (h in history) {
            // logsフィールドをパースして各モジュールの実行結果を抽出
            val moduleResults = parseModuleResultsFromLogs(h.logs)
            
            for ((moduleType, isSuccess) in moduleResults) {
                // 使用回数をカウント
                moduleUsage[moduleType] = (moduleUsage[moduleType] ?: 0) + 1
                
                // 失敗した場合のみエラーカウント
                if (!isSuccess) {
                    moduleErrors[moduleType] = (moduleErrors[moduleType] ?: 0) + 1
                }
            }
        }

        return moduleUsage.map { (name, usage) ->
            ModuleStat(name, usage, moduleErrors.getOrDefault(name, 0))
        }.sortedByDescending { it.usageCount }
    }

    /**
     * History.logsフィールドをパースして、各モジュールの実行結果を抽出する
     * 
     * ログフォーマット:
     * [Step N: module_type]
     * Output: ...
     * または
     * [Step N: module_type] - Skipped (disabled)
     * または
     * ERROR: ...
     * 
     * @param logs 実行ログ
     * @return モジュールタイプと成功フラグのマップ (スキップされたモジュールは含まない)
     */
    private fun parseModuleResultsFromLogs(logs: String): Map<String, Boolean> {
        val results = mutableMapOf<String, Boolean>()
        val lines = logs.lines()
        
        var currentModule: String? = null
        var currentModuleSuccess = true
        
        for (line in lines) {
            // [Step N: module_type] のパターンをチェック
            val stepMatch = Regex("""\[Step \d+: (.+?)\]""").find(line)
            if (stepMatch != null) {
                // 前のモジュールの結果を保存
                if (currentModule != null) {
                    results[currentModule] = currentModuleSuccess
                }
                
                // 新しいモジュールの処理開始
                val moduleType = stepMatch.groupValues[1]
                
                // スキップされたモジュールはカウントしない
                if (line.contains("Skipped (disabled)")) {
                    currentModule = null
                    currentModuleSuccess = true
                } else {
                    currentModule = moduleType
                    currentModuleSuccess = true // デフォルトは成功
                }
            } else if (currentModule != null && line.contains("ERROR")) {
                // 現在のモジュールでエラーが発生
                currentModuleSuccess = false
            }
        }
        
        // 最後のモジュールの結果を保存
        if (currentModule != null) {
            results[currentModule] = currentModuleSuccess
        }
        
        return results
    }
}
