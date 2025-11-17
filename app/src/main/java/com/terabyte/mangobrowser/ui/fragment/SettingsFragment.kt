package com.terabyte.mangobrowser.ui.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.terabyte.mangobrowser.R
import com.terabyte.mangobrowser.databinding.FragmentSettingsBinding
import com.terabyte.mangobrowser.datastore.SettingsDataStore
import com.terabyte.mangobrowser.viewmodel.MainViewModel
import com.terabyte.mangobrowser.web.HomePageTypes
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    private val viewModel: MainViewModel by lazy {
        val factory = MainViewModel.Factory(SettingsDataStore(requireActivity()))
        ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        configureSpinnerHomePages()

        try {
            val pInfo = requireActivity().packageManager.getPackageInfo(requireContext().packageName, 0)
            val version = pInfo.versionName
            binding.textAppVersion.text = getString(R.string.version, version)
        } catch (e: PackageManager.NameNotFoundException) {
            binding.textAppVersion.visibility = View.GONE
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.buttonBack.setOnClickListener {
            viewModel.setFragment(R.layout.fragment_web)
        }

        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveDarkTheme(isChecked)
        }

        binding.switchUseJs.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveUseJS(isChecked)
        }

        binding.switchUseZoom.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveUseZoom(isChecked)
        }

        lifecycleScope.launch {
            viewModel.flowHomePage.collect { homePageUrl ->
                val pageTypes = HomePageTypes.toList()
                binding.spinnerHomePage.setSelection(pageTypes.indexOf(homePageUrl))
            }

        }

        lifecycleScope.launch {
            viewModel.flowDarkTheme.collect { darkTheme ->
                binding.switchDarkTheme.isChecked = darkTheme
            }
        }

        lifecycleScope.launch {
            viewModel.flowUseJS.collect { useJS ->
                binding.switchUseJs.isChecked = useJS
            }
        }

        lifecycleScope.launch {
            viewModel.flowUseZoom.collect { useZoom ->
                binding.switchUseZoom.isChecked = useZoom
            }
        }
    }

    private fun configureSpinnerHomePages() {
        val homePages = HomePageTypes.toList()
        val adapter =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, homePages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerHomePage.adapter = adapter
        binding.spinnerHomePage.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val homePageType = parent.getItemAtPosition(position) as String
                    viewModel.saveHomePage(homePageType)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}