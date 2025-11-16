package com.gws.auto.mobile.android.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferencesKeys {
        val FIRST_DAY_OF_WEEK = stringPreferencesKey("first_day_of_week")
        val HOLIDAY_COUNTRY = stringPreferencesKey("holiday_country")
        val THEME = stringPreferencesKey("theme")
        val HIGHLIGHT_COLOR = stringPreferencesKey("highlight_color")
        val LANGUAGE = stringPreferencesKey("language")
        val WIZARD_COMPLETED = booleanPreferencesKey("wizard_completed")
    }

    val firstDayOfWeek: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.FIRST_DAY_OF_WEEK] ?: "Sunday" }
    val holidayCountry: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.HOLIDAY_COUNTRY] ?: "US" }
    val theme: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.THEME] ?: "System" }
    val highlightColor: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.HIGHLIGHT_COLOR] ?: "default" }
    val language: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.LANGUAGE] ?: "en" }
    val isWizardCompleted: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.WIZARD_COMPLETED] ?: false }

    suspend fun saveFirstDayOfWeek(firstDayOfWeek: String) {
        context.dataStore.edit { it[PreferencesKeys.FIRST_DAY_OF_WEEK] = firstDayOfWeek }
    }

    suspend fun saveHolidayCountry(country: String) {
        context.dataStore.edit { it[PreferencesKeys.HOLIDAY_COUNTRY] = country }
    }

    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { it[PreferencesKeys.THEME] = theme }
    }

    suspend fun saveHighlightColor(highlightColor: String) {
        context.dataStore.edit { it[PreferencesKeys.HIGHLIGHT_COLOR] = highlightColor }
    }

    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { it[PreferencesKeys.LANGUAGE] = language }
    }

    suspend fun setWizardCompleted(completed: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.WIZARD_COMPLETED] = completed }
    }
}
