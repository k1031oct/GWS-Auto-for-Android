package com.gws.auto.mobile.android.ui.dashboard

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.content.Intent
import com.gws.auto.mobile.android.ui.announcement.AnnouncementViewModel
import com.gws.auto.mobile.android.ui.settings.SettingsActivity
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme
import com.gws.auto.mobile.android.ui.theme.ThemeViewModel
import com.gws.auto.mobile.android.ui.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by activityViewModels()
    private val announcementViewModel: AnnouncementViewModel by activityViewModels()
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                val theme by themeViewModel.theme.collectAsStateWithLifecycle()
                val highlightColor by themeViewModel.highlightColor.collectAsStateWithLifecycle()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                GWSAutoForAndroidTheme(
                    theme = theme,
                    highlightColor = highlightColor
                ) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        DashboardScreen(
                            uiState = uiState,
                            onRefreshClicked = { viewModel.refresh() },
                            onAnnouncementClicked = {
                                announcementViewModel.markAllAsRead()
                                val intent = Intent(requireContext(), SettingsActivity::class.java)
                                intent.putExtra("fragment_to_load", "announcement")
                                startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.searchQuery.collect { query ->
                viewModel.setSearchQuery(query)
            }
        }
    }
}
