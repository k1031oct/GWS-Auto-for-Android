package com.gws.auto.mobile.android.ui.history

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.ListItemHistoryHeaderBinding
import com.gws.auto.mobile.android.databinding.ListItemHistoryLogBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter(
    private val onHeaderClick: (String) -> Unit,
    private val onBookmarkClick: (HistoryListItem.HeaderItem) -> Unit
) : ListAdapter<HistoryListItem, RecyclerView.ViewHolder>(HistoryDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HistoryListItem.HeaderItem -> VIEW_TYPE_HEADER
            is HistoryListItem.LogItem -> VIEW_TYPE_LOG
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ListItemHistoryHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding, onHeaderClick, onBookmarkClick)
            }
            else -> {
                val binding = ListItemHistoryLogBinding.inflate(inflater, parent, false)
                LogViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HistoryListItem.HeaderItem -> (holder as HeaderViewHolder).bind(item)
            is HistoryListItem.LogItem -> (holder as LogViewHolder).bind(item)
        }
    }

    class HeaderViewHolder(
        private val binding: ListItemHistoryHeaderBinding,
        private val onHeaderClick: (String) -> Unit,
        private val onBookmarkClick: (HistoryListItem.HeaderItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryListItem.HeaderItem) {
            binding.workflowName.text = item.history.workflowName
            // Format the date for better readability
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            binding.executionTime.text = sdf.format(item.history.executedAt)
            binding.executionStatus.text = item.history.status
            binding.bookmarkButton.isChecked = item.history.isBookmarked

            // Set trigger type text
            val context = binding.root.context
            val triggerTypeResId = when (item.history.triggerType) {
                "MANUAL" -> R.string.trigger_type_manual
                "SCHEDULED" -> R.string.trigger_type_scheduled
                else -> 0
            }

            if (triggerTypeResId != 0) {
                binding.triggerTypeBadge.text = context.getString(triggerTypeResId)
            } else {
                binding.triggerTypeBadge.text = item.history.triggerType
            }

            itemView.setOnClickListener {
                onHeaderClick(item.history.id.toString())
            }
            binding.bookmarkButton.setOnClickListener {
                onBookmarkClick(item)
            }
        }
    }

    class LogViewHolder(private val binding: ListItemHistoryLogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryListItem.LogItem) {
            binding.logMessage.text = item.log
        }
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_LOG = 1
    }
}

class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryListItem>() {
    override fun areItemsTheSame(oldItem: HistoryListItem, newItem: HistoryListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HistoryListItem, newItem: HistoryListItem): Boolean {
        return oldItem == newItem
    }
}
