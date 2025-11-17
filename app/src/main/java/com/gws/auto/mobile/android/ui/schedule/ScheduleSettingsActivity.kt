package com.gws.auto.mobile.android.ui.schedule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleSettingsActivity : ComponentActivity() {

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
        private const val EXTRA_SCHEDULE_ID = "scheduleId"

        fun newIntent(context: Context, scheduleId: String? = null): Intent {
            val intent = Intent(context, ScheduleSettingsActivity::class.java)
            scheduleId?.let { intent.putExtra(EXTRA_SCHEDULE_ID, it) }
            return intent
        }
    }
}
