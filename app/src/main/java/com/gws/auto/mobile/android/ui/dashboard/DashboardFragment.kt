package com.gws.auto.mobile.android.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.gws.auto.mobile.android.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var workflowStatAdapter: WorkflowStatAdapter
    private lateinit var moduleStatAdapter: ModuleStatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCharts()
        setupRecyclerViews()
        observeViewModel()

        binding.refreshButton.setOnClickListener {
            viewModel.refresh()
        }
    }

    private fun setupCharts() {
        binding.workflowErrorRateChart.description.isEnabled = false
        binding.workflowRankingChart.description.isEnabled = false
        binding.moduleErrorRateChart.description.isEnabled = false
        binding.moduleRankingChart.description.isEnabled = false
    }

    private fun setupRecyclerViews() {
        workflowStatAdapter = WorkflowStatAdapter()
        binding.workflowStatsTable.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = workflowStatAdapter
        }

        moduleStatAdapter = ModuleStatAdapter()
        binding.moduleStatsTable.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = moduleStatAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.uiState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { uiState ->
                updateSummary(uiState)
                updateWorkflowCharts(uiState)
                updateModuleCharts(uiState)
                workflowStatAdapter.submitList(uiState.workflowExecutionCounts)
                moduleStatAdapter.submitList(uiState.moduleStats)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun updateSummary(uiState: DashboardUiState) {
        // Total Executions
        binding.totalExecutionsText.text = uiState.totalCountMonth.toString()
        binding.totalExecutionsDayChangeText.text = formatChange(uiState.totalCountDayChange, getString(R.string.dashboard_vs_yesterday))
        binding.totalExecutionsMonthChangeText.text = formatChange(uiState.totalCountMonthChange, getString(R.string.dashboard_vs_last_month))

        // Error Count
        binding.errorCountText.text = uiState.errorCountMonth.toString()
        binding.errorCountDayChangeText.text = formatChange(uiState.errorCountDayChange, getString(R.string.dashboard_vs_yesterday))
        binding.errorCountMonthChangeText.text = formatChange(uiState.errorCountMonthChange, getString(R.string.dashboard_vs_last_month))

        // Total Duration
        binding.totalDurationText.text = formatDuration(uiState.totalDurationMonth)
        binding.totalDurationDayChangeText.text = formatChange(uiState.totalDurationDayChange, getString(R.string.dashboard_vs_yesterday))
        binding.totalDurationMonthChangeText.text = formatChange(uiState.totalDurationMonthChange, getString(R.string.dashboard_vs_last_month))
    }

    private fun updateWorkflowCharts(uiState: DashboardUiState) {
        updateErrorRateChart(binding.workflowErrorRateChart, uiState.totalCountMonth, uiState.errorCountMonth, getString(R.string.dashboard_workflow_error_rate))
        updateRankingChart(binding.workflowRankingChart, uiState.workflowExecutionCounts.mapIndexed { index, it -> BarEntry(index.toFloat(), it.executionCount.toFloat()) }, uiState.workflowExecutionCounts.map { it.workflowName })
    }

    private fun updateModuleCharts(uiState: DashboardUiState) {
        updateErrorRateChart(binding.moduleErrorRateChart, uiState.moduleUsageCount, uiState.moduleErrorCount, getString(R.string.dashboard_module_error_rate))
        updateRankingChart(binding.moduleRankingChart, uiState.moduleStats.mapIndexed { index, it -> BarEntry(index.toFloat(), it.usageCount.toFloat()) }, uiState.moduleStats.map { it.moduleName })
    }

    private fun updateErrorRateChart(chart: com.github.mikephil.charting.charts.PieChart, total: Int, errors: Int, centerText: String) {
        if (total == 0) {
            chart.visibility = View.GONE
            return
        }
        chart.visibility = View.VISIBLE

        val success = total - errors
        val entries = listOf(
            PieEntry(success.toFloat(), getString(R.string.execution_status_success)),
            PieEntry(errors.toFloat(), getString(R.string.execution_status_failure))
        )

        val colorPrimary = ContextCompat.getColor(requireContext(), R.color.md_theme_light_primary)
        val colorError = ContextCompat.getColor(requireContext(), R.color.md_theme_light_error)

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(colorPrimary, colorError)
            setDrawValues(true)
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorSurface, typedValue, true)
        val holeColor = typedValue.data
        requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
        val textColor = typedValue.data

        chart.apply {
            data = PieData(dataSet)
            isDrawHoleEnabled = true
            setHoleColor(holeColor)
            setCenterTextColor(textColor)
            holeRadius = 58f
            transparentCircleRadius = 61f
            this.centerText = centerText
            setCenterTextSize(16f)
            legend.isEnabled = false
            animateY(1400)
            invalidate()
        }
    }

    private fun updateRankingChart(chart: com.github.mikephil.charting.charts.BarChart, entries: List<BarEntry>, labels: List<String>) {
        if (entries.isEmpty()) {
            chart.visibility = View.GONE
            return
        }
        chart.visibility = View.VISIBLE

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
        val textColor = typedValue.data

        val dataSet = BarDataSet(entries, getString(R.string.dashboard_executions)).apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            setDrawValues(true)
            valueTextColor = textColor
            valueTextSize = 10f
        }

        chart.apply {
            data = BarData(dataSet)
            setFitBars(true)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(labels)
                setDrawGridLines(false)
                granularity = 1f
                labelRotationAngle = -45f
                this.textColor = textColor
            }
            axisLeft.apply {
                setDrawGridLines(false)
                axisMinimum = 0f
                this.textColor = textColor
            }
            axisRight.isEnabled = false
            legend.textColor = textColor
            legend.isEnabled = true
            animateY(1400)
            invalidate()
        }
    }

    private fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        return "${hours}h ${minutes}m"
    }

    private fun formatChange(change: Float, context: String): String {
        val sign = if (change >= 0) "+" else ""
        return String.format("$sign%.1f%% %s", change, context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
