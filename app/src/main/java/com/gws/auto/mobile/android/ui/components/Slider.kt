package com.gws.auto.mobile.android.ui.components

import androidx.compose.ui.res.stringResource
import com.gws.auto.mobile.android.R

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
fun AppSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        valueRange = valueRange,
        steps = steps,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AppSliderPreview() {
    GWSAutoForAndroidTheme {
        var sliderValue by remember { mutableStateOf(0.5f) }
        Surface(modifier = Modifier.padding(16.dp)) {
            Column {
                AppSlider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it }
                )
                Text(text = stringResource(R.string.value_format, sliderValue))
            }
        }
    }
}
