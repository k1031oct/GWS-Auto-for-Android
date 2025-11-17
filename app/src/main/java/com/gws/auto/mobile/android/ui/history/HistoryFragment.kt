package com.gws.auto.mobile.android.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentHistoryBinding
import com.gws.auto.mobile.android.ui.workflow.WorkflowViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels()
    private val workflowViewModel: WorkflowViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyAdapter = HistoryAdapter(
            onHeaderClick = { historyId ->
                viewModel.toggleItemExpanded(historyId.toLong())
            },
            onBookmarkClick = { headerItem ->
                viewModel.toggleBookmark(headerItem.history)
            }
        )
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.historyRecyclerView.adapter = historyAdapter

        setupItemTouchHelper()
        observeViewModel()
    }

    private fun setupItemTouchHelper() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                (historyAdapter.currentList[position] as? HistoryListItem.HeaderItem)?.let { headerItem ->
                    showDeleteConfirmationDialog(headerItem) {
                        historyAdapter.notifyItemChanged(position)
                    }
                }
            }

            override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                return if (viewHolder is HistoryAdapter.HeaderViewHolder) {
                    super.getSwipeDirs(recyclerView, viewHolder)
                } else {
                    0
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.historyRecyclerView)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                binding.progressBar.visibility = View.VISIBLE
                combine(viewModel.uiState, workflowViewModel.filteredItems) { history, workflows ->
                    Pair(history, workflows)
                }.collect { (history, workflows) ->
                    binding.progressBar.visibility = View.GONE
                    historyAdapter.submitList(history)

                    if (history.isEmpty()) {
                        binding.emptyViewHistory.visibility = View.VISIBLE
                        if (workflows.isEmpty()) {
                            binding.emptyViewHistory.text = getString(R.string.no_history_prompt_no_workflow)
                        } else {
                            binding.emptyViewHistory.text = getString(R.string.no_history_prompt_with_workflow)
                        }
                    } else {
                        binding.emptyViewHistory.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog(headerItem: HistoryListItem.HeaderItem, onCancel: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete History Item")
            .setMessage("Are you sure you want to delete this execution history item?")
            .setPositiveButton("Delete") { _, _ -> viewModel.deleteHistory(headerItem.history) }
            .setNegativeButton(R.string.cancel) { _, _ -> onCancel() }
            .setOnCancelListener { onCancel() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
