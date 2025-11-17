package com.terabyte.mangobrowser.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.terabyte.mangobrowser.datastore.SettingsDataStore
import com.terabyte.mangobrowser.web.HomePageTypes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class IncognitoViewModel(private val dataStore: SettingsDataStore): ViewModel() {
    private val _liveDataCurrentWebUrl = MutableLiveData<String>(HomePageTypes.GOOGLE)
    val liveDataCurrentWebUrl: LiveData<String> = _liveDataCurrentWebUrl

    val flowHomePage: StateFlow<String> = dataStore.flowHomePage
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomePageTypes.GOOGLE
        )

    fun setWebUrl(url: String) {
        _liveDataCurrentWebUrl.value = url
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val dataStore: SettingsDataStore): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return IncognitoViewModel(dataStore) as T
        }
    }
}