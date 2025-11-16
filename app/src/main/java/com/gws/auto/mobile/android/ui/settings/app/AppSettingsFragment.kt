package com.gws.auto.mobile.android.ui.settings.app

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.data.repository.SettingsRepository
import com.gws.auto.mobile.android.databinding.FragmentAppSettingsBinding
import com.gws.auto.mobile.android.ui.settings.tag.TagManagementFragment
import com.gws.auto.mobile.android.ui.settings.user.UserInfoFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppSettingsFragment : Fragment() {

    private var _binding: FragmentAppSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var settingsRepository: SettingsRepository

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

    private fun setupListeners() {
        binding.userInfoButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.settings_fragment_container, UserInfoFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.tagManagementButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.settings_fragment_container, TagManagementFragment())
                .addToBackStack(null)
                .commit()
        }
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
            val currentCountry = settingsRepository.country.first()
            val countryPosition = countryValues.indexOf(currentCountry)
            binding.countrySpinner.setSelection(if (countryPosition != -1) countryPosition else 0)
        }
        binding.countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selection = countryValues[position]
                lifecycleScope.launch { settingsRepository.saveCountry(selection) }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Language
        val languageAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.language_entries, R.layout.spinner_item_right_aligned).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.languageSpinner.adapter = it
        }
        val languageValues = resources.getStringArray(R.array.language_values)
        lifecycleScope.launch {
            val currentLanguageTag = settingsRepository.language.first()
            val langPosition = languageValues.indexOf(currentLanguageTag)
            binding.languageSpinner.setSelection(if (langPosition != -1) langPosition else 0)
        }
        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selection = languageValues[position]
                lifecycleScope.launch { settingsRepository.saveLanguage(selection) }
                Toast.makeText(requireContext(), "App restart required to apply changes", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), "App restart required to apply changes", Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
