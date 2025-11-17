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
        prepareSiteContextMenuFeature(view)
        pageStartedListener()
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        enableClipboardPaste(view)
        enableSiteContextMenu(view)
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

    private fun enableClipboardPaste(webView: WebView?) {
        webView?.let {
            val jsCodeToEnablePaste = """
                try {
                    // Разрешаем paste для всех элементов ввода
                    document.addEventListener('paste', function(e) {
                        e.stopPropagation();
                    }, true);
                    
                    // Убираем ограничения на вставку
                    const inputs = document.querySelectorAll('input, textarea, [contenteditable="true"]');
                    inputs.forEach(input => {
                        input.addEventListener('paste', function(e) {
                            e.stopPropagation();
                        });
                    });
                } catch(e) {
                    console.log('Clipboard enable error: ' + e);
                }
            """.trimIndent()
            it.evaluateJavascript(jsCodeToEnablePaste, null)
        }
    }

    private fun prepareSiteContextMenuFeature(webView: WebView?) {
        webView?.let {
            val jsCodeToPrepareContextMenu = """
            // Блокируем блокировку контекстного меню на ранней стадии
            document.addEventListener('DOMContentLoaded', function() {
                // Убираем все обработчики, которые блокируют contextmenu
                document.removeEventListener('contextmenu', preventContextMenuHandlers);
                
                // Разрешаем выделение текста везде
                const style = document.createElement('style');
                style.textContent = `
                    * {
                        -webkit-user-select: text !important;
                        user-select: text !important;
                        -webkit-touch-callout: default !important;
                    }
                    input, textarea {
                        -webkit-user-select: text !important;
                        user-select: text !important;
                    }
                `;
                document.head.appendChild(style);
            });
            
            function preventContextMenuHandlers(e) {
                e.stopPropagation();
            }
        """.trimIndent()

            it.evaluateJavascript(jsCodeToPrepareContextMenu, null)
        }
    }

    private fun enableSiteContextMenu(webView: WebView?) {
        webView?.let {
            val jsCodeToEnableContextMenu = """
            // Полностью включаем контекстное меню
            try {
                // 1. Убираем все существующие блокировки contextmenu
                const originalAddEventListener = EventTarget.prototype.addEventListener;
                EventTarget.prototype.addEventListener = function(type, listener, options) {
                    if (type === 'contextmenu' && listener && listener.toString().includes('preventDefault')) {
                        console.log('Blocked contextmenu preventer');
                        return;
                    }
                    originalAddEventListener.call(this, type, listener, options);
                };
                
                // 2. Восстанавливаем стандартное поведение contextmenu
                document.addEventListener('contextmenu', function(e) {
                    // Разрешаем событию всплывать и выполняться стандартно
                    return true;
                }, true);
                
                // 3. Убираем CSS блокировки
                const disableStyles = [
                    '-webkit-touch-callout: none',
                    '-webkit-user-select: none', 
                    'user-select: none',
                    'pointer-events: none'
                ];
                
                disableStyles.forEach(styleRule => {
                    const elements = document.querySelectorAll('[style*="' + styleRule + '"]');
                    elements.forEach(el => {
                        el.style.cssText = el.style.cssText.replace(new RegExp(styleRule, 'g'), '');
                    });
                });
                
                // 4. Разрешаем выделение для всех элементов
                document.querySelectorAll('*').forEach(el => {
                    el.style.webkitUserSelect = 'text';
                    el.style.userSelect = 'text';
                    el.style.webkitTouchCallout = 'default';
                });
                
                console.log('Context menu fully enabled');
                
            } catch (error) {
                console.log('Error enabling context menu: ' + error);
            }
        """.trimIndent()

            it.evaluateJavascript(jsCodeToEnableContextMenu, null)
        }
    }

}