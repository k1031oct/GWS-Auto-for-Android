package com.gws.auto.mobile.android.ui.theme

import app.cash.turbine.test
import com.gws.auto.mobile.android.data.repository.SettingsRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ThemeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: ThemeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        settingsRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `highlightColor should emit the value from settingsRepository`() = runTest {
        // Given
        val highlightColorFlow = MutableStateFlow("blue")
        every { settingsRepository.highlightColor } returns highlightColorFlow
        viewModel = ThemeViewModel(settingsRepository)

        // When & Then
        viewModel.highlightColor.test {
            assertEquals("blue", awaitItem())
        }
    }

    @Test
    fun `theme should emit the value from settingsRepository`() = runTest {
        // Given
        val themeFlow = MutableStateFlow("Dark")
        every { settingsRepository.theme } returns themeFlow
        viewModel = ThemeViewModel(settingsRepository)

        // When & Then
        viewModel.theme.test {
            assertEquals("Dark", awaitItem())
        }
    }
}
