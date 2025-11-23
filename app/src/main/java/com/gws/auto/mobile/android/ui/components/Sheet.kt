package com.gws.auto.mobile.android.ui.components

import androidx.compose.ui.res.stringResource
import com.gws.auto.mobile.android.R

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme
import kotlinx.coroutines.launch

@Composable
fun AppSheet(
    drawerState: DrawerState,
    sheetContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                sheetContent()
            }
        },
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun AppSheetPreview() {
    GWSAutoForAndroidTheme {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        Surface(modifier = Modifier.padding(16.dp)) {
            AppSheet(
                drawerState = drawerState,
                sheetContent = {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Sheet Content")
                        Spacer(modifier = Modifier.height(16.dp))
                        AppButton(onClick = { scope.launch { drawerState.close() } }, text = stringResource(R.string.close_sheet))
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    AppButton(onClick = { scope.launch { drawerState.open() } }, text = stringResource(R.string.open_sheet))
                }
            }
        }
    }
}
