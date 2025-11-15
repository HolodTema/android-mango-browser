package com.terabyte.mangobrowser.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.terabyte.mangobrowser.R
import com.terabyte.mangobrowser.databinding.FragmentWebBinding
import com.terabyte.mangobrowser.viewmodel.MainViewModel
import com.terabyte.mangobrowser.web.CustomWebViewClient

class WebFragment : Fragment() {
    private lateinit var binding: FragmentWebBinding

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWebBinding.inflate(inflater, container, false)

        binding.webView.apply {
            webViewClient = CustomWebViewClient()
            settings.javaScriptEnabled = true
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.buttonSettings.setOnClickListener {
            viewModel.setFragment(R.layout.fragment_settings)
        }
        binding.buttonOpenedTabs.setOnClickListener {
            viewModel.setFragment(R.layout.fragment_opened_tabs)
        }
        binding.buttonFavoriteTabs.setOnClickListener {
            viewModel.setFragment(R.layout.fragment_favorite_tabs)
        }

        binding.buttonSearch.setOnClickListener {
            val url = binding.editWebRequest.text.toString()
            if (viewModel.liveDataCurrentWebUrl.value != url) {
                viewModel.setWebUrl(url)
            }
        }

        binding.editWebRequest.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                binding.buttonSearch.isEnabled = s != null && s.isNotBlank()
            }
        })

        viewModel.liveDataCurrentWebUrl.observe(viewLifecycleOwner) {
            binding.editWebRequest.setText(it)
            binding.webView.loadUrl(it)
        }
    }

    companion object {
        fun newInstance(): WebFragment {
            return WebFragment()
        }
    }
}