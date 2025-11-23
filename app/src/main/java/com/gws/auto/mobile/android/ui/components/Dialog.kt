package com.gws.auto.mobile.android.ui.components

import androidx.compose.ui.res.stringResource
import com.gws.auto.mobile.android.R

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme

@Composable
fun AppDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dialogTitle: String,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = { Text(dialogTitle, style = MaterialTheme.typography.headlineSmall) },
        text = content,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Close")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AppDialogPreview() {
    GWSAutoForAndroidTheme {
        var showDialog by remember { mutableStateOf(true) }

        Surface(modifier = Modifier.padding(16.dp)) {
            AppButton(onClick = { showDialog = true }, text = stringResource(R.string.show_dialog))
        }

        if (showDialog) {
            AppDialog(
                onDismissRequest = { showDialog = false },
                dialogTitle = "Generic Dialog"
            ) {
                Column {
                    Text("This is a generic dialog with custom content.")
                    AppButton(onClick = { showDialog = false }, text = stringResource(R.string.close))
                }
            }
        }
    }
}
