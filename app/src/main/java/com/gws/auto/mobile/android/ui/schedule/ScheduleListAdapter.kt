package com.gws.auto.mobile.android.ui.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.data.model.Schedule
import timber.log.Timber

class ScheduleListAdapter(private val onScheduleClick: (Schedule) -> Unit) :
    ListAdapter<Schedule, ScheduleListAdapter.ScheduleListViewHolder>(ScheduleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_schedule, parent, false)
        return ScheduleListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleListViewHolder, position: Int) {
        val schedule = getItem(position)
        holder.bind(schedule)
        holder.itemView.setOnClickListener { onScheduleClick(schedule) }
    }

    class ScheduleListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val workflowNameTextView: TextView = itemView.findViewById(R.id.schedule_workflow_name)
        private val typeAndTimeTextView: TextView = itemView.findViewById(R.id.schedule_type_and_time)
        private val statusTextView: TextView = itemView.findViewById(R.id.schedule_status)

        fun bind(schedule: Schedule) {
            // TODO: Get actual workflow name from workflowId
            workflowNameTextView.text = itemView.context.getString(R.string.workflow_id_label, schedule.workflowId)

            val typeAndTimeString = when (schedule.scheduleType) {
                "時間毎" -> itemView.context.getString(R.string.schedule_hourly_format, schedule.hourlyInterval)
                "日毎" -> itemView.context.getString(R.string.schedule_daily_format, schedule.time)
                "週毎" -> itemView.context.getString(R.string.schedule_weekly_format, schedule.weeklyDays?.joinToString(", "), schedule.time)
                "月毎" -> itemView.context.getString(R.string.schedule_monthly_format, schedule.monthlyDays?.joinToString(", "), schedule.time)
                "年毎" -> itemView.context.getString(R.string.schedule_yearly_format, schedule.yearlyMonth, schedule.yearlyDayOfMonth, schedule.time)
                else -> schedule.scheduleType
            }
            typeAndTimeTextView.text = typeAndTimeString
            statusTextView.text = if (schedule.isEnabled) itemView.context.getString(R.string.status_active) else itemView.context.getString(R.string.status_inactive)
        }
    }
}

class ScheduleDiffCallback : DiffUtil.ItemCallback<Schedule>() {
    override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
        return oldItem == newItem
    }
}
