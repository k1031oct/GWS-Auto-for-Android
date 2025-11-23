package com.gws.auto.mobile.android.ui.components

import androidx.compose.ui.res.stringResource
import com.gws.auto.mobile.android.R

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme

@Composable
fun GwsScrollArea(
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(end = 12.dp) // Make space for scrollbar
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GwsScrollAreaPreview() {
    GWSAutoForAndroidTheme {
        Surface(modifier = Modifier.padding(16.dp).height(200.dp)) {
            GwsScrollArea {
                Text(
                    text = stringResource(R.string.long_scrollable_text).repeat(20)
                )
            }
        }
    }
}
