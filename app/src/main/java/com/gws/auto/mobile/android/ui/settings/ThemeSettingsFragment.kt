package com.gws.auto.mobile.android.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gws.auto.mobile.android.MainActivity
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.data.repository.SettingsRepository
import com.gws.auto.mobile.android.databinding.FragmentThemeSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ThemeSettingsFragment : Fragment() {

    private var _binding: FragmentThemeSettingsBinding? = null
    private val binding get() = _binding!!
    
    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThemeSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DataStoreから現在のテーマを読み込み
        viewLifecycleOwner.lifecycleScope.launch {
            val currentTheme = settingsRepository.theme.first()
            updateRadioButtons(currentTheme)
        }

        // テーマ変更リスナー
        binding.themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val newTheme = when (checkedId) {
                binding.themeLightRadio.id -> "Light"
                binding.themeDarkRadio.id -> "Dark"
                else -> "System"
            }
            
            // 現在のテーマと異なる場合のみ確認ダイアログを表示
            viewLifecycleOwner.lifecycleScope.launch {
                val currentTheme = settingsRepository.theme.first()
                if (newTheme != currentTheme) {
                    showThemeChangeConfirmationDialog(newTheme)
                }
            }
        }
    }

    /**
     * ラジオボタンの状態を更新
     */
    private fun updateRadioButtons(theme: String) {
        when (theme) {
            "Light" -> binding.themeLightRadio.isChecked = true
            "Dark" -> binding.themeDarkRadio.isChecked = true
            else -> binding.themeSystemRadio.isChecked = true
        }
    }

    /**
     * テーマ変更確認ダイアログを表示
     */
    private fun showThemeChangeConfirmationDialog(theme: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.theme_change_title)
            .setMessage(R.string.theme_change_message)
            .setPositiveButton(R.string.restart) { _, _ ->
                applyThemeAndRestart(theme)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                // 以前の選択に戻す
                viewLifecycleOwner.lifecycleScope.launch {
                    val currentTheme = settingsRepository.theme.first()
                    updateRadioButtons(currentTheme)
                }
            }
            .setCancelable(false)
            .show()
    }

    /**
     * テーマをDataStoreに保存してアプリケーションを再起動
     */
    private fun applyThemeAndRestart(theme: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            // DataStoreに保存
            settingsRepository.saveTheme(theme)
            
            // 開いているComposeドロップダウンメニューがクリーンアップされるまで待機
            // ライフサイクルイベント処理が完了するまでの時間を確保
            kotlinx.coroutines.delay(200)
            
            // MainActivityを再起動
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
