package com.gws.auto.mobile.android.domain.service

import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.drive.DriveScopes
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.sheets.v4.SheetsScopes

/**
 * Defines the API scopes required for Google services used in the application.
 * This sealed class provides a type-safe way to manage OAuth 2.0 scopes.
 */
sealed class Scope(val scopeUri: String) {
    // Google Chat: Permission to run as a chatbot.
    object ChatBot : Scope("https://www.googleapis.com/auth/chat.bot")

    // Google Calendar: Read-only access to calendars.
    object CalendarReadOnly : Scope(CalendarScopes.CALENDAR_READONLY)

    // Google Calendar: Full access to events, for creation and modification.
    object CalendarFullAccess : Scope(CalendarScopes.CALENDAR)

    // Google Drive: Full access to user's files for reading, creating, and modifying.
    object DriveFullAccess : Scope(DriveScopes.DRIVE)

    // Gmail: Permission to send emails on behalf of the user.
    object GmailSend : Scope(GmailScopes.GMAIL_SEND)

    // Google Sheets: Full access to spreadsheets for reading and writing data.
    object SheetsFullAccess : Scope(SheetsScopes.SPREADSHEETS)
}
