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
        setupFab()
        setupDragAndDrop()
    }

    private fun setupRecyclerView() {
        workflowAdapter = WorkflowAdapter(
            onRunClicked = { workflow ->
                lifecycleScope.launch {
                    try {
                        workflowEngine.execute(workflow.modules)
                        Timber.d("Workflow executed: ${workflow.name}")
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
            onDeleteClicked = { workflow -> viewModel.deleteWorkflow(workflow) },
            onAddClicked = {
                Timber.d("Add new workflow clicked")
                startActivity(Intent(activity, WorkflowEditorActivity::class.java))
            },
            onFavoriteClicked = { workflow ->
                viewModel.toggleFavorite(workflow)
            },
            onFolderClicked = { folder ->
                // TODO: Implement folder opening/closing logic
                Timber.d("Folder clicked: ${folder.name}")
            }
        )
        binding.workflowRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.workflowRecyclerView.adapter = workflowAdapter
    }

    private fun setupFab() {
        binding.fabAddFolder.setOnClickListener {
            showCreateFolderDialog()
        }
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
                return false // We are not reordering
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.5f
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.alpha = 1.0f

                // Find the view under the center of the dragged item
                val dropTargetView = recyclerView.findChildViewUnder(
                    viewHolder.itemView.x + viewHolder.itemView.width / 2,
                    viewHolder.itemView.y + viewHolder.itemView.height / 2
                )

                if (dropTargetView != null) {
                    val targetPosition = recyclerView.getChildAdapterPosition(dropTargetView)
                    val sourcePosition = viewHolder.adapterPosition

                    if (targetPosition != RecyclerView.NO_POSITION && sourcePosition != RecyclerView.NO_POSITION) {
                        val sourceItem = workflowAdapter.currentList[sourcePosition]
                        val targetItem = workflowAdapter.currentList[targetPosition]

                        if (sourceItem is WorkflowListItem.WorkflowItem && targetItem is WorkflowListItem.FolderItem) {
                            viewModel.moveWorkflowToFolder(sourceItem.workflow.id, targetItem.folder.id)
                        }
                    }
                }
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
