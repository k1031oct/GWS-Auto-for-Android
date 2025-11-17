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

    private val filePickerRequests = mutableMapOf<Int, Triple<String, String, Button>>() // requestCode -> (paramName, expectedType, button)
    private var nextRequestCode = 100

    companion object {
        private const val ARG_MODULE_TYPE = "module_type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moduleType = requireArguments().getString(ARG_MODULE_TYPE)!!
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
        }

        val editTexts = mutableMapOf<String, EditText>()
        val filePickerParams = getFilePickerParameters(moduleType)

        getRequiredParameters(moduleType).forEach { paramName ->
            if (filePickerParams.containsKey(paramName)) {
                val expectedType = filePickerParams[paramName]!!
                val button = Button(requireContext()).apply {
                    text = "Select: $paramName ($expectedType)"
                    setOnClickListener {
                        val requestCode = nextRequestCode++
                        filePickerRequests[requestCode] = Triple(paramName, expectedType, this)
                        val intent = Intent(requireContext(), FilePickerActivity::class.java).apply {
                            putExtra("expectedType", expectedType)
                        }
                        startActivityForResult(intent, requestCode)
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
        if (resultCode == Activity.RESULT_OK && filePickerRequests.containsKey(requestCode)) {
            val fileId = data?.getStringExtra("fileId")
            val fileName = data?.getStringExtra("fileName")
            val (paramName, _, button) = filePickerRequests[requestCode]!!

            if (fileId != null && fileName != null) {
                moduleParameters[paramName] = fileId
                button.text = "$paramName: $fileName"
            }
        }
    }

    private fun getRequiredParameters(moduleType: String): List<String> {
        // Returns all possible parameters for a module
        return getFilePickerParameters(moduleType).keys.toList() + getManualParameters(moduleType)
    }

    private fun getFilePickerParameters(moduleType: String): Map<String, String> {
        return when (moduleType) {
            "drive_copy_file" -> mapOf("sourceFileId" to "file", "destFolderId" to "folder")
            "drive_create_folder" -> mapOf("parentFolderId" to "folder")
            "drive_move_file" -> mapOf("sourceFileUrl" to "file", "destinationFolderUrl" to "folder")
            "DUPLICATE_SPREADSHEET" -> mapOf("sourceSpreadsheetId" to "file", "targetFolderId" to "folder")
            "sheets_create_new" -> mapOf("destFolderId" to "folder")
            "sheets_set_value", "sheets_append_row", "sheets_clear_values" -> mapOf("targetUrl" to "file")
            else -> emptyMap()
        }
    }

    private fun getManualParameters(moduleType: String): List<String> {
        return when (moduleType) {
            "LOG_MESSAGE" -> listOf("message")
            "chat_post" -> listOf("webhookUrl", "message")
            "drive_create_folder" -> listOf("newFolderName")
            "drive_copy_file" -> listOf("newFileName")
            "DUPLICATE_SPREADSHEET" -> listOf("newSpreadsheetName")
            "gmail_send_email" -> listOf("to", "subject", "body", "cc", "bcc")
            "sheets_create_new" -> listOf("newFileName")
            "sheets_set_value" -> listOf("targetSheet", "targetCell", "valueToSet")
            "sheets_append_row" -> listOf("sheetName", "rowData")
            "sheets_clear_values" -> listOf("targetSheet", "targetRange")
            "calendar_create_event" -> listOf("calendarId", "title", "startTime", "endTime")
            else -> emptyList()
        }
    }
}
