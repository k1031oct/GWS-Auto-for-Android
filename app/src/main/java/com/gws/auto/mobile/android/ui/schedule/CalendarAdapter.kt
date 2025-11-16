package com.gws.auto.mobile.android.ui.schedule

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.ListItemCalendarDayBinding
import com.gws.auto.mobile.android.domain.model.Holiday
import java.time.LocalDate

class CalendarAdapter(
    private val onDateClick: (LocalDate) -> Unit
) : ListAdapter<CalendarItem, CalendarAdapter.CalendarViewHolder>(CalendarDiffCallback()) {

    private var holidays = listOf<Holiday>()

    fun updateHolidays(newHolidays: List<Holiday>) {
        holidays = newHolidays
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ListItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding, onDateClick)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(getItem(position), holidays)
    }

    class CalendarViewHolder(
        private val binding: ListItemCalendarDayBinding,
        private val onDateClick: (LocalDate) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CalendarItem, holidays: List<Holiday>) {
            binding.dayText.text = item.day

            // Reset styles
            binding.dayText.background = null

            val typedValue = TypedValue()
            itemView.context.theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
            binding.dayText.setTextColor(typedValue.data)

            if (item.date != null) {
                itemView.setOnClickListener { onDateClick(item.date) }

                if (item.date == LocalDate.now()) {
                    binding.dayText.setBackgroundResource(R.drawable.bg_today)
                }

                val holiday = holidays.find { it.date == item.date }
                if (holiday != null) {
                    binding.dayText.text = "${item.day}\n${holiday.name}"
                    val colorError = ContextCompat.getColor(itemView.context, R.color.md_theme_light_error)
                    binding.dayText.setTextColor(colorError)
                }
            } else {
                itemView.setOnClickListener(null)
            }
        }
    }
}

class CalendarDiffCallback : DiffUtil.ItemCallback<CalendarItem>() {
    override fun areItemsTheSame(oldItem: CalendarItem, newItem: CalendarItem): Boolean {
        return oldItem.date == newItem.date && oldItem.day == newItem.day
    }

    override fun areContentsTheSame(oldItem: CalendarItem, newItem: CalendarItem): Boolean {
        return oldItem == newItem
    }
}

data class CalendarItem(
    val day: String,
    val date: LocalDate? // Null for empty placeholders
)
