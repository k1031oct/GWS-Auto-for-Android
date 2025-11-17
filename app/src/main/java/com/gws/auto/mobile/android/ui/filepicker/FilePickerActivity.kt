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

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = FileAdapter(
            onFileSelected = { file ->
                selectedFile = file
                adapter.setSelectedFile(file)
                binding.confirmButton.visibility = View.VISIBLE
            },
            onFolderNavigation = { folder ->
                viewModel.onFolderClicked(folder.id, folder.name)
                clearSelection()
            }
        )

        binding.fileList.adapter = adapter
        binding.fileList.layoutManager = LinearLayoutManager(this)

        binding.confirmButton.setOnClickListener { confirmSelection() }

        viewModel.files.observe(this) {
            adapter.submitList(it)
        }

        viewModel.currentFolderName.observe(this) {
            supportActionBar?.title = it
        }

        viewModel.error.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                viewModel.onErrorShown()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!viewModel.onUpClicked()) {
                    finish()
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
        binding.confirmButton.visibility = View.GONE
    }

    private fun confirmSelection() {
        selectedFile?.let { file ->
            val isFolder = file.mimeType == "application/vnd.google-apps.folder"
            val typeMatches = when (expectedType) {
                "folder" -> isFolder
                "file" -> !isFolder
                else -> true // No expectation, anything is fine
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
