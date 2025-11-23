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
    @StringKey("copy_paste_sheet_values")
    abstract fun bindCopyPasteSheetValuesModule(impl: CopyPasteSheetValuesModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("duplicate_spreadsheet")
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
    @StringKey("create_gmail_draft")
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

    // Microsoft Outlook
    @Binds
    @IntoMap
    @StringKey("outlook_send_email")
    abstract fun bindOutlookSendEmailModule(impl: OutlookSendEmailModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("outlook_create_draft")
    abstract fun bindOutlookCreateDraftModule(impl: OutlookCreateDraftModule): ModuleExecutor

    // Microsoft OneDrive
    @Binds
    @IntoMap
    @StringKey("onedrive_upload_file")
    abstract fun bindOneDriveUploadFileModule(impl: OneDriveUploadFileModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("onedrive_create_folder")
    abstract fun bindOneDriveCreateFolderModule(impl: OneDriveCreateFolderModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("onedrive_copy_file")
    abstract fun bindOneDriveCopyFileModule(impl: OneDriveCopyFileModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("onedrive_move_file")
    abstract fun bindOneDriveMoveFileModule(impl: OneDriveMoveFileModule): ModuleExecutor

    // Slack
    @Binds
    @IntoMap
    @StringKey("slack_post")
    abstract fun bindSlackPostModule(impl: SlackPostModule): ModuleExecutor

    // Utility Modules
    @Binds
    @IntoMap
    @StringKey("define_variable")
    abstract fun bindDefineVariableModule(impl: DefineVariableModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("get_relative_date")
    abstract fun bindGetRelativeDateModule(impl: GetRelativeDateModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("log_message")
    abstract fun bindLogMessageModule(impl: LogMessageModule): ModuleExecutor

    @Binds
    @IntoMap
    @StringKey("toast_notification")
    abstract fun bindToastNotificationModule(impl: ToastNotificationModule): ModuleExecutor
}
