package com.gws.auto.mobile.android.ui.components

import androidx.compose.ui.res.stringResource
import com.gws.auto.mobile.android.R

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme

@Composable
fun GwsTable(
    modifier: Modifier = Modifier,
    header: @Composable (RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .border(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        if (header != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                content = header
            )
            HorizontalDivider()
        }
        Column(content = content)
    }
}

@Composable
fun GwsTableRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        content = content
    )
    HorizontalDivider()
}

@Composable
fun RowScope.GwsTableHead(
    text: String,
    modifier: Modifier = Modifier,
    weight: Float = 1f
) {
    Text(
        text = text,
        modifier = modifier
            .weight(weight)
            .padding(16.dp), // h-12 px-4 (approx)
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.onSurfaceVariant // text-muted-foreground
    )
}

@Composable
fun RowScope.GwsTableCell(
    text: String,
    modifier: Modifier = Modifier,
    weight: Float = 1f
) {
    Text(
        text = text,
        modifier = modifier
            .weight(weight)
            .padding(16.dp), // p-4
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun GwsTableCaption(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp), // mt-4
        style = MaterialTheme.typography.bodySmall, // text-sm
        color = MaterialTheme.colorScheme.onSurfaceVariant // text-muted-foreground
    )
}

@Preview(showBackground = true)
@Composable
fun GwsTablePreview() {
    GWSAutoForAndroidTheme {
        val invoices = listOf(
            Triple("INV001", "Paid", "$250.00"),
            Triple("INV002", "Pending", "$150.00"),
            Triple("INV003", "Unpaid", "$350.00"),
            Triple("INV004", "Paid", "$450.00")
        )

        Surface(modifier = Modifier.padding(16.dp)) {
            Column {
                GwsTable(
                    header = {
                        GwsTableHead(text = stringResource(R.string.invoice))
                        GwsTableHead(text = stringResource(R.string.status))
                        GwsTableHead(text = stringResource(R.string.amount), modifier = Modifier.padding(end=32.dp)) 
                    }
                ) {
                    invoices.forEach { (invoice, status, amount) ->
                        GwsTableRow {
                            GwsTableCell(text = invoice)
                            GwsTableCell(text = status)
                            GwsTableCell(text = amount, modifier = Modifier.padding(end=32.dp))
                        }
                    }
                }
                GwsTableCaption(text = stringResource(R.string.invoice_list_caption))
            }
        }
    }
}
