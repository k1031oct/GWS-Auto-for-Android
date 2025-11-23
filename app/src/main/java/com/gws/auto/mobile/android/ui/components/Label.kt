package com.gws.auto.mobile.android.ui.components

import androidx.compose.ui.res.stringResource
import com.gws.auto.mobile.android.R

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme

@Composable
fun GwsLabel(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Text(
        text = text,
        modifier = modifier.then(
            if (!enabled) Modifier.alpha(0.7f) else Modifier // peer-disabled:opacity-70
        ),
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Medium // font-medium
        ),
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Preview(showBackground = true)
@Composable
fun GwsLabelPreview() {
    GWSAutoForAndroidTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column {
                GwsLabel(text = stringResource(R.string.enabled_label))
                GwsLabel(text = stringResource(R.string.disabled_label), enabled = false)
            }
        }
    }
}
