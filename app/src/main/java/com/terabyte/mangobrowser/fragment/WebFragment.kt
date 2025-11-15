package com.terabyte.mangobrowser.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
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
        configureWebView()
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

    private fun configureWebView() {
        val webClient = CustomWebViewClient(
            pageStartedListener = {
                binding.progressWebLoading.visibility = View.VISIBLE
                binding.progressWebLoading.alpha = 0f
                binding.progressWebLoading.progress = 100
                binding.progressWebLoading.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
            },
            pageFinishedListener = {
                binding.progressWebLoading.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction {
                        binding.progressWebLoading.visibility = View.INVISIBLE
                        binding.progressWebLoading.progress = 0
                    }
                    .start()
            }
        )

        binding.webView.apply {
            webViewClient = webClient
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

            settings.javaScriptEnabled = true
            settings.loadsImagesAutomatically = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.displayZoomControls = false
            settings.builtInZoomControls = true
            settings.setSupportZoom(true)
            settings.domStorageEnabled = true
        }
    }

    companion object {
        fun newInstance(): WebFragment {
            return WebFragment()
        }
    }
}