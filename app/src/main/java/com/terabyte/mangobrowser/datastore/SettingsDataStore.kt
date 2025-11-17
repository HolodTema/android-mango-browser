package com.terabyte.mangobrowser.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.terabyte.mangobrowser.web.HomePageTypes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataStore(private val context: Context) {

    val flowHomePage: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_HOME_PAGE] ?: HomePageTypes.GOOGLE
        }

    val flowDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_DARK_THEME] ?: false
        }

    val flowUseJS: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_USE_JS] ?: true
        }

    val flowUseZoom: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_USE_ZOOM] ?: false
        }

    suspend fun saveHomePage(homePageUrl: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_HOME_PAGE] = homePageUrl
        }
    }

    suspend fun saveDarkTheme(darkTheme: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DARK_THEME] = darkTheme
        }
    }

    suspend fun saveUseJS(useJS: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USE_JS] = useJS
        }
    }

    suspend fun saveUseZoom(useZoom: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USE_ZOOM] = useZoom
        }
    }

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

        private val KEY_HOME_PAGE = stringPreferencesKey("key_home_page")
        private val KEY_DARK_THEME = booleanPreferencesKey("key_dark_theme")
        private val KEY_USE_JS = booleanPreferencesKey("key_use_js")
        private val KEY_USE_ZOOM = booleanPreferencesKey("key_use_zoom")
    }
}