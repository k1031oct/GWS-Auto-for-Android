package com.gws.auto.mobile.android.di

import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.engine.modules.CalendarCreateEventModule
import com.gws.auto.mobile.android.domain.engine.modules.ChatPostModule
import com.gws.auto.mobile.android.domain.engine.modules.CopyPasteSheetValuesModule
import com.gws.auto.mobile.android.domain.engine.modules.CreateGmailDraftModule
import com.gws.auto.mobile.android.domain.engine.modules.DefineVariableModule
import com.gws.auto.mobile.android.domain.engine.modules.DriveCopyFileModule
import com.gws.auto.mobile.android.domain.engine.modules.DriveCreateFolderModule
import com.gws.auto.mobile.android.domain.engine.modules.DriveMoveFileModule
import com.gws.auto.mobile.android.domain.engine.modules.DuplicateSpreadsheetModule
import com.gws.auto.mobile.android.domain.engine.modules.GetRelativeDateModule
import com.gws.auto.mobile.android.domain.engine.modules.GmailSendEmailModule
import com.gws.auto.mobile.android.domain.engine.modules.LogMessageModule
import com.gws.auto.mobile.android.domain.engine.modules.SheetsAppendRowModule
import com.gws.auto.mobile.android.domain.engine.modules.SheetsClearValuesModule
import com.gws.auto.mobile.android.domain.engine.modules.SheetsCreateNewModule
import com.gws.auto.mobile.android.domain.engine.modules.SheetsSetValueModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@Module
@InstallIn(SingletonComponent::class)
object WorkflowModule {

    @Provides
    @IntoMap
    @StringKey("DEFINE_VARIABLE")
    fun provideDefineVariableModule(module: DefineVariableModule): ModuleExecutor = module

    @Provides
    @IntoMap
    @StringKey("GET_RELATIVE_DATE")
    fun provideGetRelativeDateModule(module: GetRelativeDateModule): ModuleExecutor = module

    @Provides
    @IntoMap
    @StringKey("CREATE_GMAIL_DRAFT")
    fun provideCreateGmailDraftModule(module: CreateGmailDraftModule): ModuleExecutor = module

    @Provides
    @IntoMap
    @StringKey("DUPLICATE_SPREADSHEET")
    fun provideDuplicateSpreadsheetModule(module: DuplicateSpreadsheetModule): ModuleExecutor = module

    @Provides
    @IntoMap
    @StringKey("COPY_PASTE_SHEET_VALUES")
    fun provideCopyPasteSheetValuesModule(module: CopyPasteSheetValuesModule): ModuleExecutor = module

    @Provides
    @IntoMap
    @StringKey("LOG_MESSAGE")
    fun provideLogMessageModule(module: LogMessageModule): ModuleExecutor = module

    @Provides
    @IntoMap
    @StringKey("chat_post")
    fun provideChatPostModule(module: ChatPostModule): ModuleExecutor = module

    @Provides
    @IntoMap
    @StringKey("drive_create_folder")
    fun provideDriveCreateFolderModule(module: DriveCreateFolderModule): ModuleExecutor = module

    @Provides
    @IntoMap
    @StringKey("drive_copy_file")
    fun provideDriveCopyFileModule(module: DriveCopyFileModule): ModuleExecutor = module

    @Provides
    @IntoMap
    @StringKey("drive_move_file")
    fun provideDriveMoveFileModule(module: DriveMoveFileModule): ModuleExecutor = module

    @Provides
    @IntoMap
    @StringKey("gmail_send_email")
    fun provideGmailSendEmailModule(module: GmailSendEmailModule): ModuleExecutor = module

    @Provides
    @IntoMap
    @StringKey("sheets_create_new")
    fun provideSheetsCreateNewModule(module: SheetsCreateNewModule): ModuleExecutor = module

    @Provides
    @IntoMap
    @StringKey("sheets_set_value")
    fun provideSheetsSetValueModule(module: SheetsSetValueModule): ModuleExecutor = module

    @Provides
    @IntoMap
    @StringKey("sheets_append_row")
    fun provideSheetsAppendRowModule(module: SheetsAppendRowModule): ModuleExecutor = module

    @Provides
    @IntoMap
    @StringKey("sheets_clear_values")
    fun provideSheetsClearValuesModule(module: SheetsClearValuesModule): ModuleExecutor = module

    @Provides
    @IntoMap
    @StringKey("calendar_create_event")
    fun provideCalendarCreateEventModule(module: CalendarCreateEventModule): ModuleExecutor = module
}
