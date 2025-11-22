package com.gws.auto.mobile.android.ui.wizard

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.gws.auto.mobile.android.MainActivity
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.ActivityWizardBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.content.res.Configuration
import com.gws.auto.mobile.android.ui.theme.ThemeViewModel

@AndroidEntryPoint
class WizardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWizardBinding
    private val viewModel: WizardViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWizardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pagerAdapter = WizardPagerAdapter(this)
        binding.wizardViewPager.adapter = pagerAdapter

        binding.wizardViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.backButton.isEnabled = position > 0
                if (position == pagerAdapter.itemCount - 1) {
                    binding.nextButton.setText(R.string.wizard_finish)
                } else {
                    binding.nextButton.setText(R.string.wizard_next)
                }
            }
        })

        binding.nextButton.setOnClickListener {
            if (binding.wizardViewPager.currentItem < pagerAdapter.itemCount - 1) {
                binding.wizardViewPager.currentItem += 1
            } else {
                viewModel.finishWizard()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        binding.backButton.setOnClickListener {
            binding.wizardViewPager.currentItem -= 1
        }
        
        // Observe theme and apply button text colors
        lifecycleScope.launch {
            themeViewModel.theme.collect { _ ->
                applyButtonTextColors()
            }
        }
    }
    
    private fun applyButtonTextColors() {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkTheme = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        val textColor = if (isDarkTheme) android.graphics.Color.WHITE else android.graphics.Color.BLACK
        
        binding.backButton.setTextColor(textColor)
        binding.nextButton.setTextColor(textColor)
    }
}
