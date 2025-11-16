package com.terabyte.mangobrowser.web

object HomePageTypes {
    const val YANDEX = "https://ya.ru"
    const val GOOGLE = "https://google.com"
    const val YOUTUBE = "https://youtube.com"
    const val DUCKDUCKGO = "https://duckduckgo.com/"
    const val WIKIPEDIA = "https://wikipedia.org"

    fun toList(): List<String> {
        return listOf(
            YANDEX, GOOGLE, YOUTUBE, DUCKDUCKGO, WIKIPEDIA
        )
    }
}