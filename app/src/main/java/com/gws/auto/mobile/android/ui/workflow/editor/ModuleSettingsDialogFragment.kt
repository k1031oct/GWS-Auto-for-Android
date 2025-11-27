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

    private val contactInputs = mutableMapOf<String, EditText>()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrayOf("gmailAttachment", "sourceSheet", "destSheet", "sourceFile", "destFolder", "csvFile", "contact_to", "contact_cc", "contact_bcc").forEach { key ->
            launchers[key] = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    if (key.startsWith("contact_")) {
                        data?.data?.let { uri -> handleContactSelection(uri, key) }
                    } else {
                        val fileId = data?.getStringExtra("fileId")
                        val fileName = data?.getStringExtra("fileName")
                        if (fileId != null && fileName != null) {
                            handleFilePickerResult(fileId, fileName, key)
                        } else {
                            // Fallback for safety or if we still support system picker for some reason
                            data?.data?.let { uri -> handleFileSelection(uri, key) }
                        }
                    }
                }
            }
        }
    }

    private fun handleContactSelection(uri: Uri, key: String) {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val emailIndex = it.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Email.ADDRESS)
                if (emailIndex != -1) {
                    val email = it.getString(emailIndex)
                    contactInputs[key]?.let { editText ->
                        val currentText = editText.text.toString()
                        if (currentText.isBlank()) {
                            editText.setText(email)
                        } else {
                            editText.setText("$currentText, $email")
                        }
                    }
                }
            }
        }
    }

    private fun handleFilePickerResult(fileId: String, fileName: String, key: String) {
        selectedFiles[key] = Pair(fileId, fileName)
        view?.findViewWithTag<TextView>("${key}Name")?.text = fileName

        if ((key == "sourceSheet" || key == "destSheet") && fileId.isNotEmpty()) {
            view?.findViewWithTag<Spinner>("${key}SheetSpinner")?.let { fetchSheetNames(fileId, it) }
        }
    }

    private fun handleFileSelection(uri: Uri, key: String) {
        val (fileId, fileName) = getFileInfoFromUri(uri)
        if (fileId == null || fileName == null) {
            Toast.makeText(requireContext(), getString(R.string.file_info_resolve_failed), Toast.LENGTH_SHORT).show()
            return
        }
        handleFilePickerResult(fileId, fileName, key)
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
            "tasks_create_task" -> setupGoogleTasksCreateTaskUI()
            "drive_convert_excel_to_sheets" -> setupDriveConvertExcelToSheetsUI()
            "drive_delete_file" -> setupDriveDeleteFileUI()
            "drive_list_files_to_sheet" -> setupDriveListFilesToSheetUI()
            "sheets_unhide_rows_cols" -> setupSheetsUnhideRowsColsUI()
            "sheets_hide_rows_cols" -> setupSheetsHideRowsColsUI()
            "sheets_delete_rows_cols" -> setupSheetsDeleteRowsColsUI()
            "sheets_insert_rows_cols" -> setupSheetsInsertRowsColsUI()
            "sheets_import_csv" -> setupSheetsImportCsvUI()
            "sheets_export_pdf" -> setupSheetsExportPdfUI()
            "sheets_export_excel" -> setupSheetsExportExcelUI()
            "gmail_save_attachments" -> setupGmailSaveAttachmentsUI()
            "if_else" -> setupIfElseUI()
            "no_op" -> setupNoOpUI()
            "delay" -> setupDelayUI()
            "run_workflow" -> setupRunWorkflowUI()
            "get_holidays" -> setupGetHolidaysUI()
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

        contactInputs["contact_to"] = toInput.editText!!
        contactInputs["contact_cc"] = ccInput.editText!!
        contactInputs["contact_bcc"] = bccInput.editText!!

        binding.parametersContainer.addView(createContactPickerButton("contact_to", "宛先を選択"))
        binding.parametersContainer.addView(toInput)
        binding.parametersContainer.addView(createContactPickerButton("contact_cc", "CCを選択"))
        binding.parametersContainer.addView(ccInput)
        binding.parametersContainer.addView(createContactPickerButton("contact_bcc", "BCCを選択"))
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

        contactInputs["contact_to"] = toInput.editText!!
        contactInputs["contact_cc"] = ccInput.editText!!
        contactInputs["contact_bcc"] = bccInput.editText!!

        binding.parametersContainer.addView(createContactPickerButton("contact_to", "宛先を選択"))
        binding.parametersContainer.addView(toInput)
        binding.parametersContainer.addView(createContactPickerButton("contact_cc", "CCを選択"))
        binding.parametersContainer.addView(ccInput)
        binding.parametersContainer.addView(createContactPickerButton("contact_bcc", "BCCを選択"))
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
        val intent = Intent(requireContext(), com.gws.auto.mobile.android.ui.filepicker.FilePickerActivity::class.java)
        if (mimeType == "application/vnd.google-apps.folder") {
            intent.putExtra("expectedType", "folder")
        } else {
            intent.putExtra("expectedType", "file")
        }
        launchers[key]?.launch(intent)
    }

    private fun createContactPickerButton(key: String, text: String): Button {
        return Button(requireContext()).apply {
            this.text = text
            currentHighlightColor?.let {
                backgroundTintList = ColorStateList.valueOf(it)
            }
            setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK, android.provider.ContactsContract.CommonDataKinds.Email.CONTENT_URI)
                launchers[key]?.launch(intent)
            }
        }
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

    private fun setupGoogleTasksCreateTaskUI() {
        val titleInput = createTextInputLayout("タスク名", module.parameters["title"])
        val notesInput = createTextInputLayout("メモ", module.parameters["notes"], isMultiLine = true)
        val dueDateInput = createTextInputLayout("期限 (YYYY-MM-DD)", module.parameters["due_date"])
        
        // Recurrence UI (Simple Spinner)
        val recurrenceOptions = listOf("繰り返しなし", "毎日", "毎週", "毎月")
        val recurrenceSpinner = createSpinner(recurrenceOptions, module.parameters["recurrence"])
        
        // Date Picker for Due Date
        dueDateInput.editText?.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            val dateSetListener = android.app.DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val dateStr = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
                dueDateInput.editText?.setText(dateStr)
            }
            android.app.DatePickerDialog(requireContext(), dateSetListener,
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)).show()
        }
        dueDateInput.editText?.isFocusable = false // Make it read-only for typing, force picker or allow typing? 
        // Better to allow typing but show picker on click. 
        // Actually, if I set onClickListener, I should probably make it not focusableInTouchMode or similar to prevent keyboard.
        // For simplicity, let's just leave it as text input but add a "Select Date" button or icon? 
        // Or just make it focusable=false so clicking opens dialog.
        dueDateInput.editText?.isFocusable = false
        dueDateInput.editText?.isClickable = true

        binding.parametersContainer.addView(titleInput)
        binding.parametersContainer.addView(notesInput)
        binding.parametersContainer.addView(dueDateInput)
        binding.parametersContainer.addView(createSectionHeader("繰り返し設定"))
        binding.parametersContainer.addView(recurrenceSpinner)

        binding.saveButton.setOnClickListener {
            val params = mapOf(
                "title" to titleInput.editText?.text.toString(),
                "notes" to notesInput.editText?.text.toString(),
                "due_date" to dueDateInput.editText?.text.toString(),
                "recurrence" to recurrenceSpinner.selectedItem.toString()
            )
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupDriveConvertExcelToSheetsUI() {
        val sourcePicker = createFilePickerViews("sourceFile", "Excelファイルを選択", module.parameters, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        val newFileNameInput = createTextInputLayout("新しいファイル名", module.parameters["newFileName"])
        val parentFolderPicker = createFilePickerViews("destFolder", "親フォルダを選択", module.parameters, "application/vnd.google-apps.folder")

        binding.parametersContainer.addView(sourcePicker)
        binding.parametersContainer.addView(newFileNameInput)
        binding.parametersContainer.addView(parentFolderPicker)

        binding.saveButton.setOnClickListener {
            val params = mutableMapOf(
                "newFileName" to newFileNameInput.editText?.text.toString()
            )
            selectedFiles["sourceFile"]?.let { params["sourceFileId"] = it.first; params["sourceFileName"] = it.second }
            selectedFiles["destFolder"]?.let { params["parentFolderId"] = it.first; params["parentFolderName"] = it.second }
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupDriveDeleteFileUI() {
        val filePicker = createFilePickerViews("sourceFile", "削除するファイルを選択", module.parameters, "*/*")
        binding.parametersContainer.addView(filePicker)
        binding.saveButton.setOnClickListener {
            val params = mutableMapOf<String, String>()
            selectedFiles["sourceFile"]?.let { params["fileId"] = it.first; params["fileName"] = it.second }
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupDriveListFilesToSheetUI() {
        val folderPicker = createFilePickerViews("destFolder", "対象フォルダを選択", module.parameters, "application/vnd.google-apps.folder")
        val sheetPicker = createFilePickerViews("sourceSheet", "出力先シートを選択", module.parameters, "application/vnd.google-apps.spreadsheet")
        val sheetNameInput = createTextInputLayout("シート名", module.parameters["sheetName"])
        
        binding.parametersContainer.addView(folderPicker)
        binding.parametersContainer.addView(sheetPicker)
        binding.parametersContainer.addView(sheetNameInput)

        binding.saveButton.setOnClickListener {
            val params = mutableMapOf(
                "sheetName" to sheetNameInput.editText?.text.toString()
            )
            selectedFiles["destFolder"]?.let { params["folderId"] = it.first; params["folderName"] = it.second }
            selectedFiles["sourceSheet"]?.let { params["spreadsheetId"] = it.first; params["spreadsheetName"] = it.second }
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupSheetsUnhideRowsColsUI() {
        val sheetPicker = createFilePickerViews("sourceSheet", "シートを選択", module.parameters, "application/vnd.google-apps.spreadsheet")
        val sheetNameInput = createTextInputLayout("シート名", module.parameters["sheetName"])
        binding.parametersContainer.addView(sheetPicker)
        binding.parametersContainer.addView(sheetNameInput)
        binding.saveButton.setOnClickListener {
            val params = mutableMapOf("sheetName" to sheetNameInput.editText?.text.toString())
            selectedFiles["sourceSheet"]?.let { params["spreadsheetId"] = it.first; params["spreadsheetName"] = it.second }
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupSheetsHideRowsColsUI() {
        setupSheetsRowColOpUI("HIDE")
    }

    private fun setupSheetsDeleteRowsColsUI() {
        setupSheetsRowColOpUI("DELETE")
    }

    private fun setupSheetsRowColOpUI(opType: String) {
        val sheetPicker = createFilePickerViews("sourceSheet", "シートを選択", module.parameters, "application/vnd.google-apps.spreadsheet")
        val sheetNameInput = createTextInputLayout("シート名", module.parameters["sheetName"])
        val checkRangeInput = createTextInputLayout("チェック範囲 (例: A:A)", module.parameters["checkRange"])
        val conditionSpinner = createSpinner(listOf("EMPTY", "EQUALS", "CONTAINS"), module.parameters["condition"])
        val conditionValueInput = createTextInputLayout("条件値", module.parameters["conditionValue"])

        binding.parametersContainer.addView(sheetPicker)
        binding.parametersContainer.addView(sheetNameInput)
        binding.parametersContainer.addView(checkRangeInput)
        binding.parametersContainer.addView(conditionSpinner)
        binding.parametersContainer.addView(conditionValueInput)

        binding.saveButton.setOnClickListener {
            val params = mutableMapOf(
                "sheetName" to sheetNameInput.editText?.text.toString(),
                "checkRange" to checkRangeInput.editText?.text.toString(),
                "condition" to conditionSpinner.selectedItem.toString(),
                "conditionValue" to conditionValueInput.editText?.text.toString()
            )
            selectedFiles["sourceSheet"]?.let { params["spreadsheetId"] = it.first; params["spreadsheetName"] = it.second }
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupSheetsInsertRowsColsUI() {
        val sheetPicker = createFilePickerViews("sourceSheet", "シートを選択", module.parameters, "application/vnd.google-apps.spreadsheet")
        val sheetNameInput = createTextInputLayout("シート名", module.parameters["sheetName"])
        val insertAtInput = createTextInputLayout("挿入位置 (インデックス)", module.parameters["insertAt"], isNumeric = true)
        val countInput = createTextInputLayout("挿入数", module.parameters["count"], isNumeric = true)

        binding.parametersContainer.addView(sheetPicker)
        binding.parametersContainer.addView(sheetNameInput)
        binding.parametersContainer.addView(insertAtInput)
        binding.parametersContainer.addView(countInput)

        binding.saveButton.setOnClickListener {
            val params = mutableMapOf(
                "sheetName" to sheetNameInput.editText?.text.toString(),
                "insertAt" to insertAtInput.editText?.text.toString(),
                "count" to countInput.editText?.text.toString()
            )
            selectedFiles["sourceSheet"]?.let { params["spreadsheetId"] = it.first; params["spreadsheetName"] = it.second }
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupSheetsImportCsvUI() {
        val csvPicker = createFilePickerViews("csvFile", "CSVファイルを選択", module.parameters, "text/csv")
        val sheetPicker = createFilePickerViews("sourceSheet", "出力先シートを選択", module.parameters, "application/vnd.google-apps.spreadsheet")
        val sheetNameInput = createTextInputLayout("シート名", module.parameters["sheetName"])
        val delimiterInput = createTextInputLayout("区切り文字 (デフォルト: ,)", module.parameters["delimiter"])

        binding.parametersContainer.addView(csvPicker)
        binding.parametersContainer.addView(sheetPicker)
        binding.parametersContainer.addView(sheetNameInput)
        binding.parametersContainer.addView(delimiterInput)

        binding.saveButton.setOnClickListener {
            val params = mutableMapOf(
                "sheetName" to sheetNameInput.editText?.text.toString(),
                "delimiter" to delimiterInput.editText?.text.toString()
            )
            selectedFiles["csvFile"]?.let { params["csvFileId"] = it.first; params["csvFileName"] = it.second }
            selectedFiles["sourceSheet"]?.let { params["spreadsheetId"] = it.first; params["spreadsheetName"] = it.second }
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupSheetsExportPdfUI() {
        val sheetPicker = createFilePickerViews("sourceSheet", "シートを選択", module.parameters, "application/vnd.google-apps.spreadsheet")
        val sheetNameInput = createTextInputLayout("シート名", module.parameters["sheetName"])
        val folderPicker = createFilePickerViews("destFolder", "出力先フォルダを選択", module.parameters, "application/vnd.google-apps.folder")
        val fileNameInput = createTextInputLayout("ファイル名", module.parameters["fileName"])

        binding.parametersContainer.addView(sheetPicker)
        binding.parametersContainer.addView(sheetNameInput)
        binding.parametersContainer.addView(folderPicker)
        binding.parametersContainer.addView(fileNameInput)

        binding.saveButton.setOnClickListener {
            val params = mutableMapOf(
                "sheetName" to sheetNameInput.editText?.text.toString(),
                "fileName" to fileNameInput.editText?.text.toString()
            )
            selectedFiles["sourceSheet"]?.let { params["spreadsheetId"] = it.first; params["spreadsheetName"] = it.second }
            selectedFiles["destFolder"]?.let { params["destFolderId"] = it.first; params["destFolderName"] = it.second }
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupSheetsExportExcelUI() {
        val sheetPicker = createFilePickerViews("sourceSheet", "シートを選択", module.parameters, "application/vnd.google-apps.spreadsheet")
        val folderPicker = createFilePickerViews("destFolder", "出力先フォルダを選択", module.parameters, "application/vnd.google-apps.folder")
        val fileNameInput = createTextInputLayout("ファイル名", module.parameters["fileName"])

        binding.parametersContainer.addView(sheetPicker)
        binding.parametersContainer.addView(folderPicker)
        binding.parametersContainer.addView(fileNameInput)

        binding.saveButton.setOnClickListener {
            val params = mutableMapOf(
                "fileName" to fileNameInput.editText?.text.toString()
            )
            selectedFiles["sourceSheet"]?.let { params["spreadsheetId"] = it.first; params["spreadsheetName"] = it.second }
            selectedFiles["destFolder"]?.let { params["destFolderId"] = it.first; params["destFolderName"] = it.second }
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupGmailSaveAttachmentsUI() {
        val queryInput = createTextInputLayout("検索クエリ (例: has:attachment)", module.parameters["query"])
        val folderPicker = createFilePickerViews("destFolder", "保存先フォルダを選択", module.parameters, "application/vnd.google-apps.folder")

        binding.parametersContainer.addView(queryInput)
        binding.parametersContainer.addView(folderPicker)

        binding.saveButton.setOnClickListener {
            val params = mutableMapOf(
                "query" to queryInput.editText?.text.toString()
            )
            selectedFiles["destFolder"]?.let { params["destFolderId"] = it.first; params["destFolderName"] = it.second }
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupIfElseUI() {
        val conditionInput = createTextInputLayout("条件 (例: {{status}} == Success)", module.parameters["condition"])
        val trueIdInput = createTextInputLayout("Trueの場合のモジュールID", module.parameters["trueModuleId"])
        val falseIdInput = createTextInputLayout("Falseの場合のモジュールID", module.parameters["falseModuleId"])

        binding.parametersContainer.addView(conditionInput)
        binding.parametersContainer.addView(trueIdInput)
        binding.parametersContainer.addView(falseIdInput)

        binding.saveButton.setOnClickListener {
            val params = mapOf(
                "condition" to conditionInput.editText?.text.toString(),
                "trueModuleId" to trueIdInput.editText?.text.toString(),
                "falseModuleId" to falseIdInput.editText?.text.toString()
            )
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupNoOpUI() {
        binding.parametersContainer.addView(TextView(requireContext()).apply { text = "設定項目はありません" })
        binding.saveButton.setOnClickListener { dismiss() }
    }

    private fun setupDelayUI() {
        val durationInput = createTextInputLayout("時間", module.parameters["duration"], isNumeric = true)
        val unitSpinner = createSpinner(listOf("SECONDS", "MINUTES", "HOURS"), module.parameters["unit"])
        binding.parametersContainer.addView(durationInput)
        binding.parametersContainer.addView(unitSpinner)
        binding.saveButton.setOnClickListener {
            val params = mapOf(
                "duration" to durationInput.editText?.text.toString(),
                "unit" to unitSpinner.selectedItem.toString()
            )
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupRunWorkflowUI() {
        val workflowIdInput = createTextInputLayout("ワークフローID", module.parameters["workflowId"])
        val modeSpinner = createSpinner(listOf("SEQUENTIAL", "PARALLEL"), module.parameters["mode"])
        binding.parametersContainer.addView(workflowIdInput)
        binding.parametersContainer.addView(modeSpinner)
        binding.saveButton.setOnClickListener {
            val params = mapOf(
                "workflowId" to workflowIdInput.editText?.text.toString(),
                "mode" to modeSpinner.selectedItem.toString()
            )
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun setupGetHolidaysUI() {
        val startDateInput = createTextInputLayout("開始日 (YYYY-MM-DD)", module.parameters["startDate"])
        val endDateInput = createTextInputLayout("終了日 (YYYY-MM-DD)", module.parameters["endDate"])
        val sheetPicker = createFilePickerViews("sourceSheet", "出力先シートを選択", module.parameters, "application/vnd.google-apps.spreadsheet")
        val sheetNameInput = createTextInputLayout("シート名", module.parameters["sheetName"])
        val countryInput = createTextInputLayout("国コード (例: JP, US)", module.parameters["countryCode"])

        binding.parametersContainer.addView(startDateInput)
        binding.parametersContainer.addView(endDateInput)
        binding.parametersContainer.addView(sheetPicker)
        binding.parametersContainer.addView(sheetNameInput)
        binding.parametersContainer.addView(countryInput)

        binding.saveButton.setOnClickListener {
            val params = mutableMapOf(
                "startDate" to startDateInput.editText?.text.toString(),
                "endDate" to endDateInput.editText?.text.toString(),
                "sheetName" to sheetNameInput.editText?.text.toString(),
                "countryCode" to countryInput.editText?.text.toString()
            )
            selectedFiles["sourceSheet"]?.let { params["spreadsheetId"] = it.first; params["spreadsheetName"] = it.second }
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
