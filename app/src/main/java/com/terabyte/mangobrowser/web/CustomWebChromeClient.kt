package com.terabyte.mangobrowser.web

import android.graphics.Bitmap
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class CustomWebChromeClient(
    private var filePathCallback: ValueCallback<Array<Uri>>?,
    private val progressChangedListener: (Int) -> Unit,
    private val fileChooserDialogListener: (FileChooserParams?)->Unit
) : WebChromeClient() {

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        progressChangedListener(newProgress)
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        this.filePathCallback?.onReceiveValue(null)
        this.filePathCallback = filePathCallback

        fileChooserDialogListener(fileChooserParams)
        return true
    }
}