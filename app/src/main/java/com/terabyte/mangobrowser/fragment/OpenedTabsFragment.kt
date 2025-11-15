package com.terabyte.mangobrowser.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.terabyte.mangobrowser.R
import com.terabyte.mangobrowser.databinding.FragmentOpenedTabsBinding
import com.terabyte.mangobrowser.databinding.FragmentSettingsBinding
import com.terabyte.mangobrowser.databinding.FragmentWebBinding
import com.terabyte.mangobrowser.viewmodel.MainViewModel

class OpenedTabsFragment: Fragment() {
    private lateinit var binding: FragmentOpenedTabsBinding

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOpenedTabsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.buttonBack.setOnClickListener {
            viewModel.setFragment(R.layout.fragment_web)
        }
    }

    companion object {
        fun newInstance(): OpenedTabsFragment {
            return OpenedTabsFragment()
        }
    }
}