package com.gws.auto.mobile.android.ui.schedule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.SavedStateHandle
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleSettingsActivity : ComponentActivity() {

    // The viewModels() delegate will automatically handle the SavedStateHandle
    // when the activity is created with the intent extras.
    private val viewModel: ScheduleSettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            GWSAutoForAndroidTheme {
                ScheduleSettingsScreen(viewModel = viewModel, onSave = { finish() })
            }
        }
    }

    companion object {
        // The key used to pass the schedule ID in the Intent and retrieve it in the ViewModel
        const val EXTRA_SCHEDULE_ID = "scheduleId"

        fun newIntent(context: Context, scheduleId: String? = null): Intent {
            return Intent(context, ScheduleSettingsActivity::class.java).apply {
                scheduleId?.let { putExtra(EXTRA_SCHEDULE_ID, it) }
            }
        }
    }
}
