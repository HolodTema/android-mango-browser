package com.terabyte.mangobrowser.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.terabyte.mangobrowser.R
import com.terabyte.mangobrowser.databinding.FragmentFavoriteTabsBinding
import com.terabyte.mangobrowser.databinding.FragmentOpenedTabsBinding
import com.terabyte.mangobrowser.databinding.FragmentSettingsBinding
import com.terabyte.mangobrowser.databinding.FragmentWebBinding
import com.terabyte.mangobrowser.datastore.SettingsDataStore
import com.terabyte.mangobrowser.viewmodel.MainViewModel

class FavoriteTabsFragment: Fragment() {
    private lateinit var binding: FragmentFavoriteTabsBinding

    private val viewModel: MainViewModel by lazy {
        val factory = MainViewModel.Factory(SettingsDataStore(requireActivity()))
        ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteTabsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.buttonBack.setOnClickListener {
            viewModel.setFragment(R.layout.fragment_web)
        }
    }

    companion object {
        fun newInstance(): FavoriteTabsFragment {
            return FavoriteTabsFragment()
        }
    }
}