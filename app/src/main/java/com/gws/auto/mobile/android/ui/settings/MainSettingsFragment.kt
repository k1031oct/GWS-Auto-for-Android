package com.gws.auto.mobile.android.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentMainSettingsBinding
import com.gws.auto.mobile.android.ui.settings.about.AboutAppFragment
import com.gws.auto.mobile.android.ui.settings.tag.TagManagementFragment
import com.gws.auto.mobile.android.ui.wizard.ThemeFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainSettingsFragment : Fragment() {

    private var _binding: FragmentMainSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    @JvmField
    var auth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateUserInfo()
        setupSettingsList()
    }

    private fun populateUserInfo() {
        val user = auth?.currentUser
        if (user != null) {
            binding.userName.text = user.displayName
            binding.userEmail.text = user.email
            // binding.userAvatar.load(user.photoUrl) // Coil dependency needed
        } else {
            binding.userName.text = getString(R.string.user_name_placeholder)
            binding.userEmail.text = getString(R.string.user_email_placeholder)
        }
    }

    private fun setupSettingsList() {
        val settingsItems = listOf(
            SettingsItem(getString(R.string.settings_category_account)) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.settings_fragment_container, com.gws.auto.mobile.android.ui.settings.account.AccountConnectionsFragment())
                    .addToBackStack(null)
                    .commit()
            },
            SettingsItem(getString(R.string.settings_category_app)) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.settings_fragment_container, com.gws.auto.mobile.android.ui.settings.app.AppSettingsFragment())
                    .addToBackStack(null)
                    .commit()
            },
            SettingsItem(getString(R.string.settings_category_other)) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.settings_fragment_container, AboutAppFragment())
                    .addToBackStack(null)
                    .commit()
            }
        )

        binding.settingsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = SettingsAdapter(settingsItems)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
