package com.gws.auto.mobile.android.ui.settings.tag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gws.auto.mobile.android.databinding.FragmentTagManagementBinding
import com.gws.auto.mobile.android.domain.model.Tag
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import android.content.res.ColorStateList
import android.content.res.Configuration
import androidx.compose.ui.graphics.toArgb
import com.gws.auto.mobile.android.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TagManagementFragment : Fragment() {

    private var _binding: FragmentTagManagementBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TagManagementViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()
    private lateinit var tagAdapter: TagAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTagManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupViews()
        observeViewModel()
        observeTheme()
    }

    private fun setupRecyclerView() {
        tagAdapter = TagAdapter(
            onTagClick = { tag ->
                showEditTagDialog(tag)
            },
            onDeleteClick = { tag ->
                viewModel.deleteTag(tag)
            }
        )
        binding.tagRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.tagRecyclerView.adapter = tagAdapter
    }

    private fun setupViews() {
        binding.addTagButton.setOnClickListener {
            val tagName = binding.tagNameInput.text.toString()
            viewModel.addTag(tagName)
            binding.tagNameInput.text.clear()
        }
    }

    private fun showEditTagDialog(tag: Tag) {
        val input = android.widget.EditText(requireContext())
        input.setText(tag.name)
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("タグ名の編集")
            .setView(input)
            .setPositiveButton("保存") { _, _ ->
                val newName = input.text.toString()
                if (newName.isNotBlank()) {
                    viewModel.updateTag(tag, newName)
                }
            }
            .setNegativeButton("キャンセル", null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.tags
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { tags ->
                tagAdapter.submitList(tags)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeTheme() {
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
                binding.addTagButton.backgroundTintList = ColorStateList.valueOf(color.toArgb())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
