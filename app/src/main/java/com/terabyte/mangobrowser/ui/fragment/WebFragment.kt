package com.terabyte.mangobrowser.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebSettings
import android.widget.PopupMenu
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.terabyte.mangobrowser.R
import com.terabyte.mangobrowser.activity.IncognitoActivity
import com.terabyte.mangobrowser.databinding.FragmentWebBinding
import com.terabyte.mangobrowser.datastore.SettingsDataStore
import com.terabyte.mangobrowser.ui.dialog.AddFavoriteBottomSheet
import com.terabyte.mangobrowser.viewmodel.MainViewModel
import com.terabyte.mangobrowser.web.CustomWebChromeClient
import com.terabyte.mangobrowser.web.CustomWebViewClient
import com.terabyte.mangobrowser.web.addProtocolNameIfNecessary


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
            viewModel.setFragment(R.layout.fragment_search_history)
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
                hideNoInternetUI()
                val urlWithProtocolName = addProtocolNameIfNecessary(url)
                binding.webView.loadUrl(urlWithProtocolName)
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
        binding.editWebRequest.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                hideNoInternetUI()
                val url = binding.editWebRequest.text.toString()
                val urlWithProtocolName = addProtocolNameIfNecessary(url)
                binding.webView.loadUrl(urlWithProtocolName)
                true
            } else {
                false
            }
        }

        binding.buttonHomePage.setOnClickListener {
            hideNoInternetUI()
            binding.webView.loadUrl(viewModel.flowHomePage.value)
        }

        binding.buttonAddToFavorites.setOnClickListener {
            val bottomSheet = AddFavoriteBottomSheet.newInstance()
            bottomSheet.setFavoriteTabAddedListener {
                binding.buttonAddToFavorites.isEnabled = false
            }
            bottomSheet.show(
                requireActivity().supportFragmentManager,
                AddFavoriteBottomSheet.FRAGMENT_TAG
            )
        }

        binding.buttonMore.setOnClickListener {
            showPopupMenu()
        }

        viewModel.liveDataCurrentWebUrl.observe(viewLifecycleOwner) {
            binding.editWebRequest.setText(it)
            viewModel.checkCurrentUrlInFavorites {
                binding.buttonAddToFavorites.isEnabled = !it
            }
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
                    viewModel.insertHistoryTab(strUrl)
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
            },
            intentUriListener = ::overrideIntentUri,
            systemUriListener = ::overrideSystemUri,
            deepLinkListener = ::overrideDeepLink
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

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(requireActivity(), binding.buttonMore)
        popupMenu.menuInflater.inflate(R.menu.menu_web_fragment_more, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_reload_page -> {
                    onButtonReloadPagePressed()
                }

                R.id.menu_item_share_page -> {
                    onButtonSharePagePressed()
                }

                R.id.menu_item_incognito_mode -> {
                    onButtonIncognitoModePressed()
                }
            }
            true
        }

        popupMenu.show()
    }

    private fun onButtonReloadPagePressed() {
        hideNoInternetUI()
        binding.webView.loadUrl(
            viewModel.liveDataCurrentWebUrl.value ?: viewModel.flowHomePage.value
        )
    }

    private fun onButtonSharePagePressed() {
        val url = viewModel.liveDataCurrentWebUrl.value
        if (url != null && url.isNotBlank()) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, viewModel.liveDataCurrentWebUrl.value)

            val intentChooser = Intent.createChooser(intent, getString(R.string.share_page_url))
            startActivity(intentChooser)
        }
    }

    private fun onButtonIncognitoModePressed() {
        startActivity(Intent(requireActivity(), IncognitoActivity::class.java))
    }


    private fun hideKeyboard() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.editWebRequest.windowToken, 0)
    }

    private fun overrideSystemUri(uri: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
            startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun overrideDeepLink(uri: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
            startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun overrideIntentUri(uri: String): Boolean {
        return try {
            val intent = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME)

            val packageManager = requireActivity().packageManager
            val resolvedActivity = packageManager
                .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)

            if (resolvedActivity != null) {
                startActivity(intent)
                true
            }
            else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        fun newInstance(): WebFragment {
            return WebFragment()
        }
    }
}