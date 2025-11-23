package com.gws.auto.mobile.android.ui.components

import androidx.compose.ui.res.stringResource
import com.gws.auto.mobile.android.R

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
fun AppPopover(
    trigger: @Composable (onClick: () -> Unit) -> Unit,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        trigger { expanded = true }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
        ) {

            Column(modifier = Modifier.padding(8.dp)) {
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPopoverPreview() {
    GWSAutoForAndroidTheme {
        Surface(modifier = Modifier.padding(32.dp)) {
            AppPopover(
                trigger = { onClick ->
                    AppButton(onClick = onClick, text = stringResource(R.string.open_popover))
                },
                content = {
                    Column {
                        Text("This is the popover content.", style = MaterialTheme.typography.bodyMedium)
                        AppButton(onClick = { }, text = stringResource(R.string.action_in_popover))
                    }
                }
            )
        }
    }
}
