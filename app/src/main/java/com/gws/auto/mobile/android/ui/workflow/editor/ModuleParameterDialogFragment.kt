package com.gws.auto.mobile.android.ui.workflow.editor

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.ui.filepicker.FilePickerActivity

class ModuleParameterDialogFragment : DialogFragment() {

    interface ModuleParameterListener {
        fun onModuleParametersSet(module: Module)
    }

    var listener: ModuleParameterListener? = null
    private val moduleParameters = mutableMapOf<String, String>()
    private lateinit var moduleType: String

    companion object {
        private const val ARG_MODULE_TYPE = "module_type"
        private const val REQUEST_CODE_PICK_FILE = 100

        fun newInstance(moduleType: String): ModuleParameterDialogFragment {
            val args = Bundle()
            args.putString(ARG_MODULE_TYPE, moduleType)
            val fragment = ModuleParameterDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        moduleType = requireArguments().getString(ARG_MODULE_TYPE)!!

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
        }

        val editTexts = mutableMapOf<String, EditText>()

        val filePickerParams = setOf("sourceFileId", "destFolderId", "sourceFileUrl", "destinationFolderUrl", "spreadsheetUrl", "targetUrl", "targetFolderId")

        getRequiredParameters(moduleType).forEach { paramName ->
            if (filePickerParams.contains(paramName)) {
                val button = Button(requireContext()).apply {
                    text = "Select File"
                    setOnClickListener {
                        val intent = Intent(requireContext(), FilePickerActivity::class.java)
                        startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
                        // We need a way to know which parameter this file picker is for.
                        // For simplicity, we'll store it in a member variable.
                        (this@ModuleParameterDialogFragment as Any).javaClass.getDeclaredField("currentFilePickerParam").apply {
                            isAccessible = true
                            set(this@ModuleParameterDialogFragment, paramName)
                        }
                    }
                }
                layout.addView(button)
            } else {
                val editText = EditText(requireContext()).apply { hint = paramName.replaceFirstChar { it.uppercase() } }
                editTexts[paramName] = editText
                layout.addView(editText)
            }
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Set parameters for $moduleType")
            .setView(layout)
            .setPositiveButton("OK") { _, _ ->
                editTexts.forEach { (key, editText) ->
                    moduleParameters[key] = editText.text.toString()
                }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == Activity.RESULT_OK) {
            val fileId = data?.getStringExtra("fileId")
            val fileName = data?.getStringExtra("fileName")
            val paramName = (this as Any).javaClass.getDeclaredField("currentFilePickerParam").apply {
                isAccessible = true
            }.get(this) as String
            moduleParameters[paramName] = fileId ?: ""
            // Update the button text to show the selected file name
        }
    }

    private fun getRequiredParameters(moduleType: String): List<String> {
        return when (moduleType) {
            "LOG_MESSAGE" -> listOf("message")
            "chat_post" -> listOf("webhookUrl", "message")
            "drive_create_folder" -> listOf("parentFolderId", "newFolderName")
            "drive_copy_file" -> listOf("sourceFileId", "destFolderId", "newFileName")
            "drive_move_file" -> listOf("sourceFileUrl", "destinationFolderUrl")
            "DUPLICATE_SPREADSHEET" -> listOf("sourceSpreadsheetId", "newSpreadsheetName", "targetFolderId")
            "gmail_send_email" -> listOf("to", "subject", "body", "cc", "bcc")
            "sheets_create_new" -> listOf("newFileName", "destFolderId")
            "sheets_set_value" -> listOf("targetUrl", "targetSheet", "targetCell", "valueToSet")
            "sheets_append_row" -> listOf("spreadsheetUrl", "sheetName", "rowData")
            "sheets_clear_values" -> listOf("targetUrl", "targetSheet", "targetRange")
            "calendar_create_event" -> listOf("calendarId", "title", "startTime", "endTime")
            else -> emptyList()
        }
    }

    private var currentFilePickerParam: String = ""
}
