package com.terabyte.mangobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.terabyte.mangobrowser.R
import com.terabyte.mangobrowser.databinding.FragmentSearchHistoryBinding
import com.terabyte.mangobrowser.datastore.SettingsDataStore
import com.terabyte.mangobrowser.db.HistoryTab
import com.terabyte.mangobrowser.ui.adapter.HistoryTabAdapter
import com.terabyte.mangobrowser.ui.adapter.HistoryTabItemCallbacks
import com.terabyte.mangobrowser.viewmodel.MainViewModel

class SearchHistoryFragment : Fragment(), HistoryTabItemCallbacks {
    private lateinit var binding: FragmentSearchHistoryBinding

    private lateinit var adapter: HistoryTabAdapter

    private val viewModel: MainViewModel by lazy {
        val factory = MainViewModel.Factory(SettingsDataStore(requireActivity()))
        ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchHistoryBinding.inflate(inflater, container, false)

        adapter = HistoryTabAdapter(layoutInflater, this)
        binding.recyclerSearchHistory.adapter = adapter

        binding.textAmountHistoryTabs.text = getString(R.string.amount_history_tabs, 0)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.buttonBack.setOnClickListener {
            viewModel.setFragment(R.layout.fragment_web)
        }

        binding.buttonClearAllHistory.setOnClickListener {
            viewModel.deleteAllHistoryTabs()
        }

        viewModel.liveDataHistoryTabs.observe(viewLifecycleOwner) { historyTabs ->
            adapter.submitList(historyTabs)
            binding.textAmountHistoryTabs.text =
                getString(R.string.amount_history_tabs, historyTabs.size)
        }
    }

    override fun onHistoryTabDelete(historyTab: HistoryTab) {
        viewModel.deleteHistoryTab(historyTab)
    }

    override fun onHistoryTabClicked(historyTab: HistoryTab) {
        viewModel.setWebUrl(historyTab.url)
        viewModel.setFragment(R.layout.fragment_web)
    }

    companion object {
        fun newInstance(): SearchHistoryFragment {
            return SearchHistoryFragment()
        }
    }
}