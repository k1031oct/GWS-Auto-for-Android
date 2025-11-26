package com.gws.auto.mobile.android.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.ActivitySettingsBinding
import com.gws.auto.mobile.android.ui.announcement.AnnouncementFragment
import com.gws.auto.mobile.android.ui.settings.about.AboutAppFragment
import com.gws.auto.mobile.android.ui.settings.account.AccountConnectionsFragment
import com.gws.auto.mobile.android.ui.settings.app.AppSettingsFragment
import com.gws.auto.mobile.android.ui.settings.tag.TagManagementFragment
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.gws.auto.mobile.android.ui.theme.*
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.compose.ui.graphics.toArgb
import android.content.res.Configuration
import kotlinx.coroutines.launch
import com.gws.auto.mobile.android.ui.theme.ThemeViewModel

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, view.paddingBottom)
            insets
        }

        if (savedInstanceState == null) {
            val fragmentKey = intent.getStringExtra("fragment_to_load")
            if (fragmentKey != null) {
                val fragment = when (fragmentKey) {
                    "announcement" -> AnnouncementFragment()
                    "account_connections" -> AccountConnectionsFragment()
                    "app_settings" -> AppSettingsFragment()
                    "about_app" -> AboutAppFragment()
                    "tag_management" -> TagManagementFragment()
                    else -> MainSettingsFragment()
                }
                val title = getTitleForFragment(fragmentKey)
                val spannableTitle = SpannableString(title)
                spannableTitle.setSpan(ForegroundColorSpan(android.graphics.Color.WHITE), 0, title.length, 0)
                supportActionBar?.title = spannableTitle

                supportFragmentManager.beginTransaction()
                    .replace(R.id.settings_fragment_container, fragment)
                    .commit()
            } else {
                supportActionBar?.title = getString(R.string.title_settings)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.settings_fragment_container, MainSettingsFragment())
                    .commit()
            }
        }

        lifecycleScope.launch {
            themeViewModel.highlightColor.collect { colorName ->
                applyHighlightColor(colorName)
            }
        }
    }

    private fun applyHighlightColor(colorName: String) {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkTheme = nightModeFlags == Configuration.UI_MODE_NIGHT_YES

        val color = when (colorName) {
            "forest" -> if (isDarkTheme) ForestPrimaryDark else ForestPrimaryLight
            "ocean" -> if (isDarkTheme) OceanPrimaryDark else OceanPrimaryLight
            "sakura" -> if (isDarkTheme) SakuraPrimaryDark else SakuraPrimaryLight
            "neon" -> if (isDarkTheme) NeonPrimaryDark else NeonPrimaryLight
            else -> if (isDarkTheme) MonochromePrimaryDark else MonochromePrimaryLight
        }

        binding.toolbar.setBackgroundColor(color.toArgb())
        binding.toolbar.navigationIcon?.setTint(android.graphics.Color.WHITE)
    }

    private fun getTitleForFragment(key: String?): String {
        return when (key) {
            "announcement" -> getString(R.string.title_announcement)
            "account_connections" -> getString(R.string.title_account_connections)
            "app_settings" -> getString(R.string.title_app_settings)
            "about_app" -> getString(R.string.title_about_app)
            "tag_management" -> getString(R.string.manage_tags)
            else -> getString(R.string.title_settings)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
        return true
    }
}
