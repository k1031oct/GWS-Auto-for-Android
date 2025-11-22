package com.gws.auto.mobile.android.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentSearchBinding
import com.gws.auto.mobile.android.domain.model.DisplayTag
import com.gws.auto.mobile.android.domain.model.FilterTag
import com.gws.auto.mobile.android.domain.model.FilterType
import com.gws.auto.mobile.android.domain.model.Tag
import com.gws.auto.mobile.android.ui.MainSharedViewModel
import com.gws.auto.mobile.android.ui.history.HistoryViewModel
import com.gws.auto.mobile.android.ui.workflow.WorkflowViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.gws.auto.mobile.android.ui.theme.*
import android.content.res.Configuration
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private val mainSharedViewModel: MainSharedViewModel by viewModels({ requireActivity() })
    private val workflowViewModel: WorkflowViewModel by viewModels({ requireActivity() })
    private val historyViewModel: HistoryViewModel by viewModels({ requireActivity() })
    private val themeViewModel: ThemeViewModel by viewModels()

    private lateinit var tagAdapter: TagAdapter
    private lateinit var historyAdapter: SearchHistoryAdapter
    private lateinit var suggestionAdapter: SearchSuggestionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        observeViewModel()
        setupClickListeners()
    }

    private fun setupRecyclerViews() {
        tagAdapter = TagAdapter(
            onTagClicked = { tag -> handleTagClick(tag) },
            onTagLongClicked = { tag ->
                showDeleteConfirmationDialog(getString(R.string.delete_tag_confirmation), tag.name) { viewModel.deleteTag(tag) }
            },
            onAddTagClicked = { showAddTagDialog() }
        )
        binding.tagRecyclerView.adapter = tagAdapter

        historyAdapter = SearchHistoryAdapter(
            onHistoryItemClicked = { history ->
                mainSharedViewModel.setSearchQuery(history.query)
                requireActivity().findViewById<View>(R.id.search_fragment_container).visibility = View.GONE
            },
            onHistoryItemLongClicked = { history ->
                showDeleteConfirmationDialog(getString(R.string.delete_history_confirmation), history.query) { viewModel.deleteSearchHistoryItem(history.query) }
            }
        )
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.historyRecyclerView.adapter = historyAdapter

        suggestionAdapter = SearchSuggestionAdapter { suggestion ->
            val query = when (suggestion) {
                is SearchSuggestion.WorkflowItem -> suggestion.workflow.name
                is SearchSuggestion.FolderItem -> suggestion.folder.name
                is SearchSuggestion.TagItem -> suggestion.tag.name
            }
            mainSharedViewModel.setSearchQuery(query)
            viewModel.addSearchHistory(query)
            requireActivity().findViewById<View>(R.id.search_fragment_container).visibility = View.GONE
        }
    }

    private fun handleTagClick(tag: DisplayTag) {
        when (tag) {
            is FilterTag -> {
                when (tag.type) {
                    FilterType.FAVORITE -> workflowViewModel.toggleFavoriteFilter()
                    FilterType.BOOKMARK -> historyViewModel.toggleBookmarkFilter()
                }
            }
            is Tag -> {
                mainSharedViewModel.setSearchQuery(tag.name)
                viewModel.addSearchHistory(tag.name)
                requireActivity().findViewById<View>(R.id.search_fragment_container).visibility = View.GONE
            }
        }
    }

    private fun observeViewModel() {
        val favoriteFilterTag = FilterTag("★ Favorites", FilterType.FAVORITE)
        val bookmarkFilterTag = FilterTag("🔖 Bookmarks", FilterType.BOOKMARK)

        combine(mainSharedViewModel.currentPage, viewModel.tags) { page, tags ->
            val filterTags = when (page) {
                0 -> listOf(favoriteFilterTag)
                2 -> listOf(bookmarkFilterTag)
                else -> emptyList()
            }
            filterTags + tags
        }.onEach { combinedTags ->
            tagAdapter.submitList(combinedTags)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.searchHistory.onEach { history ->
            historyAdapter.submitList(history)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.searchQuery.collectLatest { query ->
                viewModel.setSearchQuery(query)
                if (query.isBlank()) {
                    binding.historyRecyclerView.adapter = historyAdapter
                    binding.clearHistoryButton.visibility = View.VISIBLE
                } else {
                    binding.historyRecyclerView.adapter = suggestionAdapter
                    binding.clearHistoryButton.visibility = View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchSuggestions.collectLatest { suggestions ->
                suggestionAdapter.submitList(suggestions)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            themeViewModel.highlightColor.collectLatest { colorName ->
                val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                val isDarkTheme = nightModeFlags == Configuration.UI_MODE_NIGHT_YES

                val color = when (colorName) {
                    "forest" -> if (isDarkTheme) ForestPrimaryDark else ForestPrimaryLight
                    "ocean" -> if (isDarkTheme) OceanPrimaryDark else OceanPrimaryLight
                    "sakura" -> if (isDarkTheme) SakuraPrimaryDark else SakuraPrimaryLight
                    else -> if (isDarkTheme) DefaultPrimaryDark else DefaultPrimaryLight
                }
                tagAdapter.highlightColor = color.toArgb()
            }
        }
    }

    private fun setupClickListeners() {
        binding.clearHistoryButton.setOnClickListener {
            showDeleteConfirmationDialog(getString(R.string.clear_search_history_confirmation), null) { viewModel.clearSearchHistory() }
        }
    }

    private fun showAddTagDialog() {
        val editText = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_tag_title)
            .setView(editText)
            .setPositiveButton(R.string.add) { _, _ ->
                val tagName = editText.text.toString()
                if (tagName.isNotBlank()) {
                    viewModel.addTag(tagName)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showDeleteConfirmationDialog(message: String, itemName: String?, onConfirm: () -> Unit) {
        val finalMessage = if (itemName != null) "$message \"$itemName\"?" else message
        AlertDialog.Builder(requireContext())
            .setMessage(finalMessage)
            .setPositiveButton(R.string.delete) { _, _ -> onConfirm() }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
