package com.gws.auto.mobile.android.ui.filepicker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.api.services.drive.model.File
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.ListItemFileBinding

class FileAdapter(
    private val onFileSelected: (File) -> Unit,
    private val onFolderNavigation: (File) -> Unit
) : ListAdapter<File, FileAdapter.FileViewHolder>(FileDiffCallback) {

    private var selectedFile: File? = null
    private var lastClickTime = 0L

    companion object {
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 // milliseconds
    }

    fun setSelectedFile(file: File?) {
        selectedFile = file
        notifyDataSetChanged()
    }

    class FileViewHolder(val binding: ListItemFileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(file: File, isSelected: Boolean) {
            binding.fileName.text = file.name
            val isFolder = file.mimeType == "application/vnd.google-apps.folder"

            binding.fileIcon.setImageResource(
                if (isFolder) R.drawable.ic_folder else R.drawable.ic_file
            )
            binding.navigateIcon.visibility = if (isFolder) View.VISIBLE else View.GONE

            if (isSelected) {
                binding.root.setBackgroundColor(Color.LTGRAY)
            } else {
                binding.root.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ListItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = getItem(position)
        holder.bind(file, file.id == selectedFile?.id)
        holder.itemView.setOnClickListener {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA && file.mimeType == "application/vnd.google-apps.folder") {
                onFolderNavigation(file)
            } else {
                onFileSelected(file)
            }
            lastClickTime = clickTime
        }
        holder.binding.navigateIcon.setOnClickListener { 
            if (file.mimeType == "application/vnd.google-apps.folder") onFolderNavigation(file) 
        }
    }
}

object FileDiffCallback : DiffUtil.ItemCallback<File>() {
    override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem == newItem
    }
}
