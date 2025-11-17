package com.terabyte.mangobrowser.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.terabyte.mangobrowser.R
import com.terabyte.mangobrowser.databinding.ActivityIncognitoBinding
import com.terabyte.mangobrowser.datastore.SettingsDataStore
import com.terabyte.mangobrowser.ui.dialog.AboutIncognitoBottomSheet
import com.terabyte.mangobrowser.viewmodel.IncognitoViewModel
import com.terabyte.mangobrowser.web.CustomWebChromeClient
import com.terabyte.mangobrowser.web.CustomWebViewClient
import com.terabyte.mangobrowser.web.addProtocolNameIfNecessary

class IncognitoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIncognitoBinding

    private val viewModel: IncognitoViewModel by lazy {
        val factory = IncognitoViewModel.Factory(SettingsDataStore(this))
        ViewModelProvider(this, factory)[IncognitoViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncognitoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureWebView()
    }

    override fun onStart() {
        super.onStart()

        binding.buttonClose.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.buttonAboutIncognito.setOnClickListener {
            val bottomSheet = AboutIncognitoBottomSheet.newInstance()
            bottomSheet.show(supportFragmentManager, AboutIncognitoBottomSheet.FRAGMENT_TAG)
        }

        binding.buttonHomePage.setOnClickListener {
            hideNoInternetUI()
            binding.webView.loadUrl(viewModel.flowHomePage.value)
        }

        binding.buttonMore.setOnClickListener {
            showPopupMenu()
        }

        binding.buttonSearch.setOnClickListener {
            val url = binding.editWebRequest.text.toString()

            if (viewModel.liveDataCurrentWebUrl.value != url) {
                hideNoInternetUI()
                val urlWithProtocolName = addProtocolNameIfNecessary(url)
                binding.webView.loadUrl(urlWithProtocolName)
            }
        }


        binding.buttonClearEditWebRequest.setOnClickListener {
            binding.editWebRequest.setText("")
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
                binding.buttonClearEditWebRequest.visibility = View.VISIBLE
            }
            else {
                binding.buttonClearEditWebRequest.visibility = View.INVISIBLE
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

        viewModel.liveDataCurrentWebUrl.observe(this) {
            binding.editWebRequest.setText(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearWebViewData()
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

        binding.webView.webViewClient = webViewClient
        binding.webView.webChromeClient = webChromeClient

        binding.webView.settings.apply {
            cacheMode = WebSettings.LOAD_NO_CACHE
            domStorageEnabled = false
            databaseEnabled = false
            savePassword = false
            saveFormData = false
            allowContentAccess = false
            allowFileAccess = false
            allowFileAccessFromFileURLs = false
            allowUniversalAccessFromFileURLs = false
            mediaPlaybackRequiresUserGesture = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            javaScriptEnabled = true

            userAgentString = "Mozilla/5.0 (Linux; Android 10; " +
                    Build.MODEL + ") AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/91.0.4472.120 Mobile Safari/537.36"
        }

        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(false)
        cookieManager.setAcceptThirdPartyCookies(binding.webView, false)

        binding.webView.loadUrl(
            viewModel.liveDataCurrentWebUrl.value ?: viewModel.flowHomePage.value
        )
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

            val resolvedActivity = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
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

    private fun clearWebViewData() {
        binding.webView.apply {
            clearCache(true)
            clearHistory()
            clearFormData()
            clearSslPreferences()

            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.flush()

            WebStorage.getInstance().deleteAllData()

            GeolocationPermissions.getInstance().clearAll()
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
        val popupMenu = PopupMenu(this, binding.buttonMore)
        popupMenu.menuInflater.inflate(R.menu.menu_incognito_activity_more, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_reload_page -> {
                    onButtonReloadPagePressed()
                }

                R.id.menu_item_share_page -> {
                    onButtonSharePagePressed()
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

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.editWebRequest.windowToken, 0)
    }

}