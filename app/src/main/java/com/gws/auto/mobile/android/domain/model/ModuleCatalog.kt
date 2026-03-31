package com.gws.auto.mobile.android.domain.model

object ModuleCatalog {
    
    data class Folder(val name: String, val modules: List<Module>)

    val folders: List<Folder> by lazy {
        listOf(
            Folder("Logic & Flow", listOf(
                Module(id = "", type = "if_else", parameters = emptyMap()),
                Module(id = "", type = "delay", parameters = emptyMap()),
                Module(id = "", type = "run_workflow", parameters = emptyMap()),
                Module(id = "", type = "no_op", parameters = emptyMap())
            )),
            Folder("Variables & Data", listOf(
                Module(id = "", type = "DEFINE_VARIABLE", parameters = emptyMap()),
                Module(id = "", type = "GET_RELATIVE_DATE", parameters = emptyMap()),
                Module(id = "", type = "CALCULATE", parameters = emptyMap())
            )),
            Folder("System & UI", listOf(
                Module(id = "", type = "SHOW_TOAST", parameters = emptyMap()),
                Module(id = "", type = "SYSTEM_NOTIFICATION", parameters = emptyMap()),
                Module(id = "", type = "LOG_MESSAGE", parameters = emptyMap()),
                Module(id = "", type = "GET_CLIPBOARD", parameters = emptyMap()),
                Module(id = "", type = "SET_CLIPBOARD", parameters = emptyMap()),
                Module(id = "", type = "FILE_PICKER", parameters = emptyMap())
            )),
            Folder("Network", listOf(
                Module(id = "", type = "HTTP_REQUEST", parameters = emptyMap())
            )),
            Folder("Gmail", listOf(
                Module(id = "", type = "CREATE_GMAIL_DRAFT", parameters = emptyMap()),
                Module(id = "", type = "gmail_send_email", parameters = emptyMap()),
                Module(id = "", type = "gmail_save_attachments", parameters = emptyMap())
            )),
            Folder("Google Sheets", listOf(
                Module(id = "", type = "DUPLICATE_SPREADSHEET", parameters = emptyMap()),
                Module(id = "", type = "COPY_PASTE_SHEET_VALUES", parameters = emptyMap()),
                Module(id = "", type = "sheets_create_new", parameters = emptyMap()),
                Module(id = "", type = "sheets_set_value", parameters = emptyMap()),
                Module(id = "", type = "sheets_append_row", parameters = emptyMap()),
                Module(id = "", type = "sheets_clear_values", parameters = emptyMap()),
                Module(id = "", type = "sheets_unhide_rows_cols", parameters = emptyMap()),
                Module(id = "", type = "sheets_hide_rows_cols", parameters = emptyMap()),
                Module(id = "", type = "sheets_delete_rows_cols", parameters = emptyMap()),
                Module(id = "", type = "sheets_insert_rows_cols", parameters = emptyMap()),
                Module(id = "", type = "sheets_import_csv", parameters = emptyMap()),
                Module(id = "", type = "sheets_export_pdf", parameters = emptyMap()),
                Module(id = "", type = "sheets_export_excel", parameters = emptyMap())
            )),
            Folder("Google Drive", listOf(
                Module(id = "", type = "drive_create_folder", parameters = emptyMap()),
                Module(id = "", type = "drive_copy_file", parameters = emptyMap()),
                Module(id = "", type = "drive_move_file", parameters = emptyMap()),
                Module(id = "", type = "drive_convert_excel_to_sheets", parameters = emptyMap()),
                Module(id = "", type = "drive_delete_files_in_folder", parameters = emptyMap()),
                Module(id = "", type = "drive_detect_file", parameters = emptyMap()),
                Module(id = "", type = "drive_list_files_to_sheet", parameters = emptyMap())
            )),
            Folder("Google Calendar", listOf(
                Module(id = "", type = "calendar_create_event", parameters = emptyMap()),
                Module(id = "", type = "get_holidays", parameters = emptyMap())
            )),
            Folder("Google Chat", listOf(
                Module(id = "", type = "chat_post", parameters = emptyMap())
            )),
            Folder("Google Tasks", listOf(
                Module(id = "", type = "tasks_create_task", parameters = emptyMap())
            ))
        )
    }
}
