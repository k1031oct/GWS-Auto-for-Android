package com.gws.auto.mobile.android.ui.workflow.editor

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.databinding.ListItemModuleBinding
import com.gws.auto.mobile.android.domain.model.Module

class ModuleAdapter(
    private val onEditClicked: (Module) -> Unit,
    private val onRemoveClicked: (Module) -> Unit
) : ListAdapter<Module, ModuleAdapter.ModuleViewHolder>(ModuleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding = ListItemModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        val module = getItem(position)
        holder.bind(module)
        holder.itemView.setOnClickListener { onEditClicked(module) }
        holder.binding.deleteButton.setOnClickListener { onRemoveClicked(module) }

        if (position == 0) {
            holder.binding.lineTop.visibility = View.INVISIBLE
        } else {
            holder.binding.lineTop.visibility = View.VISIBLE
        }

        if (position == itemCount - 1) {
            holder.binding.lineBottom.visibility = View.INVISIBLE
        } else {
            holder.binding.lineBottom.visibility = View.VISIBLE
        }
    }

    inner class ModuleViewHolder(val binding: ListItemModuleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(module: Module) {
            binding.moduleName.text = module.type
        }
    }
}

class ModuleDiffCallback : DiffUtil.ItemCallback<Module>() {
    override fun areItemsTheSame(oldItem: Module, newItem: Module): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Module, newItem: Module): Boolean {
        return oldItem == newItem
    }
}
