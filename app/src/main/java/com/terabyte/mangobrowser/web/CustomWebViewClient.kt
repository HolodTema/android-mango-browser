package com.terabyte.mangobrowser.web

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.terabyte.mangobrowser.LOG_TAG_DEBUG

class CustomWebViewClient(
    private val pageStartedListener: () -> Unit,
    private val pageFinishedListener: (String?) -> Unit,
    private val noInternetListener: (Int) -> Unit,
    private val intentUriListener: (String) -> Boolean,
    private val systemUriListener: (String) -> Boolean,
    private val deepLinkListener: (String) -> Boolean
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url?.toString()
        if (url == null) {
            return false
        }

        return when {
            isIntentUri(url) -> {
                intentUriListener(url)
            }
            isDeepLink(url) -> {
                deepLinkListener(url)
            }
            isSystemUri(url) -> {
                systemUriListener(url)
            }
            else -> {
                false
            }
        }
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        pageStartedListener()
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        pageFinishedListener(url)
    }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
        val statusCode = errorResponse?.statusCode?.toString() ?: "unknown status code"
        Log.d(LOG_TAG_DEBUG, "onReceivedHttpError(): $statusCode")
    }

    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceError
    ) {
        super.onReceivedError(view, request, error)
        if (!request.isForMainFrame) {
            return
        }

        val code = error.errorCode
        if (code == ERROR_TIMEOUT || code == ERROR_CONNECT || code == ERROR_HOST_LOOKUP) {
            noInternetListener(code)
        }
    }

}