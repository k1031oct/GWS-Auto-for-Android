package com.gws.auto.mobile.android.ui.workflow.editor

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.android.material.textfield.TextInputLayout
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentModuleSettingsBinding
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import androidx.fragment.app.viewModels
import com.gws.auto.mobile.android.ui.theme.*
import androidx.compose.ui.graphics.toArgb
import android.content.res.Configuration
import android.content.res.ColorStateList

@AndroidEntryPoint
class ModuleSettingsDialogFragment(private val module: Module) : DialogFragment() {

    private var _binding: FragmentModuleSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkflowEditorViewModel by activityViewModels()

    @Inject
    lateinit var googleApiAuthorizer: GoogleApiAuthorizer
    
    private val themeViewModel: com.gws.auto.mobile.android.ui.theme.ThemeViewModel by viewModels()
    private var currentHighlightColor: Int? = null

    private val launchers = mutableMapOf<String, ActivityResultLauncher<Intent>>()
    private val selectedFiles = mutableMapOf<String, Pair<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrayOf("gmailAttachment", "sourceSheet", "destSheet").forEach { key ->
            launchers[key] = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri -> handleFileSelection(uri, key) }
                }
            }
        }
    }

    private fun handleFileSelection(uri: Uri, key: String) {
        val (fileId, fileName) = getFileInfoFromUri(uri)
        if (fileId == null || fileName == null) {
            Toast.makeText(requireContext(), "Failed to resolve file information.", Toast.LENGTH_SHORT).show()
            return
        }
        selectedFiles[key] = Pair(fileId, fileName)
        view?.findViewWithTag<TextView>("${key}Name")?.text = fileName

        if ((key == "sourceSheet" || key == "destSheet") && fileId.isNotEmpty()) {
            view?.findViewWithTag<Spinner>("${key}SheetSpinner")?.let { fetchSheetNames(fileId, it) }
        }
    }

    private fun getFileInfoFromUri(uri: Uri): Pair<String?, String?> {
        var displayName: String? = null
        var documentId: String? = null
        try {
            requireContext().contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) displayName = cursor.getString(nameIndex)
                }
            }
            uri.lastPathSegment?.let { documentId = it.split(":").last() }
        } catch (e: Exception) {
            Timber.e(e, "Error getting file info from URI")
            return Pair(null, null)
        }
        return Pair(documentId, displayName ?: documentId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentModuleSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.moduleType.text = module.type
        binding.parametersContainer.removeAllViews()
        when (module.type) {
            "DEFINE_VARIABLE" -> setupDefineVariableUI()
            "GET_RELATIVE_DATE" -> setupGetRelativeDateUI()
            "CREATE_GMAIL_DRAFT" -> setupCreateGmailDraftUI()
            "DUPLICATE_SPREADSHEET" -> setupDuplicateSpreadsheetUI()
            "COPY_PASTE_SHEET_VALUES" -> setupCopyPasteSheetValuesUI()
            else -> setupDefaultUI()
        }
        binding.cancelButton.setOnClickListener { dismiss() }
        
        // Observe and apply highlight color
        lifecycleScope.launch {
            themeViewModel.highlightColor.collect { colorName ->
                val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                val isDarkTheme = nightModeFlags == Configuration.UI_MODE_NIGHT_YES

                val color = when (colorName) {
                    "forest" -> if (isDarkTheme) ForestPrimaryDark else ForestPrimaryLight
                    "ocean" -> if (isDarkTheme) OceanPrimaryDark else OceanPrimaryLight
                    "sakura" -> if (isDarkTheme) SakuraPrimaryDark else SakuraPrimaryLight
                    else -> if (isDarkTheme) DefaultPrimaryDark else DefaultPrimaryLight
                }
                currentHighlightColor = color.toArgb()
                applyHighlightColorToDialog()
            }
        }
    }
    
    private fun applyHighlightColorToDialog() {
        currentHighlightColor?.let { color ->
            val colorStateList = ColorStateList.valueOf(color)
            binding.saveButton.backgroundTintList = colorStateList
            binding.saveButton.setTextColor(android.graphics.Color.BLACK)
            binding.cancelButton.setTextColor(color)
        }
    }

    private fun setupDefineVariableUI() {
        val variableNameInput = createTextInputLayout("Variable Name", module.parameters["variableName"])
        val valueInput = createTextInputLayout("Value", module.parameters["value"])
        binding.parametersContainer.addView(variableNameInput)
        binding.parametersContainer.addView(valueInput)
        binding.saveButton.setOnClickListener {
            val params = mapOf(
                "variableName" to variableNameInput.editText?.text.toString(),
                "value" to valueInput.editText?.text.toString()
            )
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupGetRelativeDateUI() {
        val amountInput = createTextInputLayout("Amount", module.parameters["amount"], isNumeric = true)
        val unitSpinner = createSpinner(R.array.date_units, module.parameters["unit"])
        val directionSpinner = createSpinner(R.array.date_directions, module.parameters["direction"])
        val variableNameInput = createTextInputLayout("Variable Name for Result", module.parameters["variableName"])
        binding.parametersContainer.addView(amountInput)
        binding.parametersContainer.addView(unitSpinner)
        binding.parametersContainer.addView(directionSpinner)
        binding.parametersContainer.addView(variableNameInput)
        binding.saveButton.setOnClickListener {
            val params = mapOf(
                "amount" to amountInput.editText?.text.toString(),
                "unit" to unitSpinner.selectedItem.toString(),
                "direction" to directionSpinner.selectedItem.toString(),
                "variableName" to variableNameInput.editText?.text.toString()
            )
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupCreateGmailDraftUI() {
        val toInput = createTextInputLayout("To", module.parameters["to"])
        val ccInput = createTextInputLayout("Cc", module.parameters["cc"])
        val bccInput = createTextInputLayout("Bcc", module.parameters["bcc"])
        val subjectInput = createTextInputLayout("Subject", module.parameters["subject"])
        val bodyInput = createTextInputLayout("Body", module.parameters["body"], isMultiLine = true)
        val attachmentPicker = createFilePickerViews("gmailAttachment", "Attach File", module.parameters, "*/*")

        binding.parametersContainer.addView(toInput)
        binding.parametersContainer.addView(ccInput)
        binding.parametersContainer.addView(bccInput)
        binding.parametersContainer.addView(subjectInput)
        binding.parametersContainer.addView(bodyInput)
        binding.parametersContainer.addView(attachmentPicker)

        binding.saveButton.setOnClickListener {
            val params = mutableMapOf(
                "to" to toInput.editText?.text.toString(),
                "cc" to ccInput.editText?.text.toString(),
                "bcc" to bccInput.editText?.text.toString(),
                "subject" to subjectInput.editText?.text.toString(),
                "body" to bodyInput.editText?.text.toString()
            )
            selectedFiles["gmailAttachment"]?.let {
                params["attachmentFileId"] = it.first
                params["attachmentFileName"] = it.second
            }
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupDuplicateSpreadsheetUI() {
        val newFileNameInput = createTextInputLayout("New File Name", module.parameters["newFileName"])
        val sourcePicker = createFilePickerViews("sourceSheet", "Select Source Sheet", module.parameters, "application/vnd.google-apps.spreadsheet")
        val destFolderIdInput = createTextInputLayout("Destination Folder ID (Optional)", module.parameters["destinationFolderId"])

        binding.parametersContainer.addView(sourcePicker)
        binding.parametersContainer.addView(newFileNameInput)
        binding.parametersContainer.addView(destFolderIdInput)

        binding.saveButton.setOnClickListener {
            val params = mutableMapOf(
                "newFileName" to newFileNameInput.editText?.text.toString(),
                "destinationFolderId" to destFolderIdInput.editText?.text.toString()
            )
            selectedFiles["sourceSheet"]?.let {
                params["sourceFileId"] = it.first
                params["sourceFileName"] = it.second
            }
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupCopyPasteSheetValuesUI() {
        val container = binding.parametersContainer

        val sourceSheetName = module.parameters["sourceSheetName"]
        val destSheetName = module.parameters["destinationSheetName"]
        val sourceRange = module.parameters["sourceRange"]?.split(":") ?: listOf("", "")
        
        container.addView(createSectionHeader("Source"))
        val sourceSheetSpinner = createSpinner(emptyList(), sourceSheetName).apply { tag = "sourceSheetSheetSpinner" }
        val sourceRangeStartInput = createTextInputLayout("Start Cell", sourceRange.getOrNull(0))
        val sourceRangeEndInput = createTextInputLayout("End Cell", sourceRange.getOrNull(1))
        container.addView(createFilePickerViews("sourceSheet", "Select Source Sheet", module.parameters, "application/vnd.google-apps.spreadsheet"))
        container.addView(sourceSheetSpinner)
        container.addView(LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(sourceRangeStartInput, LinearLayout.LayoutParams(0, -2, 1f))
            addView(createStaticTextView(" : "))
            addView(sourceRangeEndInput, LinearLayout.LayoutParams(0, -2, 1f))
        })
        
        container.addView(createSectionHeader("Destination"))
        val destSheetSpinner = createSpinner(emptyList(), destSheetName).apply { tag = "destSheetSheetSpinner" }
        val destStartCellInput = createTextInputLayout("Start Cell (e.g., C5)", module.parameters["destinationStartCell"])
        container.addView(createFilePickerViews("destSheet", "Select Destination Sheet", module.parameters, "application/vnd.google-apps.spreadsheet"))
        container.addView(destSheetSpinner)
        container.addView(destStartCellInput)

        module.parameters["sourceFileId"]?.let { fetchSheetNames(it, sourceSheetSpinner, sourceSheetName) }
        module.parameters["destinationFileId"]?.let { fetchSheetNames(it, destSheetSpinner, destSheetName) }

        binding.saveButton.setOnClickListener {
            val params = mutableMapOf(
                "sourceSheetName" to sourceSheetSpinner.selectedItem?.toString(),
                "sourceRange" to "${sourceRangeStartInput.editText?.text}:${sourceRangeEndInput.editText?.text}",
                "destinationSheetName" to destSheetSpinner.selectedItem?.toString(),
                "destinationStartCell" to destStartCellInput.editText?.text.toString()
            )
            selectedFiles["sourceSheet"]?.let {
                params["sourceFileId"] = it.first
                params["sourceFileName"] = it.second
            }
            selectedFiles["destSheet"]?.let {
                params["destinationFileId"] = it.first
                params["destinationFileName"] = it.second
            }
            viewModel.updateModuleParameters(module.id, params.filterValues { it != null } as Map<String, String>)
            dismiss()
        }
    }

    private fun fetchSheetNames(fileId: String, spinner: Spinner, selection: String? = null) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val account = GoogleSignIn.getLastSignedInAccount(requireContext())
                if (account == null) {
                    withContext(Dispatchers.Main) { Toast.makeText(requireContext(), "Not signed in.", Toast.LENGTH_SHORT).show() }
                    return@launch
                }
                val credential = GoogleAccountCredential.usingOAuth2(requireContext(), listOf(SheetsScopes.SPREADSHEETS_READONLY))
                credential.selectedAccount = account.account

                val sheetsService = Sheets.Builder(googleApiAuthorizer.httpTransport, googleApiAuthorizer.jsonFactory, credential).build()
                val spreadsheet = sheetsService.spreadsheets().get(fileId).execute()
                val sheetNames = spreadsheet.sheets.map { it.properties.title }
                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sheetNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                    selection?.let { spinner.setSelection(adapter.getPosition(it)) }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching sheet names")
                withContext(Dispatchers.Main) { Toast.makeText(requireContext(), "Failed to fetch sheet names.", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun setupDefaultUI() {
        val textView = TextView(requireContext()).apply {
            text = "This module is not configurable yet."
        }
        binding.parametersContainer.addView(textView)
        binding.saveButton.setOnClickListener { dismiss() }
    }
    
    private fun createSectionHeader(title: String): TextView {
        return TextView(requireContext()).apply {
            text = title
            setTextAppearance(android.R.style.TextAppearance_Material_Medium)
            setPadding(0, 24, 0, 8)
        }
    }
    
    private fun createStaticTextView(text: String): TextView = TextView(requireContext()).apply { this.text = text; setPadding(8) }
    
    private fun createFilePickerViews(key: String, buttonText: String, initialParams: Map<String, String>, mimeType: String): View {
        val layout = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL }
        val button = Button(requireContext()).apply { 
            text = buttonText
            // Apply highlight color if available
            currentHighlightColor?.let {
                backgroundTintList = ColorStateList.valueOf(it)
            }
        }
        val textView = TextView(requireContext()).apply {
            tag = "${key}Name"
            text = initialParams["${key}FileName"] ?: "None selected"
            setPadding(8)
        }
        button.setOnClickListener { openPicker(key, mimeType) }
        layout.addView(button)
        layout.addView(textView)
        return layout
    }
    
    private fun openPicker(key: String, mimeType: String) {
        if (GoogleSignIn.getLastSignedInAccount(requireContext()) == null) {
            Toast.makeText(requireContext(), "Please sign in first via the User Info settings.", Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = mimeType
        }
        launchers[key]?.launch(intent)
    }

    private fun createTextInputLayout(hint: String, initialValue: String?, isNumeric: Boolean = false, isMultiLine: Boolean = false): TextInputLayout {
        val textInputLayout = TextInputLayout(requireContext()).apply {
            this.hint = hint
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            // Apply highlight color if available
            currentHighlightColor?.let {
                setBoxStrokeColorStateList(ColorStateList.valueOf(it))
            }
        }
        val editText = EditText(requireContext()).apply {
            setText(initialValue ?: "")
            if (isNumeric) {
                inputType = android.text.InputType.TYPE_CLASS_NUMBER
            }
            if (isMultiLine) {
                inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
                minLines = 3
            }
        }
        textInputLayout.addView(editText)
        return textInputLayout
    }

    private fun createSpinner(items: List<String>, selectedValue: String? = null): Spinner {
        val spinner = Spinner(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        selectedValue?.let {
            val position = adapter.getPosition(it)
            if (position >= 0) {
                spinner.setSelection(position)
            }
        }
        return spinner
    }

    private fun createSpinner(itemsArrayRes: Int, selectedValue: String?): Spinner {
        val spinner = Spinner(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            itemsArrayRes,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = it
        }
        selectedValue?.let {
            val position = adapter.getPosition(it)
            if (position >= 0) {
                spinner.setSelection(position)
            }
        }
        return spinner
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
