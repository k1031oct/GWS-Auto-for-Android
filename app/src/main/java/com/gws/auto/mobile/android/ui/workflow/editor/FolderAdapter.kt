package com.gws.auto.mobile.android.ui.workflow.editor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.databinding.ItemFolderBinding

class FolderAdapter(
    private val folders: List<String>,
    private val onFolderClicked: (String) -> Unit
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = ItemFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        holder.bind(folder)
        holder.itemView.setOnClickListener {
            onFolderClicked(folder)
        }
    }

    override fun getItemCount(): Int = folders.size

    class FolderViewHolder(private val binding: ItemFolderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(folder: String) {
            binding.folderName.text = folder
        }
    }
}
