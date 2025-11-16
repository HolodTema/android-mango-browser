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
    private val noInternetListener: (Int) -> Unit
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return false
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