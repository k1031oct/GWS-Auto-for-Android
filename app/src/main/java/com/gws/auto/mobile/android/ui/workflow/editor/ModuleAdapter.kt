package com.gws.auto.mobile.android.ui.workflow.editor

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.ItemWorkflowDividerBinding
import com.gws.auto.mobile.android.databinding.ListItemModuleBinding
import com.gws.auto.mobile.android.domain.model.Module
import java.util.Collections

class ModuleAdapter(
    private val onEditClicked: (Module) -> Unit,
    private val onRemoveClicked: (Module) -> Unit,
    private val onRunModuleClicked: (Module) -> Unit,
    private val onModuleEnabledChanged: (Module, Boolean) -> Unit,
    private val onStartDragListener: (RecyclerView.ViewHolder) -> Unit,
    private val onModulesReordered: (List<Module>) -> Unit,
    private val onInsertModuleClicked: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {

    private var modules: MutableList<Module> = mutableListOf()

    companion object {
        private const val VIEW_TYPE_DIVIDER = 0
        private const val VIEW_TYPE_MODULE = 1
        private const val VIEW_TYPE_FOOTER = 2
    }

    fun submitList(newModules: List<Module>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = modules.size * 2 + 2
            override fun getNewListSize(): Int = newModules.size * 2 + 2

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldIsFooter = oldItemPosition == modules.size * 2 + 1
                val newIsFooter = newItemPosition == newModules.size * 2 + 1
                if (oldIsFooter && newIsFooter) return true
                if (oldIsFooter || newIsFooter) return false

                val oldIsModule = oldItemPosition % 2 != 0
                val newIsModule = newItemPosition % 2 != 0
                if (oldIsModule != newIsModule) return false
                
                if (oldIsModule) {
                    val oldModuleIndex = (oldItemPosition - 1) / 2
                    val newModuleIndex = (newItemPosition - 1) / 2
                    if (oldModuleIndex >= modules.size || newModuleIndex >= newModules.size) return false
                    return modules[oldModuleIndex].id == newModules[newModuleIndex].id
                } else {
                    return oldItemPosition == newItemPosition
                }
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val isFooter = oldItemPosition == modules.size * 2 + 1
                if (isFooter) return true

                val isModule = oldItemPosition % 2 != 0
                if (isModule) {
                    val moduleIndex = (oldItemPosition - 1) / 2
                    val newModuleIndex = (newItemPosition - 1) / 2
                    if (moduleIndex >= modules.size || newModuleIndex >= newModules.size) return false
                    return modules[moduleIndex] == newModules[newModuleIndex]
                }
                return true
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        modules.clear()
        modules.addAll(newModules)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == modules.size * 2 + 1) return VIEW_TYPE_FOOTER
        return if (position % 2 == 0) VIEW_TYPE_DIVIDER else VIEW_TYPE_MODULE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MODULE -> {
                val binding = ListItemModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ModuleViewHolder(binding)
            }
            VIEW_TYPE_DIVIDER -> {
                val binding = ItemWorkflowDividerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DividerViewHolder(binding)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workflow_footer, parent, false)
                FooterViewHolder(view)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ModuleViewHolder) {
            val moduleIndex = (position - 1) / 2
            val module = modules[moduleIndex]
            holder.bind(module)
            holder.itemView.setOnClickListener { onEditClicked(module) }
            holder.binding.deleteButton.setOnClickListener { onRemoveClicked(module) }
            holder.binding.runModuleButton.setOnClickListener { onRunModuleClicked(module) }
            holder.binding.moduleEnabledSwitch.setOnCheckedChangeListener { _, isChecked ->
                onModuleEnabledChanged(module, isChecked)
            }

            holder.binding.dragHandle.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    onStartDragListener(holder)
                }
                false
            }

            // Timeline lines logic
            if (moduleIndex == 0) {
                holder.binding.lineTop.visibility = View.INVISIBLE
            } else {
                holder.binding.lineTop.visibility = View.VISIBLE
            }

            if (moduleIndex == modules.size - 1) {
                holder.binding.lineBottom.visibility = View.INVISIBLE
            } else {
                holder.binding.lineBottom.visibility = View.VISIBLE
            }

        } else if (holder is DividerViewHolder) {
            val dividerIndex = position / 2
            holder.binding.root.setOnClickListener { onInsertModuleClicked(dividerIndex) }
            
            if (dividerIndex == 0) {
                holder.binding.lineTop.visibility = View.INVISIBLE
            } else {
                holder.binding.lineTop.visibility = View.VISIBLE
            }
            
            if (dividerIndex == modules.size) {
                holder.binding.lineBottom.visibility = View.INVISIBLE
            } else {
                holder.binding.lineBottom.visibility = View.VISIBLE
            }
        } else if (holder is FooterViewHolder) {
            holder.itemView.findViewById<View>(R.id.add_module_button).setOnClickListener {
                onInsertModuleClicked(modules.size)
            }
        }
    }

    override fun getItemCount(): Int = modules.size * 2 + 2

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        // Only allow moving modules
        if (getItemViewType(fromPosition) != VIEW_TYPE_MODULE || getItemViewType(toPosition) != VIEW_TYPE_MODULE) {
            return false
        }

        val fromModuleIndex = (fromPosition - 1) / 2
        val toModuleIndex = (toPosition - 1) / 2

        if (fromModuleIndex < toModuleIndex) {
            for (i in fromModuleIndex until toModuleIndex) {
                Collections.swap(modules, i, i + 1)
            }
        } else {
            for (i in fromModuleIndex downTo toModuleIndex + 1) {
                Collections.swap(modules, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
        if (getItemViewType(position) != VIEW_TYPE_MODULE) return

        val moduleIndex = (position - 1) / 2
        val module = modules[moduleIndex]
        modules.removeAt(moduleIndex)
        
        // Remove the module and the following divider?
        // Or the preceding divider?
        // We have [Div0, Mod0, Div1, Mod1, Div2]
        // Remove Mod0 (pos 1). We want [Div0, Mod1, Div2] -> [Div0, Mod0', Div1']
        // Actually we want to remove 2 items to keep the pattern.
        // Remove pos 1 (Module) and pos 2 (Divider).
        // Result: [Div0, Mod1, Div2] (indices shifted).
        // Wait, if we remove Mod0, we have [Div0, Div1, Mod1, Div2].
        // Div0 and Div1 are adjacent.
        // So we remove Mod0 and Div1.
        
        notifyItemRemoved(position) // Remove module
        notifyItemRemoved(position) // Remove the item that is now at 'position' (which was position+1, the divider)
        
        onRemoveClicked(module)
    }

    override fun onItemClear() {
        onModulesReordered(modules.toList())
    }

    class ModuleViewHolder(val binding: ListItemModuleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(module: Module) {
            val context = binding.root.context
            val moduleKeys = context.resources.getStringArray(R.array.module_keys)
            val moduleDisplayNames = context.resources.getStringArray(R.array.module_display_names)
            
            val index = moduleKeys.indexOf(module.type)
            if (index != -1 && index < moduleDisplayNames.size) {
                binding.moduleName.text = moduleDisplayNames[index]
            } else {
                binding.moduleName.text = module.type
            }
            
            val description = module.parameters.entries.joinToString(", ") { "${it.key}: ${it.value}" }
            if (description.isNotEmpty()) {
                binding.moduleDescription.text = description
                binding.moduleDescription.visibility = View.VISIBLE
            } else {
                binding.moduleDescription.visibility = View.GONE
            }
            
            binding.moduleEnabledSwitch.isChecked = module.isEnabled
        }
    }

    class DividerViewHolder(val binding: ItemWorkflowDividerBinding) : RecyclerView.ViewHolder(binding.root)
    
    class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
