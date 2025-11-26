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
            is FilterTagViewHolder -> holder.bind(getItem(position) as FilterTag, highlightColor)
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
        fun bind(tag: FilterTag, highlightColor: Int?) {
            binding.tagName.text = tag.displayName // e.g., "★ Favorites"
            
            if (tag.isActive && highlightColor != null) {
                val chip = binding.root as? com.google.android.material.chip.Chip
                chip?.chipBackgroundColor = ColorStateList.valueOf(highlightColor)
                
                val context = binding.root.context
                val nightModeFlags = context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
                val isDarkTheme = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES
                val textColor = if (isDarkTheme) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                
                chip?.setTextColor(textColor)
                chip?.chipIconTint = ColorStateList.valueOf(textColor)
            } else {
                // Reset to default style if not active
                val chip = binding.root as? com.google.android.material.chip.Chip
                // We need to reset to default colors. Since we don't have easy access to original theme attributes here without context resolution,
                // we might need to rely on the fact that RecyclerView rebinding usually clears state if we are careful, 
                // but for Chips, manual reset is safer.
                // However, getting the default "surfaceVariant" or similar from here is tricky without context.
                // A better approach is to invalidate the view or let the theme handle the default state.
                // But since we are manually setting it for active, we must manually unset it.
                
                // Let's try to get the default color from the context of the view
                val context = binding.root.context
                val defaultBackgroundColor = com.google.android.material.color.MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorSurfaceVariant)
                val defaultTextColor = com.google.android.material.color.MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorOnSurfaceVariant)
                
                chip?.chipBackgroundColor = ColorStateList.valueOf(defaultBackgroundColor)
                chip?.setTextColor(defaultTextColor)
                chip?.chipIconTint = ColorStateList.valueOf(defaultTextColor)
            }

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
                
                val context = binding.root.context
                val nightModeFlags = context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
                val isDarkTheme = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES
                val textColor = if (isDarkTheme) android.graphics.Color.BLACK else android.graphics.Color.WHITE

                chip?.chipIconTint = ColorStateList.valueOf(textColor)
                chip?.setTextColor(textColor)
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
