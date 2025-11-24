package com.gws.auto.mobile.android.ui.workflow.editor

import android.app.Activity
import android.content.Intent
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
    
    private val themeViewModel: ThemeViewModel by viewModels()
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
            Toast.makeText(requireContext(), getString(R.string.file_info_resolve_failed), Toast.LENGTH_SHORT).show()
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
            Timber.e(e, "URIからのファイル情報取得エラー")
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
            "gmail_send_email" -> setupGmailSendEmailUI()
            "sheets_append_row" -> setupSheetsAppendRowUI()
            "sheets_set_value" -> setupSheetsSetValueUI()
            "sheets_create_new" -> setupSheetsCreateNewUI()
            "sheets_clear_values" -> setupSheetsClearValuesUI()
            "drive_create_folder" -> setupDriveCreateFolderUI()
            "drive_copy_file" -> setupDriveCopyFileUI()
            "drive_move_file" -> setupDriveMoveFileUI()
            "calendar_create_event" -> setupCalendarCreateEventUI()
            "chat_post" -> setupChatPostUI()
            "SHOW_TOAST" -> setupToastNotificationUI()
            "LOG_MESSAGE" -> setupLogMessageUI()
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
                    else -> if (isDarkTheme) MonochromePrimaryDark else MonochromePrimaryLight
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
        val variableNameInput = createTextInputLayout("変数名", module.parameters["variableName"])
        val valueInput = createTextInputLayout("値", module.parameters["value"])
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
        val amountInput = createTextInputLayout("量", module.parameters["amount"], isNumeric = true)
        val unitSpinner = createSpinner(R.array.date_units, module.parameters["unit"])
        val directionSpinner = createSpinner(R.array.date_directions, module.parameters["direction"])
        val variableNameInput = createTextInputLayout("結果の変数名", module.parameters["variableName"])
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
        val toInput = createTextInputLayout("宛先", module.parameters["to"])
        val ccInput = createTextInputLayout("CC", module.parameters["cc"])
        val bccInput = createTextInputLayout("BCC", module.parameters["bcc"])
        val subjectInput = createTextInputLayout("件名", module.parameters["subject"])
        val bodyInput = createTextInputLayout("本文", module.parameters["body"], isMultiLine = true)
        val attachmentPicker = createFilePickerViews("gmailAttachment", "ファイルを添付", module.parameters, "*/*")

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
        val newFileNameInput = createTextInputLayout("新しいファイル名", module.parameters["newFileName"])
        val sourcePicker = createFilePickerViews("sourceSheet", "ソースシートを選択", module.parameters, "application/vnd.google-apps.spreadsheet")
        val destFolderIdInput = createTextInputLayout("先のフォルダID (任意)", module.parameters["destinationFolderId"])

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
        
        container.addView(createSectionHeader("ソース"))
        val sourceSheetSpinner = createSpinner(emptyList(), sourceSheetName).apply { tag = "sourceSheetSheetSpinner" }
        val sourceRangeStartInput = createTextInputLayout("開始セル", sourceRange.getOrNull(0))
        val sourceRangeEndInput = createTextInputLayout("終了セル", sourceRange.getOrNull(1))
        container.addView(createFilePickerViews("sourceSheet", "ソースシートを選択", module.parameters, "application/vnd.google-apps.spreadsheet"))
        container.addView(sourceSheetSpinner)
        container.addView(LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(sourceRangeStartInput, LinearLayout.LayoutParams(0, -2, 1f))
            addView(createStaticTextView())
            addView(sourceRangeEndInput, LinearLayout.LayoutParams(0, -2, 1f))
        })
        
        container.addView(createSectionHeader("先"))
        val destSheetSpinner = createSpinner(emptyList(), destSheetName).apply { tag = "destSheetSheetSpinner" }
        val destStartCellInput = createTextInputLayout("開始セル (例: C5)", module.parameters["destinationStartCell"])
        container.addView(createFilePickerViews("destSheet", "先のシートを選択", module.parameters, "application/vnd.google-apps.spreadsheet"))
        container.addView(destSheetSpinner)
        container.addView(destStartCellInput)

        module.parameters["sourceFileId"]?.let { fetchSheetNames(it, sourceSheetSpinner, sourceSheetName) }
        module.parameters["destinationFileId"]?.let { fetchSheetNames(it, destSheetSpinner, destSheetName) }

        binding.saveButton.setOnClickListener {
            val params = mutableMapOf<String, String?>(
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
                    withContext(Dispatchers.Main) { Toast.makeText(requireContext(), getString(R.string.not_signed_in), Toast.LENGTH_SHORT).show() }
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
                Timber.e(e, "シート名の取得エラー")
                withContext(Dispatchers.Main) { Toast.makeText(requireContext(), getString(R.string.sheet_name_fetch_failed), Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun setupDefaultUI() {
        val textView = TextView(requireContext()).apply {
            text = getString(R.string.module_not_configurable)
        }
        binding.parametersContainer.addView(textView)
        binding.saveButton.setOnClickListener { dismiss() }
    }

    private fun setupGmailSendEmailUI() {
        val toInput = createTextInputLayout("宛先", module.parameters["to"])
        val ccInput = createTextInputLayout("CC", module.parameters["cc"])
        val bccInput = createTextInputLayout("BCC", module.parameters["bcc"])
        val subjectInput = createTextInputLayout("件名", module.parameters["subject"])
        val bodyInput = createTextInputLayout("本文", module.parameters["body"], isMultiLine = true)

        binding.parametersContainer.addView(toInput)
        binding.parametersContainer.addView(ccInput)
        binding.parametersContainer.addView(bccInput)
        binding.parametersContainer.addView(subjectInput)
        binding.parametersContainer.addView(bodyInput)

        binding.saveButton.setOnClickListener {
            val params = mapOf(
                "to" to toInput.editText?.text.toString(),
                "cc" to ccInput.editText?.text.toString(),
                "bcc" to bccInput.editText?.text.toString(),
                "subject" to subjectInput.editText?.text.toString(),
                "body" to bodyInput.editText?.text.toString()
            )
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupSheetsAppendRowUI() {
        val spreadsheetUrlInput = createTextInputLayout("スプレッドシートURL", module.parameters["spreadsheetUrl"])
        val sheetNameInput = createTextInputLayout("シート名", module.parameters["sheetName"])
        val rowDataInput = createTextInputLayout("行データ（カンマ区切り）", module.parameters["rowData"])

        binding.parametersContainer.addView(spreadsheetUrlInput)
        binding.parametersContainer.addView(sheetNameInput)
        binding.parametersContainer.addView(rowDataInput)

        binding.saveButton.setOnClickListener {
            val params = mapOf(
                "spreadsheetUrl" to spreadsheetUrlInput.editText?.text.toString(),
                "sheetName" to sheetNameInput.editText?.text.toString(),
                "rowData" to rowDataInput.editText?.text.toString()
            )
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupSheetsSetValueUI() {
        val spreadsheetUrlInput = createTextInputLayout("スプレッドシートURL", module.parameters["spreadsheetUrl"])
        val rangeInput = createTextInputLayout("セル範囲（例: A1:B2）", module.parameters["range"])
        val valueInput = createTextInputLayout("値", module.parameters["value"])

        binding.parametersContainer.addView(spreadsheetUrlInput)
        binding.parametersContainer.addView(rangeInput)
        binding.parametersContainer.addView(valueInput)

        binding.saveButton.setOnClickListener {
            val params = mapOf(
                "spreadsheetUrl" to spreadsheetUrlInput.editText?.text.toString(),
                "range" to rangeInput.editText?.text.toString(),
                "value" to valueInput.editText?.text.toString()
            )
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupSheetsCreateNewUI() {
        val titleInput = createTextInputLayout("タイトル", module.parameters["title"])
        val parentFolderIdInput = createTextInputLayout("親フォルダID（任意）", module.parameters["parentFolderId"])

        binding.parametersContainer.addView(titleInput)
        binding.parametersContainer.addView(parentFolderIdInput)

        binding.saveButton.setOnClickListener {
            val params = mapOf(
                "title" to titleInput.editText?.text.toString(),
                "parentFolderId" to parentFolderIdInput.editText?.text.toString()
            )
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupSheetsClearValuesUI() {
        val spreadsheetUrlInput = createTextInputLayout("スプレッドシートURL", module.parameters["spreadsheetUrl"])
        val rangeInput = createTextInputLayout("クリア範囲（例: A1:Z100）", module.parameters["range"])

        binding.parametersContainer.addView(spreadsheetUrlInput)
        binding.parametersContainer.addView(rangeInput)

        binding.saveButton.setOnClickListener {
            val params = mapOf(
                "spreadsheetUrl" to spreadsheetUrlInput.editText?.text.toString(),
                "range" to rangeInput.editText?.text.toString()
            )
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupDriveCreateFolderUI() {
        val newFolderNameInput = createTextInputLayout("フォルダ名", module.parameters["newFolderName"])
        val parentFolderIdInput = createTextInputLayout("親フォルダID（任意）", module.parameters["parentFolderId"])
        val outputFolderIdInput = createTextInputLayout("出力変数名（任意）", module.parameters["outputFolderId"])

        binding.parametersContainer.addView(newFolderNameInput)
        binding.parametersContainer.addView(parentFolderIdInput)
        binding.parametersContainer.addView(outputFolderIdInput)

        binding.saveButton.setOnClickListener {
            val params = mapOf(
                "newFolderName" to newFolderNameInput.editText?.text.toString(),
                "parentFolderId" to parentFolderIdInput.editText?.text.toString(),
                "outputFolderId" to outputFolderIdInput.editText?.text.toString()
            )
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupDriveCopyFileUI() {
        val sourceFileIdInput = createTextInputLayout("ソースファイルID", module.parameters["sourceFileId"])
        val destFolderIdInput = createTextInputLayout("先フォルダID", module.parameters["destinationFolderId"])
        val newFileNameInput = createTextInputLayout("新しいファイル名", module.parameters["newFileName"])

        binding.parametersContainer.addView(sourceFileIdInput)
        binding.parametersContainer.addView(destFolderIdInput)
        binding.parametersContainer.addView(newFileNameInput)

        binding.saveButton.setOnClickListener {
            val params = mapOf(
                "sourceFileId" to sourceFileIdInput.editText?.text.toString(),
                "destinationFolderId" to destFolderIdInput.editText?.text.toString(),
                "newFileName" to newFileNameInput.editText?.text.toString()
            )
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupDriveMoveFileUI() {
        val fileIdInput = createTextInputLayout("ファイルID", module.parameters["fileId"])
        val toFolderIdInput = createTextInputLayout("先フォルダID", module.parameters["toFolderId"])

        binding.parametersContainer.addView(fileIdInput)
        binding.parametersContainer.addView(toFolderIdInput)

        binding.saveButton.setOnClickListener {
            val params = mapOf(
                "fileId" to fileIdInput.editText?.text.toString(),
                "toFolderId" to toFolderIdInput.editText?.text.toString()
            )
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupCalendarCreateEventUI() {
        val summaryInput = createTextInputLayout("イベント名", module.parameters["summary"])
        val startInput = createTextInputLayout("開始日時（ISO8601形式）", module.parameters["start"])
        val endInput = createTextInputLayout("終了日時（ISO8601形式）", module.parameters["end"])
        val descriptionInput = createTextInputLayout("説明（任意）", module.parameters["description"], isMultiLine = true)

        binding.parametersContainer.addView(summaryInput)
        binding.parametersContainer.addView(startInput)
        binding.parametersContainer.addView(endInput)
        binding.parametersContainer.addView(descriptionInput)

        binding.saveButton.setOnClickListener {
            val params = mapOf(
                "summary" to summaryInput.editText?.text.toString(),
                "start" to startInput.editText?.text.toString(),
                "end" to endInput.editText?.text.toString(),
                "description" to descriptionInput.editText?.text.toString()
            )
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupChatPostUI() {
        val spaceIdInput = createTextInputLayout("スペースID", module.parameters["spaceId"])
        val messageInput = createTextInputLayout("メッセージ", module.parameters["message"], isMultiLine = true)

        binding.parametersContainer.addView(spaceIdInput)
        binding.parametersContainer.addView(messageInput)

        binding.saveButton.setOnClickListener {
            val params = mapOf(
                "spaceId" to spaceIdInput.editText?.text.toString(),
                "message" to messageInput.editText?.text.toString()
            )
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupToastNotificationUI() {
        val messageInput = createTextInputLayout("メッセージ", module.parameters["message"])

        binding.parametersContainer.addView(messageInput)

        binding.saveButton.setOnClickListener {
            val params = mapOf(
                "message" to messageInput.editText?.text.toString()
            )
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupLogMessageUI() {
        val messageInput = createTextInputLayout("メッセージ", module.parameters["message"])

        binding.parametersContainer.addView(messageInput)

        binding.saveButton.setOnClickListener {
            val params = mapOf(
                "message" to messageInput.editText?.text.toString()
            )
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }
    
    private fun createSectionHeader(title: String): TextView {
        return TextView(requireContext()).apply {
            text = title
            setTextAppearance(android.R.style.TextAppearance_Material_Medium)
            setPadding(0, 24, 0, 8)
        }
    }
    
    private fun createStaticTextView(): TextView = TextView(requireContext()).apply { this.text = " : "; setPadding(8) }
    
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
            text = initialParams["${key}FileName"] ?: "選択されていません"
            setPadding(8)
        }
        button.setOnClickListener { openPicker(key, mimeType) }
        layout.addView(button)
        layout.addView(textView)
        return layout
    }
    
    private fun openPicker(key: String, mimeType: String) {
        if (GoogleSignIn.getLastSignedInAccount(requireContext()) == null) {
            Toast.makeText(requireContext(), getString(R.string.please_sign_in_first), Toast.LENGTH_LONG).show()
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
