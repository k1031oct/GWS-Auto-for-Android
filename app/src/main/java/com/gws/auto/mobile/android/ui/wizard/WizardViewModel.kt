package com.gws.auto.mobile.android.ui.wizard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WizardViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val theme: StateFlow<String> = settingsRepository.theme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "System")
    
    val highlightColor: StateFlow<String> = settingsRepository.highlightColor
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "default")

    fun setCountryAndLanguage(countryCode: String) {
        viewModelScope.launch {
            settingsRepository.saveHolidayCountry(countryCode)
            // For now, assume language code is the same as the country code in lower case
            settingsRepository.saveLanguage(countryCode.lowercase())
        }
    }

    fun setWeekStart(day: String) {
        viewModelScope.launch {
            settingsRepository.saveFirstDayOfWeek(day)
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            settingsRepository.saveTheme(theme)
        }
    }

    fun setHighlightColor(color: String) {
        viewModelScope.launch {
            settingsRepository.saveHighlightColor(color)
        }
    }

    fun finishWizard() {
        viewModelScope.launch {
            settingsRepository.setWizardCompleted(true)
        }
    }
}
