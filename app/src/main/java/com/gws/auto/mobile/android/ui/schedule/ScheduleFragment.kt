package com.gws.auto.mobile.android.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme
import com.gws.auto.mobile.android.ui.theme.ThemeViewModel
import com.gws.auto.mobile.android.ui.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    private val viewModel: ScheduleViewModel by viewModels()
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()
    private val themeViewModel: ThemeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val theme by themeViewModel.theme.collectAsState()
                val highlightColor by themeViewModel.highlightColor.collectAsState()

                GWSAutoForAndroidTheme(
                    theme = theme,
                    highlightColor = highlightColor
                ) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        CalendarScreen(viewModel = viewModel)
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
