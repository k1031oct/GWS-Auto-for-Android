package com.gws.auto.mobile.android.ui.workflow.editor

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.gws.auto.mobile.android.R

class ModuleListDialogFragment : DialogFragment() {

    interface ModuleListListener {
        fun onModuleSelected(moduleType: String)
    }

    var listener: ModuleListListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val moduleDisplayNames = resources.getStringArray(R.array.module_display_names)
        val moduleKeys = resources.getStringArray(R.array.module_keys)

        return AlertDialog.Builder(requireContext())
            .setTitle("Select a module")
            .setItems(moduleDisplayNames) { _, which ->
                listener?.onModuleSelected(moduleKeys[which])
            }
            .create()
    }
}
