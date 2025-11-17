package com.gws.auto.mobile.android.ui.workflow

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentWorkflowBinding
import com.gws.auto.mobile.android.domain.engine.WorkflowEngine
import com.gws.auto.mobile.android.domain.model.Workflow
import com.gws.auto.mobile.android.domain.model.WorkflowFolder
import com.gws.auto.mobile.android.domain.model.WorkflowListItem
import com.gws.auto.mobile.android.ui.MainSharedViewModel
import com.gws.auto.mobile.android.ui.workflow.editor.WorkflowEditorActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WorkflowFragment : Fragment() {

    private var _binding: FragmentWorkflowBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkflowViewModel by viewModels()
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    @Inject
    lateinit var workflowEngine: WorkflowEngine

    private lateinit var workflowAdapter: WorkflowAdapter
    private var dropTargetFolder: WorkflowListItem.FolderItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkflowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated called")
        setupRecyclerView()
        observeViewModels()
        setupDragAndDrop()
    }

    private fun setupRecyclerView() {
        workflowAdapter = WorkflowAdapter(
            onRunClicked = { workflow ->
                lifecycleScope.launch {
                    try {
                        workflowEngine.execute(workflow.id, workflow.name, workflow.modules)
                        Timber.d("Workflow execution requested: ${workflow.name}")
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to execute workflow: ${workflow.name}")
                    }
                }
            },
            onEditClicked = { workflow ->
                val intent = Intent(activity, WorkflowEditorActivity::class.java)
                intent.putExtra("workflowId", workflow.id)
                startActivity(intent)
            },
            onDeleteClicked = { workflow -> showDeleteConfirmationDialog(workflow) },
            onFolderDeleteClicked = { folder -> showDeleteFolderConfirmationDialog(folder) },
            onAddClicked = {
                Timber.d("Add new workflow clicked")
                startActivity(Intent(activity, WorkflowEditorActivity::class.java))
            },
            onFavoriteClicked = { workflow ->
                viewModel.toggleFavorite(workflow)
            },
            onFolderClicked = { folder ->
                viewModel.toggleFolderExpansion(folder.id)
            }
        )
        binding.workflowRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.workflowRecyclerView.adapter = workflowAdapter
    }

    private fun showCreateFolderDialog() {
        val editText = EditText(context).apply {
            hint = getString(R.string.folder_name_hint)
        }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.create_folder_dialog_title)
            .setView(editText)
            .setPositiveButton(R.string.create) { dialog, _ ->
                val folderName = editText.text.toString()
                if (folderName.isNotBlank()) {
                    viewModel.createFolder(folderName)
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
    
    private fun showDeleteConfirmationDialog(workflow: Workflow) {
        AlertDialog.Builder(requireContext())
            .setTitle(workflow.name)
            .setMessage("このワークフローを本当に削除しますか？")
            .setPositiveButton("削除") { _, _ ->
                viewModel.deleteWorkflow(workflow)
            }
            .setNegativeButton("キャンセル", null)
            .show()
    }

    private fun showDeleteFolderConfirmationDialog(folder: WorkflowFolder) {
        AlertDialog.Builder(requireContext())
            .setTitle(folder.name)
            .setMessage("このフォルダを本当に削除しますか？ (中のワークフローは削除されません)")
            .setPositiveButton("削除") { _, _ ->
                viewModel.deleteWorkflowFolder(folder.id)
            }
            .setNegativeButton("キャンセル", null)
            .show()
    }

    private fun setupDragAndDrop() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = if (viewHolder is WorkflowAdapter.WorkflowViewHolder) {
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN
                } else {
                    0
                }
                return makeMovementFlags(dragFlags, 0)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val targetPosition = target.adapterPosition
                if (targetPosition != RecyclerView.NO_POSITION) {
                    val item = workflowAdapter.currentList.getOrNull(targetPosition)
                    if (item is WorkflowListItem.FolderItem) {
                        dropTargetFolder = item
                        // Optional: Add visual feedback, like changing folder background
                        target.itemView.setBackgroundResource(R.color.md_theme_light_primaryContainer)
                    } else {
                        dropTargetFolder = null
                        // Optional: Clear visual feedback
                        target.itemView.background = null
                    }
                }
                return true // Return true to indicate the move was handled
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.5f
                } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                    // Clear background of all views when drag is finished
                    for (i in 0 until binding.workflowRecyclerView.childCount) {
                        binding.workflowRecyclerView.getChildAt(i).background = null
                    }
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.alpha = 1.0f

                val sourcePosition = viewHolder.adapterPosition
                if (sourcePosition != RecyclerView.NO_POSITION && dropTargetFolder != null) {
                    val sourceItem = workflowAdapter.currentList.getOrNull(sourcePosition)
                    if (sourceItem is WorkflowListItem.WorkflowItem) {
                        viewModel.moveWorkflowToFolder(sourceItem.workflow.id, dropTargetFolder!!.folder.id)
                    }
                }
                dropTargetFolder = null // Reset the drop target
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.workflowRecyclerView)
    }

    private fun observeViewModels() {
        viewModel.filteredItems
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { items ->
                Timber.d("Updating UI with ${items.size} items.")
                workflowAdapter.submitList(items)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        mainSharedViewModel.searchQuery
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { query ->
                viewModel.onQueryChanged(query)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        mainSharedViewModel.fabClick
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { showCreateFolderDialog() }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
