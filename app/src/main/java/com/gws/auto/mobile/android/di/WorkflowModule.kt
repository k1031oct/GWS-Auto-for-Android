package com.gws.auto.mobile.android.di

import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.engine.modules.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey



@Module
@InstallIn(SingletonComponent::class)
abstract class WorkflowModule {

    // Google Calendar
    @Binds
    @IntoMap
    @StringKey("calendar_create_event")
    abstract fun bindCalendarCreateEventModule(impl: CalendarCreateEventModule): ModuleExecutor

    // Google Chat
    @Binds
    @IntoMap
    @StringKey("chat_post")
    abstract fun bindChatPostModule(impl: ChatPostModule): ModuleExecutor


    // Google Sheets
    @Binds
    @IntoMap
    @StringKey("COPY_PASTE_SHEET_VALUES")
    abstract fun bindCopyPasteSheetValuesModule(impl: CopyPasteSheetValuesModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("DUPLICATE_SPREADSHEET")
    abstract fun bindDuplicateSpreadsheetModule(impl: DuplicateSpreadsheetModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("sheets_append_row")
    abstract fun bindSheetsAppendRowModule(impl: SheetsAppendRowModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("sheets_clear_values")
    abstract fun bindSheetsClearValuesModule(impl: SheetsClearValuesModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("sheets_create_new")
    abstract fun bindSheetsCreateNewModule(impl: SheetsCreateNewModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("sheets_set_value")
    abstract fun bindSheetsSetValueModule(impl: SheetsSetValueModule): ModuleExecutor

    // Gmail
    @Binds
    @IntoMap
    @StringKey("CREATE_GMAIL_DRAFT")
    abstract fun bindCreateGmailDraftModule(impl: CreateGmailDraftModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("gmail_send_email")
    abstract fun bindGmailSendEmailModule(impl: GmailSendEmailModule): ModuleExecutor

    // Google Drive
    @Binds
    @IntoMap
    @StringKey("drive_copy_file")
    abstract fun bindDriveCopyFileModule(impl: DriveCopyFileModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("drive_create_folder")
    abstract fun bindDriveCreateFolderModule(impl: DriveCreateFolderModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("drive_move_file")
    abstract fun bindDriveMoveFileModule(impl: DriveMoveFileModule): ModuleExecutor

    // Core
    @Binds
    @IntoMap
    @StringKey("DEFINE_VARIABLE")
    abstract fun bindDefineVariableModule(impl: DefineVariableModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("GET_RELATIVE_DATE")
    abstract fun bindGetRelativeDateModule(impl: GetRelativeDateModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("SHOW_TOAST")
    abstract fun bindToastNotificationModule(impl: ToastNotificationModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("ToastNotificationModule")
    abstract fun bindToastNotificationModuleAlias(impl: ToastNotificationModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("LOG_MESSAGE")
    abstract fun bindLogMessageModule(impl: LogMessageModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("SYSTEM_NOTIFICATION")
    abstract fun bindSystemNotificationModule(impl: SystemNotificationModule): ModuleExecutor

    // Google Tasks
    @Binds
    @IntoMap
    @StringKey("tasks_create_task")
    abstract fun bindGoogleTasksExecutor(impl: GoogleTasksExecutor): ModuleExecutor

    // New Drive Modules
    @Binds
    @IntoMap
    @StringKey("drive_convert_excel_to_sheets")
    abstract fun bindDriveConvertExcelToSheetsModule(impl: DriveConvertExcelToSheetsModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("drive_delete_files_in_folder")
    abstract fun bindDriveDeleteFilesInFolderModule(impl: DriveDeleteFilesInFolderModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("drive_detect_file")
    abstract fun bindDriveDetectFileModule(impl: DriveDetectFileModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("drive_list_files_to_sheet")
    abstract fun bindDriveListFilesToSheetModule(impl: DriveListFilesToSheetModule): ModuleExecutor

    // New Sheets Modules
    @Binds
    @IntoMap
    @StringKey("sheets_unhide_rows_cols")
    abstract fun bindSheetsUnhideRowsColsModule(impl: SheetsUnhideRowsColsModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("sheets_hide_rows_cols")
    abstract fun bindSheetsHideRowsColsModule(impl: SheetsHideRowsColsModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("sheets_delete_rows_cols")
    abstract fun bindSheetsDeleteRowsColsModule(impl: SheetsDeleteRowsColsModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("sheets_insert_rows_cols")
    abstract fun bindSheetsInsertRowsColsModule(impl: SheetsInsertRowsColsModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("sheets_import_csv")
    abstract fun bindSheetsImportCsvModule(impl: SheetsImportCsvModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("sheets_export_pdf")
    abstract fun bindSheetsExportPdfModule(impl: SheetsExportPdfModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("sheets_export_excel")
    abstract fun bindSheetsExportExcelModule(impl: SheetsExportExcelModule): ModuleExecutor

    // New Gmail Modules
    @Binds
    @IntoMap
    @StringKey("gmail_save_attachments")
    abstract fun bindGmailSaveAttachmentsModule(impl: GmailSaveAttachmentsModule): ModuleExecutor

    // New Utility Modules
    @Binds
    @IntoMap
    @StringKey("if_else")
    abstract fun bindIfElseModule(impl: IfElseModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("no_op")
    abstract fun bindNoOpModule(impl: NoOpModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("delay")
    abstract fun bindDelayModule(impl: DelayModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("run_workflow")
    abstract fun bindRunWorkflowModule(impl: RunWorkflowModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("get_holidays")
    abstract fun bindGetHolidaysModule(impl: GetHolidaysModule): ModuleExecutor
}
