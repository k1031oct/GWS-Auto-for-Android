package com.gws.auto.mobile.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
fun AppMenubar(
    menus: Map<String, List<String>>,
    onMenuItemClick: (String, String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .padding(horizontal = 4.dp)
    ) {
        menus.forEach { (menuTitle, menuItems) ->
            var expanded by remember { mutableStateOf(false) }
            Box {
                TextButton(onClick = { expanded = true }) {
                    Text(menuTitle)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
                ) {

                    menuItems.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                onMenuItemClick(menuTitle, item)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppMenubarPreview() {
    GWSAutoForAndroidTheme {
        val menus = mapOf(
            "File" to listOf("New", "Open", "Save"),
            "Edit" to listOf("Cut", "Copy", "Paste")
        )

        Surface(modifier = Modifier.padding(16.dp)) {
            AppMenubar(menus = menus, onMenuItemClick = { menu, item ->
                println("Clicked $item from $menu")
            })
        }
    }
}
