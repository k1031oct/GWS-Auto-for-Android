package com.gws.auto.mobile.android.ui.settings.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentAccountConnectionsBinding
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AccountConnectionsFragment : Fragment() {

    private var _binding: FragmentAccountConnectionsBinding? = null
    private val binding get() = _binding!!

    @Inject
    @JvmField
    var auth: FirebaseAuth? = null

    @Inject
    lateinit var authorizer: GoogleApiAuthorizer

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        authorizer.handleSignInResult(result.data) {
            updateUI()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountConnectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    private fun updateUI() {
        val user = auth?.currentUser
        if (user != null) {
            binding.syncStatusText.text = getString(R.string.sync_enabled_with, user.email)
            binding.authButton.text = getString(R.string.sign_out_and_disable_sync)
            binding.authButton.setOnClickListener {
                signOut()
            }
        } else {
            binding.syncStatusText.text = getString(R.string.sync_disabled)
            binding.authButton.text = getString(R.string.sign_in_with_google_to_sync)
            binding.authButton.setOnClickListener {
                signInLauncher.launch(authorizer.getSignInIntent())
            }
        }
    }

    private fun signOut() {
        authorizer.signOut {
            auth?.signOut()
            Timber.i("User signed out successfully. Sync disabled.")
            activity?.runOnUiThread {
                updateUI()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
