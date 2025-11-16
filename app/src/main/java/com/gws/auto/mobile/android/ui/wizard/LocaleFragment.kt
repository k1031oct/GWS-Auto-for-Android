package com.gws.auto.mobile.android.ui.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentWizardLocaleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocaleFragment : Fragment() {

    private var _binding: FragmentWizardLocaleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WizardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWizardLocaleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Country Spinner
        val countryAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.country_entries, android.R.layout.simple_spinner_item)
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.countrySpinner.adapter = countryAdapter
        binding.countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val countryValues = resources.getStringArray(R.array.country_values)
                viewModel.setCountryAndLanguage(countryValues[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
