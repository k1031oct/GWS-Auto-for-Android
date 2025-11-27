package com.gws.auto.mobile.android.ui.filepicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.api.services.drive.model.File
import com.gws.auto.mobile.android.databinding.ActivityFilePickerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilePickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilePickerBinding
    private val viewModel: FilePickerViewModel by viewModels()
    private lateinit var adapter: FileAdapter
    private var selectedFile: File? = null
    private var expectedType: String? = null // "file" or "folder"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expectedType = intent.getStringExtra("expectedType")
        viewModel.setExpectedType(expectedType)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = FileAdapter(
            onFileSelected = { file ->
                selectedFile = file
                adapter.setSelectedFile(file)
                binding.confirmButton.visibility = View.VISIBLE
                binding.selectCurrentFolderButton.visibility = View.GONE
            },
            onFolderNavigation = { folder ->
                viewModel.onFolderClicked(folder.id, folder.name)
                clearSelection()
            }
        )

        binding.fileList.adapter = adapter
        binding.fileList.layoutManager = LinearLayoutManager(this)

        binding.confirmButton.setOnClickListener { confirmSelection() }

        binding.selectCurrentFolderButton.setOnClickListener {
            confirmCurrentFolder()
        }

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.onSearchQueryChanged(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Optional: Live search
                // viewModel.onSearchQueryChanged(newText)
                return false
            }
        })
        
        binding.searchView.setOnCloseListener {
            viewModel.onSearchQueryChanged(null)
            false
        }

        viewModel.files.observe(this) {
            adapter.submitList(it)
        }

        viewModel.currentFolderName.observe(this) {
            supportActionBar?.title = it
            updateButtonsState()
        }

        viewModel.error.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                viewModel.onErrorShown()
            }
        }
        
        viewModel.theme.observe(this) { theme ->
            val mode = when (theme) {
                "Light" -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
                "Dark" -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
                else -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            if (androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode() != mode) {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(mode)
            }
        }

        viewModel.highlightColor.observe(this) { colorName ->
            val isDark = when (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) {
                android.content.res.Configuration.UI_MODE_NIGHT_YES -> true
                else -> false
            }
            
            // Color values from Color.kt
            val color = when (colorName) {
                "forest" -> if (isDark) 0xFF76FF03.toInt() else 0xFF9CCC65.toInt()
                "ocean" -> if (isDark) 0xFF00E5FF.toInt() else 0xFF4DD0E1.toInt()
                "sakura" -> if (isDark) 0xFFFF4081.toInt() else 0xFFF48FB1.toInt()
                "neon" -> if (isDark) 0xFFDFFF00.toInt() else 0xFFFFC66D.toInt()
                else -> if (isDark) 0xFFFFFFFF.toInt() else 0xFF000000.toInt() // Default
            }
            
            adapter.setHighlightColor(color)
            
            // Debug Toast
            Toast.makeText(this, "Theme: $colorName, Dark: $isDark", Toast.LENGTH_SHORT).show()
            
            // Optional: Update Toolbar title color to match highlight
            // binding.toolbar.setTitleTextColor(color)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!viewModel.onUpClicked()) {
                    finish()
                } else {
                    clearSelection()
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clearSelection() {
        selectedFile = null
        adapter.setSelectedFile(null)
        updateButtonsState()
    }
    
    private fun updateButtonsState() {
        if (selectedFile != null) {
            binding.confirmButton.visibility = View.VISIBLE
            binding.selectCurrentFolderButton.visibility = View.GONE
        } else {
            binding.confirmButton.visibility = View.GONE
            if (expectedType == "folder") {
                binding.selectCurrentFolderButton.visibility = View.VISIBLE
            } else {
                binding.selectCurrentFolderButton.visibility = View.GONE
            }
        }
    }

    private fun confirmCurrentFolder() {
        val folderId = viewModel.currentFolderId.value
        val folderName = viewModel.currentFolderName.value
        
        if (folderId != null && folderName != null) {
             val resultIntent = Intent()
             resultIntent.putExtra("fileId", folderId)
             resultIntent.putExtra("fileName", folderName)
             setResult(Activity.RESULT_OK, resultIntent)
             finish()
        }
    }

    private fun confirmSelection() {
        selectedFile?.let { file ->
            val isFolder = file.mimeType == "application/vnd.google-apps.folder"
            // If we are picking a file, we might select a folder to navigate, but if we click confirm on a folder...
            // If expectedType is "file", we shouldn't be able to confirm a folder, unless we want to allow it?
            // Usually if I select a folder in a file picker, I want to open it.
            // But here I have a separate navigation click.
            // If I select a folder (highlight it) and click confirm, it means I want to pick that folder.
            
            val typeMatches = when (expectedType) {
                "folder" -> isFolder
                "file" -> !isFolder
                else -> true 
            }

            if (typeMatches) {
                val resultIntent = Intent()
                resultIntent.putExtra("fileId", file.id)
                resultIntent.putExtra("fileName", file.name)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Invalid Selection")
                    .setMessage("You have selected a ${if (isFolder) "folder" else "file"}, but a ${expectedType} was expected.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
}
