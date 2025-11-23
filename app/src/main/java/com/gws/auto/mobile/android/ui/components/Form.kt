package com.gws.auto.mobile.android.ui.components

import androidx.compose.ui.res.stringResource
import com.gws.auto.mobile.android.R

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme

@Composable
fun AppFormItem(
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
    errorMessage: String? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        label?.invoke()
        content()
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AppFormLabel(
    text: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium,
        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    )
}

@Preview(showBackground = true)
@Composable
fun FormPreview() {
    GWSAutoForAndroidTheme {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Surface(modifier = Modifier.padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                AppFormItem(
                    label = { AppFormLabel(text = stringResource(R.string.username)) },
                    content = { AppInput(value = username, onValueChange = { username = it }) }
                )
                AppFormItem(
                    label = { AppFormLabel(text = stringResource(R.string.password)) },
                    content = { AppInput(value = password, onValueChange = { password = it }) },
                    errorMessage = if (password.length < 6 && password.isNotEmpty()) "Password must be at least 6 characters." else null
                )
                AppButton(onClick = { /* Handle form submission */ }, text = stringResource(R.string.submit))
            }
        }
    }
}
