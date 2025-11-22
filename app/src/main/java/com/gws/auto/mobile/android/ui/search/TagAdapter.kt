package com.gws.auto.mobile.android.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.databinding.ListItemAddTagBinding
import com.gws.auto.mobile.android.databinding.ListItemTagBinding
import com.gws.auto.mobile.android.domain.model.DisplayTag
import com.gws.auto.mobile.android.domain.model.FilterTag
import com.gws.auto.mobile.android.domain.model.Tag
import android.content.res.ColorStateList
import android.graphics.Color

class TagAdapter(
    private val onTagClicked: (DisplayTag) -> Unit,
    private val onTagLongClicked: (Tag) -> Unit, // Long click only for real tags
    private val onAddTagClicked: () -> Unit
) : ListAdapter<DisplayTag, RecyclerView.ViewHolder>(DisplayTagDiffCallback()) {

    var highlightColor: Int? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    companion object {
        private const val VIEW_TYPE_TAG = 0
        private const val VIEW_TYPE_ADD = 1
        private const val VIEW_TYPE_FILTER = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == itemCount - 1 -> VIEW_TYPE_ADD
            getItem(position).isFilter -> VIEW_TYPE_FILTER
            else -> VIEW_TYPE_TAG
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_TAG -> {
                val binding = ListItemTagBinding.inflate(inflater, parent, false)
                TagViewHolder(binding, onTagClicked, onTagLongClicked)
            }
            VIEW_TYPE_ADD -> {
                val binding = ListItemAddTagBinding.inflate(inflater, parent, false)
                AddTagViewHolder(binding, onAddTagClicked, highlightColor)
            }
            VIEW_TYPE_FILTER -> {
                val binding = ListItemTagBinding.inflate(inflater, parent, false) // Reuse tag layout
                FilterTagViewHolder(binding, onTagClicked)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TagViewHolder -> holder.bind(getItem(position) as Tag)
            is FilterTagViewHolder -> holder.bind(getItem(position) as FilterTag)
            is AddTagViewHolder -> holder.bind(highlightColor)
        }
    }

    class TagViewHolder(
        private val binding: ListItemTagBinding,
        private val onTagClicked: (Tag) -> Unit,
        private val onTagLongClicked: (Tag) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: Tag) {
            binding.tagName.text = tag.displayName
            binding.root.setOnClickListener { onTagClicked(tag) }
            binding.root.setOnLongClickListener {
                onTagLongClicked(tag)
                true
            }
        }
    }

    class FilterTagViewHolder(
        private val binding: ListItemTagBinding,
        private val onTagClicked: (FilterTag) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: FilterTag) {
            binding.tagName.text = tag.displayName // e.g., "★ Favorites"
            binding.root.setOnClickListener { onTagClicked(tag) }
            // No long click for filter tags
        }
    }

    class AddTagViewHolder(
        private val binding: ListItemAddTagBinding,
        private val onAddTagClicked: () -> Unit,
        highlightColor: Int?
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { onAddTagClicked() }
            bind(highlightColor)
        }

        fun bind(highlightColor: Int?) {
            if (highlightColor != null) {
                val chip = binding.root as? com.google.android.material.chip.Chip
                chip?.chipBackgroundColor = ColorStateList.valueOf(highlightColor)
                // Also tint the icon to be white for better contrast if the background is dark
                // For now, let's assume the icon tint should match the text color or be white/black based on theme
                // But the user only complained about the chip color.
                // Let's also set the icon tint to white to be safe as highlight colors are usually dark/vibrant
                chip?.chipIconTint = ColorStateList.valueOf(android.graphics.Color.BLACK)
                chip?.setTextColor(android.graphics.Color.BLACK)
            }
        }
    }
}

class DisplayTagDiffCallback : DiffUtil.ItemCallback<DisplayTag>() {
    override fun areItemsTheSame(oldItem: DisplayTag, newItem: DisplayTag): Boolean {
        return oldItem.displayName == newItem.displayName
    }

    override fun areContentsTheSame(oldItem: DisplayTag, newItem: DisplayTag): Boolean {
        return oldItem == newItem
    }
}
