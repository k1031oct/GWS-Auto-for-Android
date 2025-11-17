package com.gws.auto.mobile.android.domain.service

import com.gws.auto.mobile.android.domain.model.History
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class HistoryCsvExporter @Inject constructor() {

    fun export(historyList: List<History>, outputStream: OutputStream) {
        val writer = OutputStreamWriter(outputStream, "UTF-8")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())

        // Add UTF-8 BOM for better Excel compatibility
        writer.append('\uFEFF')

        // Header
        writer.append("\"ID\",\"Workflow ID\",\"Workflow Name\",\"Executed At\",\"Status\",\"Duration (ms)\",\"Bookmarked\",\"Logs\"\n")

        // Data
        historyList.forEach { history ->
            writer.append("\"${history.id}\",")
            writer.append("\"${history.workflowId}\",")
            writer.append("\"${escapeCsvField(history.workflowName)}\",")
            writer.append("\"${dateFormat.format(history.executedAt)}\",")
            writer.append("\"${history.status}\",")
            writer.append("\"${history.durationMs}\",")
            writer.append("\"${history.isBookmarked}\",")
            writer.append("\"${escapeCsvField(history.logs)}\"\n")
        }

        writer.flush()
        writer.close()
    }

    private fun escapeCsvField(field: String): String {
        return field.replace("\"", "\"\"")
    }
}
