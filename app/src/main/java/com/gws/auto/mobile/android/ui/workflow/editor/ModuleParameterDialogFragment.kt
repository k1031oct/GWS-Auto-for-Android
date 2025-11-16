package com.gws.auto.mobile.android.ui.workflow.editor

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.gws.auto.mobile.android.domain.model.Module

class ModuleParameterDialogFragment : DialogFragment() {

    interface ModuleParameterListener {
        fun onModuleParametersSet(module: Module)
    }

    var listener: ModuleParameterListener? = null

    companion object {
        private const val ARG_MODULE_TYPE = "module_type"

        fun newInstance(moduleType: String): ModuleParameterDialogFragment {
            val args = Bundle()
            args.putString(ARG_MODULE_TYPE, moduleType)
            val fragment = ModuleParameterDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val moduleType = requireArguments().getString(ARG_MODULE_TYPE)!!

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
        }

        val parameters = mutableMapOf<String, EditText>()

        when (moduleType) {
            "LOG_MESSAGE" -> {
                parameters["message"] = EditText(requireContext()).apply { hint = "Message" }
            }
            "chat_post" -> {
                parameters["webhookUrl"] = EditText(requireContext()).apply { hint = "Webhook URL" }
                parameters["message"] = EditText(requireContext()).apply { hint = "Message" }
            }
            "drive_create_folder" -> {
                parameters["parentFolderId"] = EditText(requireContext()).apply { hint = "Parent Folder ID (Optional)" }
                parameters["newFolderName"] = EditText(requireContext()).apply { hint = "New Folder Name" }
            }
            "drive_copy_file" -> {
                parameters["sourceFileId"] = EditText(requireContext()).apply { hint = "Source File ID" }
                parameters["destFolderId"] = EditText(requireContext()).apply { hint = "Destination Folder ID" }
                parameters["newFileName"] = EditText(requireContext()).apply { hint = "New File Name" }
            }
            "drive_move_file" -> {
                parameters["sourceFileUrl"] = EditText(requireContext()).apply { hint = "Source File URL" }
                parameters["destinationFolderUrl"] = EditText(requireContext()).apply { hint = "Destination Folder URL" }
            }
            "gmail_send_email" -> {
                parameters["to"] = EditText(requireContext()).apply { hint = "To" }
                parameters["subject"] = EditText(requireContext()).apply { hint = "Subject" }
                parameters["body"] = EditText(requireContext()).apply { hint = "Body" }
                parameters["cc"] = EditText(requireContext()).apply { hint = "CC (Optional)" }
                parameters["bcc"] = EditText(requireContext()).apply { hint = "BCC (Optional)" }
            }
            "sheets_create_new" -> {
                parameters["newFileName"] = EditText(requireContext()).apply { hint = "New File Name" }
                parameters["destFolderId"] = EditText(requireContext()).apply { hint = "Destination Folder ID (Optional)" }
            }
            "sheets_set_value" -> {
                parameters["targetUrl"] = EditText(requireContext()).apply { hint = "Spreadsheet URL" }
                parameters["targetSheet"] = EditText(requireContext()).apply { hint = "Sheet Name" }
                parameters["targetCell"] = EditText(requireContext()).apply { hint = "Cell (e.g., A1)" }
                parameters["valueToSet"] = EditText(requireContext()).apply { hint = "Value or Formula" }
            }
            "sheets_append_row" -> {
                parameters["spreadsheetUrl"] = EditText(requireContext()).apply { hint = "Spreadsheet URL" }
                parameters["sheetName"] = EditText(requireContext()).apply { hint = "Sheet Name (Optional)" }
                parameters["rowData"] = EditText(requireContext()).apply { hint = "Row Data (comma-separated)" }
            }
            "sheets_clear_values" -> {
                parameters["targetUrl"] = EditText(requireContext()).apply { hint = "Spreadsheet URL" }
                parameters["targetSheet"] = EditText(requireContext()).apply { hint = "Sheet Name" }
                parameters["targetRange"] = EditText(requireContext()).apply { hint = "Range (e.g., A1:C5)" }
            }
            "calendar_create_event" -> {
                parameters["calendarId"] = EditText(requireContext()).apply { hint = "Calendar ID (Optional, default: primary)" }
                parameters["title"] = EditText(requireContext()).apply { hint = "Title" }
                parameters["startTime"] = EditText(requireContext()).apply { hint = "Start Time (yyyy/MM/dd HH:mm)" }
                parameters["endTime"] = EditText(requireContext()).apply { hint = "End Time (yyyy/MM/dd HH:mm)" }
            }
        }

        parameters.values.forEach { layout.addView(it) }

        return AlertDialog.Builder(requireContext())
            .setTitle("Set parameters for $moduleType")
            .setView(layout)
            .setPositiveButton("OK") { _, _ ->
                val moduleParameters = parameters.mapValues { it.value.text.toString() }
                val module = Module(
                    id = "", // Will be set later
                    type = moduleType,
                    parameters = moduleParameters
                )
                listener?.onModuleParametersSet(module)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}
