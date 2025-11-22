package com.gws.auto.mobile.android.ui.settings.app

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.data.repository.SettingsRepository
import com.gws.auto.mobile.android.databinding.FragmentAppSettingsBinding
import com.gws.auto.mobile.android.ui.history.HistoryViewModel
import com.gws.auto.mobile.android.ui.settings.tag.TagManagementFragment

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import coil.load
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.gws.auto.mobile.android.ui.MainSharedViewModel
import timber.log.Timber
import androidx.fragment.app.activityViewModels
import javax.inject.Inject

@AndroidEntryPoint
class AppSettingsFragment : Fragment() {

    private var _binding: FragmentAppSettingsBinding? = null
    private val binding get() = _binding!!
    private val historyViewModel: HistoryViewModel by viewModels()
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    private val createCsvLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.also { uri ->
                requireContext().contentResolver.openOutputStream(uri)?.use {
                    historyViewModel.exportHistoryToCsv(it)
                    Toast.makeText(requireContext(), "History exported successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupSpinners()
    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    private fun setupListeners() {
        binding.signInButton.setOnClickListener { signIn() }
        binding.signOutButton.setOnClickListener { signOut() }

        binding.tagManagementButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.settings_fragment_container, TagManagementFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.exportHistoryButton.setOnClickListener {
            launchCreateCsvIntent()
        }
        binding.clearHistoryButton.setOnClickListener {
            showClearHistoryConfirmationDialog()
        }
    }

    private fun launchCreateCsvIntent() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            putExtra(Intent.EXTRA_TITLE, "history_export_$timestamp.csv")
        }
        createCsvLauncher.launch(intent)
    }

    private fun showClearHistoryConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear All History")
            .setMessage("Are you sure you want to delete all execution history? This action cannot be undone.")
            .setPositiveButton("Clear All") { _, _ ->
                historyViewModel.clearHistory()
                Toast.makeText(requireContext(), "All history has been cleared", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun setupSpinners() {
        // First day of week
        val firstDayOfWeekAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.first_day_of_week_entries, R.layout.spinner_item_right_aligned).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.firstDayOfWeekSpinner.adapter = it
        }
        lifecycleScope.launch {
            val currentFirstDay = settingsRepository.firstDayOfWeek.first()
            val firstDayPosition = firstDayOfWeekAdapter.getPosition(currentFirstDay)
            binding.firstDayOfWeekSpinner.setSelection(firstDayPosition)
        }
        binding.firstDayOfWeekSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selection = parent.getItemAtPosition(position) as String
                lifecycleScope.launch { settingsRepository.saveFirstDayOfWeek(selection) }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Country for holidays
        val countryAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.country_entries, R.layout.spinner_item_right_aligned).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.countrySpinner.adapter = it
        }
        val countryValues = resources.getStringArray(R.array.country_values)
        lifecycleScope.launch {
            val currentCountry = settingsRepository.holidayCountry.first()
            val countryPosition = countryValues.indexOf(currentCountry)
            binding.countrySpinner.setSelection(if (countryPosition != -1) countryPosition else 0)
        }
        binding.countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selection = countryValues[position]
                lifecycleScope.launch { settingsRepository.saveHolidayCountry(selection) }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Language
        val languageAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.country_entries, R.layout.spinner_item_right_aligned).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.languageSpinner.adapter = it
        }
        val languageValues = resources.getStringArray(R.array.language_values_from_countries)
        lifecycleScope.launch {
            val currentLanguageTag = settingsRepository.language.first()
            val langPosition = languageValues.indexOf(currentLanguageTag)
            binding.languageSpinner.setSelection(if (langPosition != -1) langPosition else 0)
        }
        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selection = languageValues[position]
                lifecycleScope.launch { settingsRepository.saveLanguage(selection) }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Theme
        val themeAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.theme_entries, R.layout.spinner_item_right_aligned).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.themeSpinner.adapter = it
        }
        val themeValues = resources.getStringArray(R.array.theme_values)
        lifecycleScope.launch {
            val currentTheme = settingsRepository.theme.first()
            val themePosition = themeValues.indexOf(currentTheme)
            binding.themeSpinner.setSelection(if (themePosition != -1) themePosition else 0)
        }
        binding.themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selection = themeValues[position]
                lifecycleScope.launch { settingsRepository.saveTheme(selection) }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Highlight Color
        val highlightColorAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.highlight_color_entries, R.layout.spinner_item_right_aligned).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.highlightColorSpinner.adapter = it
        }
        val highlightColorValues = resources.getStringArray(R.array.highlight_color_values)
        lifecycleScope.launch {
            val currentHighlightColor = settingsRepository.highlightColor.first()
            val highlightColorPosition = highlightColorValues.indexOf(currentHighlightColor)
            binding.highlightColorSpinner.setSelection(if (highlightColorPosition != -1) highlightColorPosition else 0)
            updateColorIndicator(currentHighlightColor)
        }
        binding.highlightColorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selection = highlightColorValues[position]
                lifecycleScope.launch { settingsRepository.saveHighlightColor(selection) }
                updateColorIndicator(selection)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun updateColorIndicator(colorName: String) {
        val color = when(colorName) {
            "forest" -> "#386A1F"
            "ocean" -> "#00696F"
            "sakura" -> "#B14E69"
            else -> "#6750A4" // default
        }
        binding.colorIndicator.setBackgroundColor(Color.parseColor(color))
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
            mainSharedViewModel.setSignedInStatus(false)
            updateUI()
        }
    }

    private fun handleSignInResult(completedTask: com.google.android.gms.tasks.Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            mainSharedViewModel.setSignedInStatus(true)
            updateUI()
        } catch (e: ApiException) {
            Timber.w(e, "signInResult:failed code=" + e.statusCode)
            mainSharedViewModel.setSignedInStatus(false)
            updateUI()
        }
    }

    private fun updateUI() {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            binding.userName.text = account.displayName
            binding.userEmail.text = account.email
            binding.profileImage.load(account.photoUrl) { crossfade(true) }
            binding.signInButton.visibility = View.GONE
            binding.signOutButton.visibility = View.VISIBLE
        } else {
            binding.userName.text = "Not Signed In"
            binding.userEmail.text = ""
            binding.profileImage.setImageResource(R.mipmap.ic_launcher_round)
            binding.signInButton.visibility = View.VISIBLE
            binding.signOutButton.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
