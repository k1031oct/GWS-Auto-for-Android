package com.gws.auto.mobile.android.ui.workflow.editor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.databinding.ListItemModuleLibraryBinding
import com.gws.auto.mobile.android.domain.model.Module

class ModuleLibraryAdapter(
    private var modules: List<Module>,
    private val onModuleLongClickListener: (Module, View) -> Boolean
) : RecyclerView.Adapter<ModuleLibraryAdapter.ModuleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding = ListItemModuleLibraryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        val module = modules[position]
        holder.bind(module)
        holder.itemView.setOnLongClickListener { view ->
            onModuleLongClickListener(module, view)
        }
    }

    override fun getItemCount(): Int = modules.size

    fun updateModules(newModules: List<Module>) {
        this.modules = newModules
        notifyDataSetChanged()
    }

    class ModuleViewHolder(private val binding: ListItemModuleLibraryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(module: Module) {
            binding.moduleName.text = module.type
        }
    }
}
