package com.terabyte.mangobrowser.fragment

import android.os.Build
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
import com.terabyte.mangobrowser.datastore.SettingsDataStore
import com.terabyte.mangobrowser.viewmodel.MainViewModel
import com.terabyte.mangobrowser.web.CustomWebChromeClient
import com.terabyte.mangobrowser.web.CustomWebViewClient

class WebFragment : Fragment() {
    private lateinit var binding: FragmentWebBinding

    private val viewModel: MainViewModel by lazy {
        val factory = MainViewModel.Factory(SettingsDataStore(requireActivity()))
        ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
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

        binding.buttonClearEditWebRequest.setOnClickListener {
            binding.editWebRequest.setText("")
        }

        binding.buttonSearch.setOnClickListener {
            val url = binding.editWebRequest.text.toString()
            if (viewModel.liveDataCurrentWebUrl.value != url) {
                binding.webView.loadUrl(url)
            }
        }

        binding.buttonErrorConnectAgain.setOnClickListener {
            val url = binding.editWebRequest.text.toString()
            binding.webView.loadUrl(url)
            hideNoInternetUI()
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
        binding.editWebRequest.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.buttonAddToFavorites.visibility = View.INVISIBLE
                binding.buttonClearEditWebRequest.visibility = View.VISIBLE
            } else {
                binding.buttonAddToFavorites.visibility = View.VISIBLE
                binding.buttonClearEditWebRequest.visibility = View.GONE
            }
        }

        binding.buttonHomePage.setOnClickListener {
            binding.webView.loadUrl(viewModel.flowHomePage.value)
        }

        viewModel.liveDataCurrentWebUrl.observe(viewLifecycleOwner) {
            binding.editWebRequest.setText(it)
        }
    }

    private fun configureWebView() {
        val webViewClient = CustomWebViewClient(
            pageStartedListener = {
                binding.progressWebLoading.visibility = View.VISIBLE
                binding.progressWebLoading.alpha = 0f
                binding.progressWebLoading.progress = 100
                binding.progressWebLoading.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
            },
            pageFinishedListener = { strUrl ->
                strUrl?.let {
                    viewModel.setWebUrl(strUrl)
                }

                binding.progressWebLoading.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction {
                        binding.progressWebLoading.visibility = View.INVISIBLE
                        binding.progressWebLoading.progress = 0
                    }
                    .start()
            },
            noInternetListener = { errorCode ->
                showNoInternetUI()
            }
        )

        val webChromeClient = CustomWebChromeClient(
            progressChangedListener = {
                binding.progressWebLoading.progress = it
            }
        )

        binding.webView.apply {
            this.webViewClient = webViewClient
            this.webChromeClient = webChromeClient
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

            settings.javaScriptEnabled = viewModel.flowUseJS.value

            settings.loadsImagesAutomatically = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.displayZoomControls = false
            settings.builtInZoomControls = true
            settings.setSupportZoom(true)
            settings.domStorageEnabled = true

            settings.userAgentString = "Mozilla/5.0 (Linux; Android 10; " +
                    Build.MODEL + ") AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/91.0.4472.120 Mobile Safari/537.36"

            loadUrl(viewModel.liveDataCurrentWebUrl.value ?: viewModel.flowHomePage.value)
        }
    }

    private fun showNoInternetUI() {
        binding.webView.visibility = View.GONE
        binding.textWebErrorHeader.visibility = View.VISIBLE
        binding.textWebErrorDescription.visibility = View.VISIBLE
        binding.buttonErrorConnectAgain.visibility = View.VISIBLE
    }

    private fun hideNoInternetUI() {
        binding.webView.visibility = View.VISIBLE
        binding.textWebErrorHeader.visibility = View.GONE
        binding.textWebErrorDescription.visibility = View.GONE
        binding.buttonErrorConnectAgain.visibility = View.GONE
    }

    companion object {
        fun newInstance(): WebFragment {
            return WebFragment()
        }
    }
}