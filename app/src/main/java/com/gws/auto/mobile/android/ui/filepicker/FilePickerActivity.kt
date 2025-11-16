package com.gws.auto.mobile.android.ui.filepicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gws.auto.mobile.android.databinding.ActivityFilePickerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
            val resultIntent = Intent()
            resultIntent.putExtra("fileId", file.id)
            resultIntent.putExtra("fileName", file.name)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        binding.fileList.adapter = adapter
        binding.fileList.layoutManager = LinearLayoutManager(this)

        viewModel.files.observe(this) {
            adapter.submitList(it)
        }

        binding.upButton.setOnClickListener {
            viewModel.onUpClicked()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
