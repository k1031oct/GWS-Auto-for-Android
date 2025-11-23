package com.gws.auto.mobile.android.ui.workflow.editor

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.ui.filepicker.FilePickerActivity
import com.gws.auto.mobile.android.R

class ModuleParameterDialogFragment : DialogFragment() {

    interface ModuleParameterListener {
        fun onModuleParametersSet(module: Module)
    }

    var listener: ModuleParameterListener? = null
    private val moduleParameters = mutableMapOf<String, String>()
    private lateinit var moduleType: String

    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    private var currentFilePickerRequest: Triple<String, String, Button>? = null

    companion object {
        const val ARG_MODULE_TYPE = "module_type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moduleType = requireArguments().getString(ARG_MODULE_TYPE)!!

        filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                currentFilePickerRequest?.let { (paramName, _, button) ->
                    val fileId = result.data?.getStringExtra("fileId")
                    val fileName = result.data?.getStringExtra("fileName")

                    if (fileId != null && fileName != null) {
                        moduleParameters[paramName] = fileId
                        button.text = getString(R.string.parameter_with_file, paramName, fileName)
                    }
                }
                currentFilePickerRequest = null // Clear the request
            }
        }
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
                    text = getString(R.string.select_parameter, paramName, expectedType)
                    setOnClickListener {
                        currentFilePickerRequest = Triple(paramName, expectedType, this)
                        val intent = Intent(requireContext(), FilePickerActivity::class.java).apply {
                            putExtra("expectedType", expectedType)
                        }
                        filePickerLauncher.launch(intent)
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
            .setTitle(getString(R.string.set_parameters_title, moduleType))
            .setView(layout)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
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
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
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
            "chat_post" -> listOf("spaceId", "message")
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
