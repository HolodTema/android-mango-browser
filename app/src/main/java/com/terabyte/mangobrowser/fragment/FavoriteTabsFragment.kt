package com.terabyte.mangobrowser.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.terabyte.mangobrowser.databinding.FragmentFavoriteTabsBinding
import com.terabyte.mangobrowser.databinding.FragmentOpenedTabsBinding
import com.terabyte.mangobrowser.databinding.FragmentSettingsBinding
import com.terabyte.mangobrowser.databinding.FragmentWebBinding

class FavoriteTabsFragment: Fragment() {
    private lateinit var binding: FragmentFavoriteTabsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteTabsBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        fun newInstance(): FavoriteTabsFragment {
            return FavoriteTabsFragment()
        }
    }
}