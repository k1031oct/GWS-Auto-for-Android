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
    suspend fun appendRows(spreadsheetId: String, sheetName: String, values: List<List<Any>>) = withContext(Dispatchers.IO) {
        val valueRange = ValueRange().setValues(values)
        getService().spreadsheets().values().append(spreadsheetId, sheetName, valueRange)
            .setValueInputOption("USER_ENTERED")
            .setInsertDataOption("INSERT_ROWS")
            .execute()
    }

    @Throws(IOException::class)
    suspend fun clearValues(spreadsheetId: String, range: String) = withContext(Dispatchers.IO) {
        getService().spreadsheets().values().clear(spreadsheetId, range, com.google.api.services.sheets.v4.model.ClearValuesRequest()).execute()
    }

    @Throws(IOException::class)
    suspend fun batchUpdate(spreadsheetId: String, requests: List<com.google.api.services.sheets.v4.model.Request>) = withContext(Dispatchers.IO) {
        val batchRequest = com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest().setRequests(requests)
        getService().spreadsheets().batchUpdate(spreadsheetId, batchRequest).execute()
    }

    @Throws(IOException::class)
    suspend fun getSpreadsheet(spreadsheetId: String): Spreadsheet = withContext(Dispatchers.IO) {
        getService().spreadsheets().get(spreadsheetId).execute()
    }

    @Throws(IOException::class)
    suspend fun exportSheetToPdf(spreadsheetId: String, sheetId: Int, destFolderId: String, fileName: String): String = withContext(Dispatchers.IO) {
        val credential = authorizer.getCredential(listOf(SheetsScopes.SPREADSHEETS)) ?: throw IllegalStateException("Not authenticated")
        val token = credential.token

        val urlString = "https://docs.google.com/spreadsheets/d/$spreadsheetId/export?format=pdf&gid=$sheetId"
        val url = java.net.URL(urlString)
        val connection = url.openConnection() as java.net.HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Authorization", "Bearer $token")

        if (connection.responseCode != 200) {
            throw IOException("Failed to export PDF: ${connection.responseCode} ${connection.responseMessage}")
        }

        val inputStream = connection.inputStream
        
        // Save to Drive
        // We need to use Drive API to upload this stream.
        // Since we are in SheetsApiService, we might need to inject DriveApiService or use a lower level approach.
        // But SheetsApiService doesn't know about DriveApiService.
        // I should return the InputStream or ByteArray, and let the Module handle the upload using DriveApiService.
        // But wait, I can't easily return InputStream across suspend boundaries if I close connection?
        // I'll read to ByteArray.
        
        val outputStream = java.io.ByteArrayOutputStream()
        inputStream.copyTo(outputStream)
        val pdfContent = outputStream.toByteArray()
        
        // Return the content, Module will upload it.
        // But wait, returning ByteArray might be large.
        // Ideally, I should stream it.
        // For now, I'll return ByteArray.
        
        // Actually, I can't return ByteArray easily to Module if I want to keep separation.
        // But wait, the Module has access to both Services.
        // So I should just have a method `downloadPdf(spreadsheetId, sheetId): ByteArray` in SheetsApiService.
        // And `uploadFile(folderId, name, content, mimeType)` in DriveApiService.
        
        // Let's rename this to `downloadSheetPdf`.
        return@withContext java.util.Base64.getEncoder().encodeToString(pdfContent)
    }
}
