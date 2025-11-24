package com.gws.auto.mobile.android.ui.components

import androidx.compose.ui.res.stringResource
import com.gws.auto.mobile.android.R

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
fun AppAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    modifier: Modifier = Modifier,
    confirmButtonText: String = "Continue",
    dismissButtonText: String = "Cancel"
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = { Text(text = dialogTitle, style = MaterialTheme.typography.headlineSmall) },
        text = { Text(text = dialogText, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            AppButton(
                onClick = onConfirmation,
                text = confirmButtonText,
            )
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(dismissButtonText)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AppAlertDialogPreview() {
    GWSAutoForAndroidTheme {
        var showDialog by remember { mutableStateOf(true) }

        Surface(modifier = Modifier.padding(16.dp)) {
            AppButton(onClick = { showDialog = true }, text = stringResource(R.string.show_alert_dialog))
        }

        if (showDialog) {
            AppAlertDialog(
                onDismissRequest = { showDialog = false },
                onConfirmation = { showDialog = false },
                dialogTitle = "Are you absolutely sure?",
                dialogText = "This action cannot be undone. This will permanently delete your account and remove your data from our servers."
            )
        }
    }
}
