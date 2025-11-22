package com.gws.auto.mobile.android.ui.workflow.editor

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.view.DragEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.ActivityWorkflowEditorBinding
import com.gws.auto.mobile.android.domain.model.Module
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import android.graphics.drawable.ColorDrawable
import android.content.res.ColorStateList
import android.graphics.Color
import android.content.res.Configuration
import com.gws.auto.mobile.android.ui.theme.*
import androidx.compose.ui.graphics.toArgb
import com.google.android.material.chip.Chip
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

@AndroidEntryPoint
class WorkflowEditorActivity : AppCompatActivity(), ModuleParameterDialogFragment.ModuleParameterListener {

    private lateinit var binding: ActivityWorkflowEditorBinding
    private val viewModel: WorkflowEditorViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()
    private lateinit var moduleAdapter: ModuleAdapter
    private lateinit var libraryAdapter: ModuleLibraryAdapter
    private lateinit var folderAdapter: FolderAdapter
    private var isEditingEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkflowEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, v.paddingBottom)
            insets
        }

        val workflowId = intent.getStringExtra("workflowId")
        if (workflowId != null) {
            viewModel.loadWorkflow(workflowId)
        } else {
            isEditingEnabled = true
            updateEditState()
        }

        setupRecyclerView()
        setupLibraryRecyclerView()
        setupFolderRecyclerView()
        setupFolderRecyclerView()
        setupTagSection()
        setupDragAndDrop()
        observeViewModel()
        observeViewModel()
        observeTags()
        observeTheme()

        binding.fabAddModule.visibility = View.GONE

        binding.cancelButton.setOnClickListener { finish() }
        binding.saveButton.setOnClickListener { saveWorkflow() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.workflow_editor_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.action_edit -> {
                isEditingEnabled = !isEditingEnabled
                updateEditState()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onModuleParametersSet(module: Module) {
        viewModel.addModule(module.copy(id = UUID.randomUUID().toString()))
    }

    private fun updateEditState() {
        binding.workflowNameEditor.isFocusable = isEditingEnabled
        binding.workflowNameEditor.isFocusableInTouchMode = isEditingEnabled
        binding.workflowDescriptionEditor.isFocusable = isEditingEnabled
        binding.workflowDescriptionEditor.isFocusableInTouchMode = isEditingEnabled
        if (isEditingEnabled) {
            binding.workflowNameEditor.requestFocus()
        }
    }

    private fun setupRecyclerView() {
        moduleAdapter = ModuleAdapter(
            onEditClicked = { module ->
                val dialog = ModuleSettingsDialogFragment(module)
                dialog.show(supportFragmentManager, "ModuleSettingsDialog")
            },
            onRemoveClicked = { module ->
                viewModel.removeModule(module)
            },
            onRunModuleClicked = { module ->
                viewModel.runSingleModule(module)
            },
            onModuleEnabledChanged = { module, isEnabled ->
                viewModel.setModuleEnabled(module.id, isEnabled)
            }
        )
        binding.moduleRecyclerView.apply {
            adapter = moduleAdapter
            layoutManager = LinearLayoutManager(this@WorkflowEditorActivity)
        }
    }

    private fun setupLibraryRecyclerView() {
        libraryAdapter = ModuleLibraryAdapter(emptyList()) { module, view ->
            val item = ClipData.Item(module.type)
            val dragData = ClipData(
                module.type,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item
            )
            val myShadow = View.DragShadowBuilder(view)
            view.startDragAndDrop(dragData, myShadow, null, 0)
            true
        }
        binding.libraryRecyclerView.apply {
            adapter = libraryAdapter
            layoutManager = LinearLayoutManager(this@WorkflowEditorActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupFolderRecyclerView() {
        val folders = listOf(
            "Input", "Output", "Process", "Core", "Gmail", "Drive", "Sheets", "Calendar", "Custom"
        )

        folderAdapter = FolderAdapter(folders) { folder ->
            val modules = when (folder) {
                "Input" -> listOf(
                    Module(id = "", type = "FILE_PICKER", parameters = emptyMap()),
                    Module(id = "", type = "GET_CLIPBOARD", parameters = emptyMap())
                )
                "Output" -> listOf(
                    Module(id = "", type = "SHOW_TOAST", parameters = emptyMap()),
                    Module(id = "", type = "LOG_MESSAGE", parameters = emptyMap()),
                    Module(id = "", type = "SET_CLIPBOARD", parameters = emptyMap())
                )
                "Process" -> listOf(
                    Module(id = "", type = "CALCULATE", parameters = emptyMap()),
                    Module(id = "", type = "HTTP_REQUEST", parameters = emptyMap())
                )
                "Core" -> listOf(
                    Module(id = "", type = "DEFINE_VARIABLE", parameters = emptyMap()),
                    Module(id = "", type = "GET_RELATIVE_DATE", parameters = emptyMap())
                )
                "Gmail" -> listOf(
                    Module(id = "", type = "CREATE_GMAIL_DRAFT", parameters = emptyMap()),
                    Module(id = "", type = "gmail_send_email", parameters = emptyMap()),
                    Module(id = "", type = "chat_post", parameters = emptyMap())
                )
                "Drive" -> listOf(
                    Module(id = "", type = "drive_create_folder", parameters = emptyMap()),
                    Module(id = "", type = "drive_copy_file", parameters = emptyMap()),
                    Module(id = "", type = "drive_move_file", parameters = emptyMap())
                )
                "Sheets" -> listOf(
                    Module(id = "", type = "DUPLICATE_SPREADSHEET", parameters = emptyMap()),
                    Module(id = "", type = "COPY_PASTE_SHEET_VALUES", parameters = emptyMap()),
                    Module(id = "", type = "sheets_create_new", parameters = emptyMap()),
                    Module(id = "", type = "sheets_set_value", parameters = emptyMap()),
                    Module(id = "", type = "sheets_append_row", parameters = emptyMap()),
                    Module(id = "", type = "sheets_clear_values", parameters = emptyMap())
                )
                "Calendar" -> listOf(
                    Module(id = "", type = "calendar_create_event", parameters = emptyMap())
                )
                else -> emptyList()
            }
            libraryAdapter.updateModules(modules)
        }

        binding.folderRecyclerView.apply {
            adapter = folderAdapter
            layoutManager = LinearLayoutManager(this@WorkflowEditorActivity, LinearLayoutManager.HORIZONTAL, false)
        }
        binding.folderRecyclerView.apply {
            adapter = folderAdapter
            layoutManager = LinearLayoutManager(this@WorkflowEditorActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupTagSection() {
        binding.addTagChip.setOnClickListener {
            showAddTagDialog()
        }
    }

    private fun showAddTagDialog() {
        val availableTags = viewModel.availableTags.value.map { it.name }
        val selectedTags = viewModel.selectedTags.value
        val suggestions = availableTags.filter { !selectedTags.contains(it) }

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
        val textView = AutoCompleteTextView(this).apply {
            setAdapter(adapter)
            threshold = 1
            hint = getString(R.string.tag_name_hint)
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.add_tag_title)
            .setView(textView)
            .setPositiveButton(R.string.add) { _, _ ->
                val tagName = textView.text.toString().trim()
                if (tagName.isNotEmpty()) {
                    viewModel.addTagToWorkflow(tagName)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun observeTags() {
        lifecycleScope.launch {
            viewModel.selectedTags.collectLatest { tags ->
                updateTagChips(tags)
            }
        }
    }

    private fun updateTagChips(tags: List<String>) {
        // Remove all chips except the "Add Tag" chip (which is the last one in XML, but let's check ID)
        val chipGroup = binding.tagChipGroup
        val addTagChip = binding.addTagChip
        
        // Remove all views except addTagChip
        val viewsToRemove = mutableListOf<View>()
        for (i in 0 until chipGroup.childCount) {
            val child = chipGroup.getChildAt(i)
            if (child.id != R.id.add_tag_chip) {
                viewsToRemove.add(child)
            }
        }
        viewsToRemove.forEach { chipGroup.removeView(it) }

        // Add new chips before the addTagChip
        tags.forEach { tagName ->
            val chip = Chip(this).apply {
                text = tagName
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    viewModel.removeTagFromWorkflow(tagName)
                }
            }
            // Add at index 0 to keep "Add Tag" at the end (assuming it's currently at index 0 if empty, or last)
            // Actually, simply adding view adds to the end. We want "Add Tag" to be last.
            // So we add before "Add Tag".
            chipGroup.addView(chip, chipGroup.indexOfChild(addTagChip))
        }
    }

    private fun setupDragAndDrop() {
        binding.moduleRecyclerView.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val item: ClipData.Item = event.clipData.getItemAt(0)
                    val moduleType = item.text.toString()
                    val dialog = ModuleParameterDialogFragment().apply {
                        arguments = Bundle().apply {
                            putString(ModuleParameterDialogFragment.ARG_MODULE_TYPE, moduleType)
                        }
                        listener = this@WorkflowEditorActivity
                    }
                    dialog.show(supportFragmentManager, "ModuleParameterDialog")
                    true
                }
                else -> true
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.workflow.collectLatest { workflow ->
                if (workflow != null) {
                    binding.workflowNameEditor.setText(workflow.name)
                    binding.workflowDescriptionEditor.setText(workflow.description)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.modules.collectLatest { modules ->
                moduleAdapter.submitList(modules)
            }
        }
        lifecycleScope.launch {
            viewModel.singleModuleExecutionResult.collectLatest { result ->
                if (result != null) {
                    AlertDialog.Builder(this@WorkflowEditorActivity)
                        .setTitle("Module Execution Result")
                        .setMessage("Success: ${result.isSuccess}\nOutput: ${result.outputMessage ?: "N/A"}")
                        .setPositiveButton("OK") { _, _ ->
                            viewModel.clearSingleModuleExecutionResult()
                        }
                        .setOnDismissListener { 
                            viewModel.clearSingleModuleExecutionResult()
                        }
                        .show()
                }
            }
        }
    }

    private fun observeTheme() {
        lifecycleScope.launch {
            themeViewModel.highlightColor.collectLatest { colorName ->
                applyHighlightColor(colorName)
            }
        }
    }

    private fun applyHighlightColor(colorName: String) {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkTheme = nightModeFlags == Configuration.UI_MODE_NIGHT_YES

        val color = when (colorName) {
            "forest" -> if (isDarkTheme) ForestPrimaryDark else ForestPrimaryLight
            "ocean" -> if (isDarkTheme) OceanPrimaryDark else OceanPrimaryLight
            "sakura" -> if (isDarkTheme) SakuraPrimaryDark else SakuraPrimaryLight
            else -> if (isDarkTheme) DefaultPrimaryDark else DefaultPrimaryLight
        }

        val colorInt = color.toArgb()
        val colorStateList = ColorStateList.valueOf(colorInt)

        // Apply to ActionBar
        supportActionBar?.setBackgroundDrawable(ColorDrawable(colorInt))

        // Apply to Buttons
        binding.saveButton.backgroundTintList = colorStateList
        binding.fabAddModule.backgroundTintList = colorStateList
        
        // Apply to TextInputLayouts (input field borders when focused)
        binding.workflowNameLayout.setBoxStrokeColorStateList(colorStateList)
        binding.workflowDescriptionLayout.setBoxStrokeColorStateList(colorStateList)
        binding.workflowNameLayout.setHintTextColor(colorStateList)
        binding.workflowDescriptionLayout.setHintTextColor(colorStateList)
        
        // Reset cursor color to default (white in dark mode, black in light mode)
        val defaultTextColor = if (isDarkTheme) android.graphics.Color.WHITE else android.graphics.Color.BLACK
        binding.workflowNameEditor.setTextColor(defaultTextColor)
        binding.workflowDescriptionEditor.setTextColor(defaultTextColor)
        
        // Apply to Cancel Button (text color)
        binding.cancelButton.setTextColor(colorInt)
        
        // Apply to ModuleAdapter (icons and switches)
        moduleAdapter.highlightColor = colorInt
    }

    private fun saveWorkflow() {
        val name = binding.workflowNameEditor.text.toString()
        val description = binding.workflowDescriptionEditor.text.toString()

        if (name.isBlank()) {
            binding.workflowNameEditor.error = "Workflow name is required."
            return
        }

        lifecycleScope.launch {
            try {
                viewModel.saveWorkflow(name, description)
                Timber.i("Workflow '$name' saved successfully.")
                finish() // Close the editor on successful save
            } catch (e: Exception) {
                Timber.e(e, "Failed to save workflow.")
                // Optionally, show a toast or a snackbar to the user
            }
        }
    }
}
