package com.terabyte.mangobrowser.web

fun isDeepLink(uri: String): Boolean {
    val deepLinkSchemes = listOf(
        "tg:", "telegram:",
        "whatsapp:",
        "viber:",
        "instagram:",
        "twitter:",
        "fb:", "facebook:",
        "market:"
    )
    return deepLinkSchemes.any {
        uri.startsWith(it)
    }
}

fun isSystemUri(uri: String): Boolean {
    val systemSchemes = listOf(
        "tel:", "sms:", "mailto:", "geo:", "maps:"
    )
    return systemSchemes.any {
        uri.startsWith(it)
    }
}

fun isIntentUri(uri: String): Boolean {
    return uri.startsWith("intent://")
}

fun addProtocolNameIfNecessary(url: String): String {
    val possiblePrefixes = listOf(
        "http://",
        "https://",
        "file://",
        "content://",
        "about:",
        "javascript:"
    )

    possiblePrefixes.forEach {
        if (url.startsWith(it)) {
            return url
        }
    }

    if (isLikelyWebAddress(url)) {
        return "https://$url"
    }
    return url
}

private fun isLikelyWebAddress(url: String): Boolean {
    val patterns = listOf(
        Regex("^[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}"),
        Regex("^[a-zA-Z0-9-]+\\.[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}"),
        Regex("^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}"),
        Regex("^localhost(:[0-9]+)?"),
        Regex("^[a-zA-Z0-9-]+(:[0-9]+)?")
    )
    return patterns.any {
        it.containsMatchIn(url)
    }
}