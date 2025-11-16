package com.gws.auto.mobile.android.ui.workflow

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.databinding.ItemWorkflowFolderBinding
import com.gws.auto.mobile.android.databinding.ListItemAddWorkflowBinding
import com.gws.auto.mobile.android.databinding.ListItemWorkflowBinding
import com.gws.auto.mobile.android.domain.model.Workflow
import com.gws.auto.mobile.android.domain.model.WorkflowFolder
import com.gws.auto.mobile.android.domain.model.WorkflowListItem

class WorkflowAdapter(
    private val onRunClicked: (Workflow) -> Unit,
    private val onEditClicked: (Workflow) -> Unit,
    private val onDeleteClicked: (Workflow) -> Unit,
    private val onAddClicked: () -> Unit,
    private val onFavoriteClicked: (Workflow) -> Unit,
    private val onFolderClicked: (WorkflowFolder) -> Unit
) : ListAdapter<WorkflowListItem, RecyclerView.ViewHolder>(WorkflowListItemDiffCallback()) {

    private val VIEW_TYPE_WORKFLOW = 1
    private val VIEW_TYPE_FOLDER = 2
    private val VIEW_TYPE_ADD = 3

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is WorkflowListItem.WorkflowItem -> VIEW_TYPE_WORKFLOW
            is WorkflowListItem.FolderItem -> VIEW_TYPE_FOLDER
            is WorkflowListItem.AddItem -> VIEW_TYPE_ADD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_WORKFLOW -> {
                val binding = ListItemWorkflowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                WorkflowViewHolder(binding, onRunClicked, onEditClicked, onDeleteClicked, onFavoriteClicked)
            }
            VIEW_TYPE_FOLDER -> {
                val binding = ItemWorkflowFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FolderViewHolder(binding, onFolderClicked)
            }
            VIEW_TYPE_ADD -> {
                val binding = ListItemAddWorkflowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AddWorkflowViewHolder(binding, onAddClicked)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is WorkflowListItem.WorkflowItem -> (holder as WorkflowViewHolder).bind(item.workflow, item.isIndented)
            is WorkflowListItem.FolderItem -> (holder as FolderViewHolder).bind(item.folder)
            is WorkflowListItem.AddItem -> {}
        }
    }

    class WorkflowViewHolder(
        private val binding: ListItemWorkflowBinding,
        private val onRunClicked: (Workflow) -> Unit,
        private val onEditClicked: (Workflow) -> Unit,
        private val onDeleteClicked: (Workflow) -> Unit,
        private val onFavoriteClicked: (Workflow) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(workflow: Workflow, isIndented: Boolean) {
            binding.workflowName.text = workflow.name
            binding.workflowDescription.text = workflow.description
            binding.workflowStatus.text = workflow.status
            binding.workflowTrigger.text = workflow.trigger
            binding.favoriteButton.isChecked = workflow.isFavorite

            binding.root.setOnClickListener { onEditClicked(workflow) }
            binding.runButton.setOnClickListener { onRunClicked(workflow) }
            binding.deleteButton.setOnClickListener { onDeleteClicked(workflow) }
            binding.favoriteButton.setOnClickListener { onFavoriteClicked(workflow) }

            val indentation = if (isIndented) 64 else 0 // 64dp for indentation
            binding.root.updatePadding(left = indentation)
        }
    }

    class FolderViewHolder(
        private val binding: ItemWorkflowFolderBinding,
        private val onFolderClicked: (WorkflowFolder) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(folder: WorkflowFolder) {
            binding.folderName.text = folder.name
            itemView.setOnClickListener { onFolderClicked(folder) }
        }
    }

    class AddWorkflowViewHolder(
        binding: ListItemAddWorkflowBinding,
        private val onAddClicked: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener { onAddClicked() }
        }
    }
}

class WorkflowListItemDiffCallback : DiffUtil.ItemCallback<WorkflowListItem>() {
    override fun areItemsTheSame(oldItem: WorkflowListItem, newItem: WorkflowListItem): Boolean {
        return when {
            oldItem is WorkflowListItem.WorkflowItem && newItem is WorkflowListItem.WorkflowItem -> oldItem.workflow.id == newItem.workflow.id
            oldItem is WorkflowListItem.FolderItem && newItem is WorkflowListItem.FolderItem -> oldItem.folder.id == newItem.folder.id
            oldItem is WorkflowListItem.AddItem && newItem is WorkflowListItem.AddItem -> true
            else -> false
        }
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: WorkflowListItem, newItem: WorkflowListItem): Boolean {
        return oldItem == newItem
    }
}
