package com.gws.auto.mobile.android.ui.filepicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gws.auto.mobile.android.databinding.ActivityFilePickerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilePickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilePickerBinding
    private val viewModel: FilePickerViewModel by viewModels()
    private lateinit var adapter: FileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = FileAdapter { file ->
            if (file.mimeType == "application/vnd.google-apps.folder") {
                viewModel.onFolderClicked(file.id, file.name)
            } else {
                val resultIntent = Intent()
                resultIntent.putExtra("fileId", file.id)
                resultIntent.putExtra("fileName", file.name)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

        binding.fileList.adapter = adapter
        binding.fileList.layoutManager = LinearLayoutManager(this)

        viewModel.files.observe(this) {
            adapter.submitList(it)
        }

        viewModel.currentFolderName.observe(this) {
            supportActionBar?.title = it
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                viewModel.onUpClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
