package com.gws.auto.mobile.android.ui.filepicker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.api.services.drive.model.File
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.ListItemFileBinding

class FileAdapter(private val onClick: (File) -> Unit) : ListAdapter<File, FileAdapter.FileViewHolder>(FileDiffCallback) {

    class FileViewHolder(private val binding: ListItemFileBinding, val onClick: (File) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(file: File) {
            binding.fileName.text = file.name
            if (file.mimeType == "application/vnd.google-apps.folder") {
                binding.fileIcon.setImageResource(R.drawable.ic_folder)
            } else {
                binding.fileIcon.setImageResource(R.drawable.ic_file)
            }
            itemView.setOnClickListener { onClick(file) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ListItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(getItem(position))
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
