package com.gws.auto.mobile.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun AppDropdownMenu(
    trigger: @Composable (onClick: () -> Unit) -> Unit,
    menuItems: List<String>,
    onMenuItemClick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        trigger { expanded = true }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
        ) {
            menuItems.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onMenuItemClick(item)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AppDropdownMenuPreview() {
    GWSAutoForAndroidTheme {
        Surface(modifier = Modifier.padding(32.dp)) {
            AppDropdownMenu(
                trigger = { onClick ->
                    IconButton(onClick = onClick) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                },
                menuItems = listOf("New Tab", "New Window", "Settings"),
                onMenuItemClick = {
                    println("$it clicked")
                }
            )
        }
    }
}
