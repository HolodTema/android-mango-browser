package com.terabyte.mangobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.terabyte.mangobrowser.R
import com.terabyte.mangobrowser.databinding.FragmentFavoriteTabsBinding
import com.terabyte.mangobrowser.datastore.SettingsDataStore
import com.terabyte.mangobrowser.db.FavoriteTab
import com.terabyte.mangobrowser.ui.adapter.FavoriteTabAdapter
import com.terabyte.mangobrowser.ui.adapter.FavoriteTabItemCallbacks
import com.terabyte.mangobrowser.viewmodel.MainViewModel

class FavoriteTabsFragment: Fragment(), FavoriteTabItemCallbacks {
    private lateinit var binding: FragmentFavoriteTabsBinding

    private lateinit var adapter: FavoriteTabAdapter

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

        adapter = FavoriteTabAdapter(layoutInflater, this)
        binding.recyclerFavoriteTabs.adapter = adapter

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.buttonBack.setOnClickListener {
            viewModel.setFragment(R.layout.fragment_web)
        }

        viewModel.liveDataFavoriteTabs.observe(viewLifecycleOwner) { favoriteTabs ->
            adapter.submitList(favoriteTabs)
            if (favoriteTabs.isEmpty()) {
                binding.recyclerFavoriteTabs.visibility = View.GONE
                binding.textCaptionNoFavoriteTabs.visibility = View.VISIBLE
            }
            else {
                binding.recyclerFavoriteTabs.visibility = View.VISIBLE
                binding.textCaptionNoFavoriteTabs.visibility = View.GONE
            }
        }
    }

    override fun onFavoriteTabClicked(favoriteTab: FavoriteTab) {
        viewModel.setWebUrl(favoriteTab.url)
        viewModel.setFragment(R.layout.fragment_web)
    }

    override fun onFavoriteTabDelete(favoriteTab: FavoriteTab) {
        viewModel.deleteFavoriteTab(favoriteTab)
        viewModel.setFragment(R.layout.fragment_web)
    }

    companion object {
        fun newInstance(): FavoriteTabsFragment {
            return FavoriteTabsFragment()
        }
    }
}