package com.gws.auto.mobile.android.ui.settings.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.data.repository.HistoryRepository
import com.gws.auto.mobile.android.data.repository.SettingsRepository
import com.gws.auto.mobile.android.ui.settings.tag.TagManagementFragment
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme
import com.gws.auto.mobile.android.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppSettingsFragment : Fragment() {

    @Inject
    lateinit var settingsRepository: SettingsRepository
    
    @Inject
    lateinit var historyRepository: HistoryRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                val themeViewModel: ThemeViewModel by activityViewModels()
                val theme by themeViewModel.theme.collectAsStateWithLifecycle()
                val highlightColor by themeViewModel.highlightColor.collectAsStateWithLifecycle()
                
                GWSAutoForAndroidTheme(
                    theme = theme,
                    highlightColor = highlightColor
                ) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        AppSettingsScreen(
                            settingsRepository = settingsRepository,
                            historyRepository = historyRepository,
                            onNavigateToTags = {
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.settings_fragment_container, TagManagementFragment())
                                    .addToBackStack(null)
                                    .commit()
                            }
                        )
                    }
                }
            }
        }
    }
}
