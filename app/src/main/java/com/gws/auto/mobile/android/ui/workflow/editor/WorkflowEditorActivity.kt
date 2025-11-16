package com.gws.auto.mobile.android.ui.workflow.editor

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.view.DragEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
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

@AndroidEntryPoint
class WorkflowEditorActivity : AppCompatActivity(), ModuleListDialogFragment.ModuleListListener, ModuleParameterDialogFragment.ModuleParameterListener {

    private lateinit var binding: ActivityWorkflowEditorBinding
    private val viewModel: WorkflowEditorViewModel by viewModels()
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
        setupDragAndDrop()
        observeViewModel()

        binding.fabAddModule.setOnClickListener {
            val dialog = ModuleListDialogFragment()
            dialog.listener = this
            dialog.show(supportFragmentManager, "ModuleListDialog")
        }

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

    override fun onModuleSelected(moduleType: String) {
        val dialog = ModuleParameterDialogFragment.newInstance(moduleType)
        dialog.listener = this
        dialog.show(supportFragmentManager, "ModuleParameterDialog")
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
            "Core", "Gmail", "Sheets", "Custom"
        )

        folderAdapter = FolderAdapter(folders) { folder ->
            val modules = when (folder) {
                "Core" -> listOf(
                    Module(id = "", type = "DEFINE_VARIABLE", parameters = emptyMap()),
                    Module(id = "", type = "GET_RELATIVE_DATE", parameters = emptyMap()),
                    Module(id = "", type = "LOG_MESSAGE", parameters = emptyMap())
                )
                "Gmail" -> listOf(
                    Module(id = "", type = "CREATE_GMAIL_DRAFT", parameters = emptyMap()),
                    Module(id = "", type = "chat_post", parameters = emptyMap())
                )
                "Sheets" -> listOf(
                    Module(id = "", type = "DUPLICATE_SPREADSHEET", parameters = emptyMap()),
                    Module(id = "", type = "COPY_PASTE_SHEET_VALUES", parameters = emptyMap())
                )
                else -> emptyList()
            }
            libraryAdapter.updateModules(modules)
        }

        binding.folderRecyclerView.apply {
            adapter = folderAdapter
            layoutManager = LinearLayoutManager(this@WorkflowEditorActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupDragAndDrop() {
        binding.moduleRecyclerView.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val item: ClipData.Item = event.clipData.getItemAt(0)
                    val moduleType = item.text.toString()
                    val dialog = ModuleParameterDialogFragment.newInstance(moduleType)
                    dialog.listener = this
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
