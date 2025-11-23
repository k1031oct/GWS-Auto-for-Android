package com.gws.auto.mobile.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.compose.ui.graphics.toArgb
import com.gws.auto.mobile.android.ui.theme.*
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.gws.auto.mobile.android.data.repository.HistoryRepository
import com.gws.auto.mobile.android.data.repository.SettingsRepository
import com.gws.auto.mobile.android.databinding.ActivityMainBinding
import com.gws.auto.mobile.android.domain.model.History
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import com.gws.auto.mobile.android.ui.MainFragmentStateAdapter
import com.gws.auto.mobile.android.ui.MainSharedViewModel
import com.gws.auto.mobile.android.ui.announcement.AnnouncementViewModel
import com.gws.auto.mobile.android.ui.history.HistoryViewModel
import com.gws.auto.mobile.android.ui.schedule.ScheduleSettingsActivity
import com.gws.auto.mobile.android.ui.settings.SettingsActivity
import com.gws.auto.mobile.android.ui.workflow.WorkflowViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainSharedViewModel: MainSharedViewModel by viewModels()
    private lateinit var announcementViewModel: AnnouncementViewModel
    private val workflowViewModel: WorkflowViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()

    @Inject
    lateinit var historyRepository: HistoryRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var googleApiAuthorizer: GoogleApiAuthorizer
    
    private var settingsPopupMenu: PopupMenu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate called")

        lifecycleScope.launch {
            applySettings()
        }

        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        announcementViewModel = ViewModelProvider(this)[AnnouncementViewModel::class.java]

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupViewPager()
        setupBottomNavigation()
        setupSearchView()
        setupActionButtons()
        setupBackButtonHandler()
        observeViewModel()
        observeSettings()
    }

    override fun onResume() {
        super.onResume()
        mainSharedViewModel.setSignedInStatus(googleApiAuthorizer.isSignedIn())
    }
    
    override fun onPause() {
        super.onPause()
        settingsPopupMenu?.dismiss()
        settingsPopupMenu = null
    }

    private suspend fun applySettings() {
        val languageTag = settingsRepository.language.first()
        // Theme is now handled by observeSettings
        // val theme = settingsRepository.theme.first()

        val appLocale = LocaleListCompat.forLanguageTags(languageTag)
        AppCompatDelegate.setApplicationLocales(appLocale)

        // Initial theme application is also handled by observeSettings, 
        // but we might want to keep it here for immediate application on cold start if needed.
        // However, observeSettings starts immediately in onCreate, so it should be fine.
    }

    private fun applyTheme(theme: String) {
        when (theme) {
            "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun observeSettings() {
        settingsRepository.highlightColor
            .onEach { colorName ->
                applyHighlightColor(colorName)
            }
            .launchIn(lifecycleScope)

        settingsRepository.theme
            .onEach { theme ->
                applyTheme(theme)
            }
            .launchIn(lifecycleScope)
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

        val colorInt = color.toArgb()
        val highlightColorStateList = ColorStateList.valueOf(colorInt)
        val whiteColorStateList = ColorStateList.valueOf(Color.WHITE)
        val blackColorStateList = ColorStateList.valueOf(Color.BLACK)

        // Bottom Navigation Styling
        // Icons: always black
        binding.bottomNav.itemIconTintList = blackColorStateList
        // Text: black in light theme, white in dark theme
        binding.bottomNav.itemTextColor = if (isDarkTheme) whiteColorStateList else blackColorStateList
        binding.bottomNav.itemActiveIndicatorColor = highlightColorStateList

        // FAB Styling
        binding.fabMain.backgroundTintList = highlightColorStateList
        binding.fabMain.imageTintList = blackColorStateList
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // 検索フラグメントが表示されている時はキーボードを隠さない
        if (binding.searchFragmentContainer.visibility != View.VISIBLE && currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = MainFragmentStateAdapter(this)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (mainSharedViewModel.isSignedIn.value || position == 0) {
                    binding.bottomNav.menu.getItem(position).isChecked = true
                    mainSharedViewModel.setCurrentPage(position)
                    updateFab(position)
                } else {
                    binding.viewPager.currentItem = 0
                    showSignInRequiredDialog()
                }
            }
        })
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            val pageIndex = when (item.itemId) {
                R.id.navigation_workflow -> 0
                R.id.navigation_schedule -> 1
                R.id.navigation_history -> 2
                R.id.navigation_dashboard -> 3
                else -> 0
            }

            if (mainSharedViewModel.isSignedIn.value || pageIndex == 0) {
                binding.viewPager.setCurrentItem(pageIndex, true)
                true
            } else {
                showSignInRequiredDialog()
                false
            }
        }
    }

    private fun showSignInRequiredDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.sign_in_required)
            .setMessage(R.string.sign_in_prompt_message)
            .setPositiveButton(R.string.sign_in) { _, _ ->
                val intent = Intent(this, SettingsActivity::class.java)
                intent.putExtra("fragment_to_load", "app_settings")
                startActivity(intent)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mainSharedViewModel.setSearchQuery(query.orEmpty())
                if (!query.isNullOrBlank()) {
                    workflowViewModel.addSearchHistory(query)
                }
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mainSharedViewModel.setSearchQuery(newText.orEmpty())
                return true
            }
        })

        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            binding.searchFragmentContainer.visibility = if (hasFocus) View.VISIBLE else View.GONE
        }
    }

    private fun updateFab(position: Int) {
        when (position) {
            0 -> { // Workflow
                binding.fabMain.setImageResource(R.drawable.ic_create_new_folder)
                binding.fabMain.setOnClickListener { mainSharedViewModel.onFabClick() }
                binding.fabMain.show()
            }
            1 -> { // Schedule
                binding.fabMain.setImageResource(R.drawable.ic_add)
                binding.fabMain.setOnClickListener { 
                    startActivity(Intent(this, ScheduleSettingsActivity::class.java))
                }
                binding.fabMain.show()
            }
            else -> {
                binding.fabMain.hide()
            }
        }
    }

    private fun setupActionButtons() {
        binding.actionSettings.setOnClickListener { showSettingsMenu(it) }
    }

    private fun observeViewModel() {
        // All icon visibility is now managed within SearchFragment
    }

    private fun setupBackButtonHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.searchFragmentContainer.visibility == View.VISIBLE) {
                    binding.searchFragmentContainer.visibility = View.GONE
                    return
                }

                if (binding.viewPager.currentItem == 0) {
                    showExitConfirmationDialog()
                } else {
                    binding.viewPager.currentItem = 0
                }
            }
        })
    }

    private fun showExitConfirmationDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.exit_confirmation_title))
            .setMessage(getString(R.string.exit_confirmation_message))
            .setPositiveButton(getString(R.string.exit)) { _, _ -> finish() }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
        
        dialog.show()
        
        // Apply theme-based text colors to dialog buttons
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkTheme = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        val textColor = if (isDarkTheme) android.graphics.Color.WHITE else android.graphics.Color.BLACK
        
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(textColor)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(textColor)
    }

    private fun showSettingsMenu(anchor: View) {
        settingsPopupMenu?.dismiss()
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.settings_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            val intent = Intent(this, SettingsActivity::class.java)
            val fragmentKey = when (menuItem.itemId) {
                R.id.navigation_announcement -> "announcement"
                R.id.settings_application -> "app_settings"
                R.id.about_app -> "about_app"
                else -> null
            }
            if (fragmentKey != null) {
                if (fragmentKey == "announcement") {
                    announcementViewModel.markAllAsRead()
                }
                intent.putExtra("fragment_to_load", fragmentKey)
                startActivity(intent)
            }
            true
        }
        popup.setOnDismissListener {
            settingsPopupMenu = null
        }
        settingsPopupMenu = popup
        popup.show()
    }
}
