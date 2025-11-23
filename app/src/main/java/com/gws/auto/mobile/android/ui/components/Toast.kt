package com.gws.auto.mobile.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gws.auto.mobile.android.R
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme
import kotlinx.coroutines.launch

@Composable
fun AppToast(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false
) {
    val containerColor = if (isDestructive) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (isDestructive) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Snackbar(
        modifier = modifier.padding(12.dp),
        containerColor = containerColor,
        contentColor = contentColor,
        action = {
            snackbarData.visuals.actionLabel?.let { actionLabel ->
                Button(onClick = { snackbarData.performAction() }) {
                    Text(actionLabel)
                }
            }
        }
    ) {
        Text(snackbarData.visuals.message)
    }
}

@Composable
fun AppToastHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(hostState, modifier = modifier) { data ->
        // The toast message can be used to pass whether it's destructive
        val isDestructive = data.visuals.message.contains("error", ignoreCase = true)
        AppToast(snackbarData = data, isDestructive = isDestructive)
    }
}

@Preview(showBackground = true)
@Composable
fun GwsToastPreview() {
    GWSAutoForAndroidTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        Surface(modifier = Modifier.padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                AppButton(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Event has been created.",
                                actionLabel = "Undo"
                            )
                        }
                    },
                    text = stringResource(R.string.show_default_toast)
                )
                AppButton(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Error: Could not save post.",
                                actionLabel = "Retry"
                            )
                        }
                    },
                    text = stringResource(R.string.show_destructive_toast)
                )
            }
        }

        // Host for the toasts
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            AppToastHost(hostState = snackbarHostState)
        }
    }
}
