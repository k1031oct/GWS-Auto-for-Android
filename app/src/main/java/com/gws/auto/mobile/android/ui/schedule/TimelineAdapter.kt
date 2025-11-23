package com.gws.auto.mobile.android.ui.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.data.model.Schedule
import com.gws.auto.mobile.android.databinding.ListItemTimelineEventBinding
import com.gws.auto.mobile.android.domain.model.Holiday
import java.time.format.DateTimeFormatter
import com.gws.auto.mobile.android.R

class TimelineAdapter(
    private var items: List<Any>
) : RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val binding = ListItemTimelineEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class TimelineViewHolder(private val binding: ListItemTimelineEventBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        fun bind(item: Any) {
            when (item) {
                is Schedule -> {
                    binding.eventTime.text = item.time
                    binding.eventTitle.text = binding.root.context.getString(R.string.workflow_label, item.workflowId)
                }
                is Holiday -> {
                    binding.eventTime.text = binding.root.context.getString(R.string.all_day)
                    binding.eventTitle.text = item.name
                }
                // Add other event types here (e.g., Google Calendar Events)
            }
        }
    }
}
