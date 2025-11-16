package com.gws.auto.mobile.android.domain.service

import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.ValueRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class SheetsApiService @Inject constructor(private val authorizer: GoogleApiAuthorizer) {

    private suspend fun getService(): Sheets {
        val credential = authorizer.getCredential(listOf(SheetsScopes.SPREADSHEETS))
        return Sheets.Builder(authorizer.httpTransport, authorizer.jsonFactory, credential)
            .setApplicationName("GWS Auto for Android")
            .build()
    }

    @Throws(IOException::class)
    suspend fun createSpreadsheet(title: String, parentFolderId: String?): Spreadsheet = withContext(Dispatchers.IO) {
        val spreadsheet = Spreadsheet().setProperties(com.google.api.services.sheets.v4.model.SpreadsheetProperties().setTitle(title))
        getService().spreadsheets().create(spreadsheet).execute()
    }

    @Throws(IOException::class)
    suspend fun getValues(spreadsheetId: String, range: String): ValueRange = withContext(Dispatchers.IO) {
        Timber.d("Getting values from spreadsheet: $spreadsheetId, range: $range")
        getService().spreadsheets().values().get(spreadsheetId, range).execute()
    }

    @Throws(IOException::class)
    suspend fun updateValues(spreadsheetId: String, range: String, values: ValueRange) = withContext(Dispatchers.IO) {
        Timber.d("Updating values to spreadsheet: $spreadsheetId, range: $range")
        getService().spreadsheets().values().update(spreadsheetId, range, values)
            .setValueInputOption("USER_ENTERED")
            .execute()
    }

    @Throws(IOException::class)
    suspend fun appendRow(spreadsheetId: String, sheetName: String, values: List<Any>) = withContext(Dispatchers.IO) {
        val valueRange = ValueRange().setValues(listOf(values))
        getService().spreadsheets().values().append(spreadsheetId, sheetName, valueRange)
            .setValueInputOption("USER_ENTERED")
            .setInsertDataOption("INSERT_ROWS")
            .execute()
    }

    @Throws(IOException::class)
    suspend fun clearValues(spreadsheetId: String, range: String) = withContext(Dispatchers.IO) {
        getService().spreadsheets().values().clear(spreadsheetId, range, com.google.api.services.sheets.v4.model.ClearValuesRequest()).execute()
    }
}
