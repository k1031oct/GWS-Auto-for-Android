package com.gws.auto.mobile.android.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.databinding.ListItemSearchHistoryBinding
import com.gws.auto.mobile.android.domain.model.Tag
import com.gws.auto.mobile.android.domain.model.Workflow
import com.gws.auto.mobile.android.domain.model.WorkflowFolder

class SearchSuggestionAdapter(
    private val onSuggestionClicked: (SearchSuggestion) -> Unit
) : ListAdapter<SearchSuggestion, RecyclerView.ViewHolder>(SearchSuggestionDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_WORKFLOW = 0
        private const val VIEW_TYPE_FOLDER = 1
        private const val VIEW_TYPE_TAG = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SearchSuggestion.WorkflowItem -> VIEW_TYPE_WORKFLOW
            is SearchSuggestion.FolderItem -> VIEW_TYPE_FOLDER
            is SearchSuggestion.TagItem -> VIEW_TYPE_TAG
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        // Reusing ListItemSearchHistoryBinding for simplicity for now, 
        // but ideally should have specific layouts or a generic one with icon support.
        // Let's assume we can set icon and text on this binding.
        val binding = ListItemSearchHistoryBinding.inflate(inflater, parent, false)
        return SuggestionViewHolder(binding, onSuggestionClicked)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SuggestionViewHolder).bind(getItem(position))
    }

    class SuggestionViewHolder(
        private val binding: ListItemSearchHistoryBinding,
        private val onSuggestionClicked: (SearchSuggestion) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchSuggestion) {
            when (item) {
                is SearchSuggestion.WorkflowItem -> {
                    binding.queryText.text = item.workflow.name
                    binding.suggestionIcon.setImageResource(com.gws.auto.mobile.android.R.drawable.ic_workflow)
                }
                is SearchSuggestion.FolderItem -> {
                    binding.queryText.text = item.folder.name
                    binding.suggestionIcon.setImageResource(com.gws.auto.mobile.android.R.drawable.ic_folder)
                }
                is SearchSuggestion.TagItem -> {
                    binding.queryText.text = item.tag.name
                    binding.suggestionIcon.setImageResource(com.gws.auto.mobile.android.R.drawable.ic_bookmark) // Using bookmark icon for tags
                }
            }
            binding.root.setOnClickListener { onSuggestionClicked(item) }
        }
    }
}

class SearchSuggestionDiffCallback : DiffUtil.ItemCallback<SearchSuggestion>() {
    override fun areItemsTheSame(oldItem: SearchSuggestion, newItem: SearchSuggestion): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: SearchSuggestion, newItem: SearchSuggestion): Boolean {
        return oldItem == newItem
    }
}
