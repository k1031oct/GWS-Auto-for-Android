package com.gws.auto.mobile.android.ui.workflow.editor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.ListItemModuleLibraryBinding
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.domain.model.ModuleCatalog

class ModuleLibraryAdapter(
    private var items: List<LibraryItem>,
    private val onItemClickListener: (LibraryItem) -> Unit = {} // Optional click listener
) : RecyclerView.Adapter<ModuleLibraryAdapter.ReelViewHolder>() {

    sealed class LibraryItem {
        data class FolderItem(val folder: ModuleCatalog.Folder) : LibraryItem()
        data class ModuleItem(val module: Module) : LibraryItem()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemModuleLibraryBinding.inflate(layoutInflater, parent, false)
        return ReelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        if (items.isNotEmpty()) {
            val actualPosition = position % items.size
            val item = items[actualPosition]
            holder.bind(item)
            holder.itemView.setOnClickListener { onItemClickListener(item) }
        }
    }

    override fun getItemCount(): Int {
        return if (items.isNotEmpty()) Int.MAX_VALUE else 0
    }

    fun getItem(position: Int): LibraryItem? {
        if (items.isEmpty()) return null
        val actualPosition = position % items.size
        return items[actualPosition]
    }

    fun updateItems(newItems: List<LibraryItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ReelViewHolder(private val binding: ListItemModuleLibraryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LibraryItem) {
            when (item) {
                is LibraryItem.FolderItem -> {
                    binding.moduleName.text = item.folder.name
                    binding.libraryIcon.setImageResource(R.drawable.ic_folder)
                }
                is LibraryItem.ModuleItem -> {
                    val module = item.module
                    val context = binding.root.context
                    val moduleKeys = context.resources.getStringArray(R.array.module_keys)
                    val moduleDisplayNames = context.resources.getStringArray(R.array.module_display_names)
                    
                    val index = moduleKeys.indexOf(module.type)
                    if (index != -1 && index < moduleDisplayNames.size) {
                        binding.moduleName.text = moduleDisplayNames[index]
                    } else {
                        binding.moduleName.text = module.type
                    }
                    binding.libraryIcon.setImageResource(R.drawable.ic_module)
                }
            }
            // Click listener is handled in onBindViewHolder
        }
    }
}
