package com.gws.auto.mobile.android.ui.settings.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.databinding.ListItemSettingTagBinding
import com.gws.auto.mobile.android.domain.model.Tag

class TagAdapter(
    private val onTagClick: (Tag) -> Unit,
    private val onDeleteClick: (Tag) -> Unit
) : ListAdapter<Tag, TagAdapter.TagViewHolder>(TagDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding = ListItemSettingTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagViewHolder(binding, onTagClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TagViewHolder(
        private val binding: ListItemSettingTagBinding,
        private val onTagClick: (Tag) -> Unit,
        private val onDeleteClick: (Tag) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: Tag) {
            binding.tagNameText.text = tag.name
            binding.root.setOnClickListener { onTagClick(tag) }
            binding.deleteTagButton.setOnClickListener { onDeleteClick(tag) }
        }
    }
}

class TagDiffCallback : DiffUtil.ItemCallback<Tag>() {
    override fun areItemsTheSame(oldItem: Tag, newItem: Tag): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Tag, newItem: Tag): Boolean {
        return oldItem == newItem
    }
}
