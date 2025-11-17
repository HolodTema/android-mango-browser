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