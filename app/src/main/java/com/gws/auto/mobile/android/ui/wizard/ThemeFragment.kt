package com.gws.auto.mobile.android.ui.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme

class ThemeFragment : Fragment() {

    private val viewModel: WizardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val theme by viewModel.theme.collectAsState()
                val highlightColor by viewModel.highlightColor.collectAsState()

                GWSAutoForAndroidTheme(
                    theme = theme,
                    highlightColor = highlightColor
                ) {
                    Surface {
                        ThemeSettingsPage(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeSettingsPage(viewModel: WizardViewModel) {
    val currentTheme by viewModel.theme.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.wizard_theme_title),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.wizard_theme_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))

        Text("Theme", style = MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            ThemeRadioButton("Light", currentTheme) { viewModel.setTheme("Light") }
            ThemeRadioButton("Dark", currentTheme) { viewModel.setTheme("Dark") }
            ThemeRadioButton("System", currentTheme) { viewModel.setTheme("System") }
        }
    }
}

@Composable
private fun ThemeRadioButton(text: String, selectedTheme: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = (text == selectedTheme),
            onClick = onClick
        )
        Text(text = text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 4.dp))
    }
}
