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
    @StringKey("LOG_MESSAGE")
    abstract fun bindLogMessageModule(impl: LogMessageModule): ModuleExecutor

    // Google Tasks
    @Binds
    @IntoMap
    @StringKey("tasks_create_task")
    abstract fun bindGoogleTasksExecutor(impl: GoogleTasksExecutor): ModuleExecutor
}
