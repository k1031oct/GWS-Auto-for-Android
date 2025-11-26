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
    private var highlightColor: Int = Color.GRAY // Default

    fun setSelectedFile(file: File?) {
        selectedFile = file
        notifyDataSetChanged()
    }
    
    fun setHighlightColor(color: Int) {
        highlightColor = color
        notifyDataSetChanged()
    }

    inner class FileViewHolder(val binding: ListItemFileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(file: File, isSelected: Boolean) {
            binding.fileName.text = file.name
            val isFolder = file.mimeType == "application/vnd.google-apps.folder"

            binding.fileIcon.setImageResource(
                if (isFolder) R.drawable.ic_folder else R.drawable.ic_file
            )
            
            // Show chevron for folders to allow navigation
            binding.navigateIcon.visibility = if (isFolder) View.VISIBLE else View.GONE
            
            val context = binding.root.context
            val density = context.resources.displayMetrics.density
            val strokeWidthSelected = (4 * density).toInt()
            val strokeWidthUnselected = (1 * density).toInt()
            
            // Resolve outline color from theme
            val typedValue = android.util.TypedValue()
            context.theme.resolveAttribute(com.google.android.material.R.attr.colorOutline, typedValue, true)
            val outlineColor = typedValue.data
            
            // Set background based on selection
            if (isSelected) {
                binding.cardView.strokeColor = highlightColor
                binding.cardView.strokeWidth = strokeWidthSelected
            } else {
                binding.cardView.strokeColor = outlineColor
                binding.cardView.strokeWidth = strokeWidthUnselected
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
             if (file.mimeType == "application/vnd.google-apps.folder") {
                 onFolderNavigation(file)
             } else {
                 // Optional: Show hint or do nothing for file tap
             }
        }
        
        holder.itemView.setOnLongClickListener {
            onFileSelected(file)
            true
        }
        
        holder.binding.navigateIcon.setOnClickListener {
             if (file.mimeType == "application/vnd.google-apps.folder") {
                 onFolderNavigation(file)
             }
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
