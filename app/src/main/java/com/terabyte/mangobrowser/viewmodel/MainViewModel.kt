package com.terabyte.mangobrowser.viewmodel

import androidx.annotation.LayoutRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.terabyte.mangobrowser.R
import com.terabyte.mangobrowser.datastore.SettingsDataStore
import com.terabyte.mangobrowser.web.HomePageTypes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val dataStore: SettingsDataStore) : ViewModel() {

    private val _liveDataCurrentFragmentLayout = MutableLiveData<Int>(R.layout.fragment_web)
    val liveDataCurrentFragmentLayout: LiveData<Int> = _liveDataCurrentFragmentLayout

    private val _liveDataCurrentWebUrl = MutableLiveData<String>(HomePageTypes.GOOGLE)
    val liveDataCurrentWebUrl: LiveData<String> = _liveDataCurrentWebUrl

    val flowHomePage: StateFlow<String> = dataStore.flowHomePage
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomePageTypes.GOOGLE
        )

    val flowDarkTheme: StateFlow<Boolean> = dataStore.flowDarkTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val flowUseJS: StateFlow<Boolean> = dataStore.flowUseJS
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun saveHomePage(homePageUrl: String) {
        viewModelScope.launch {
            dataStore.saveHomePage(homePageUrl)
        }
    }

    fun saveDarkTheme(darkTheme: Boolean) {
        viewModelScope.launch {
            dataStore.saveDarkTheme(darkTheme)
        }
    }

    fun saveUseJS(useJS: Boolean) {
        viewModelScope.launch {
            dataStore.saveUseJS(useJS)
        }
    }

    fun setFragment(@LayoutRes layoutId: Int) {
        _liveDataCurrentFragmentLayout.value = layoutId
    }

    fun setWebUrl(url: String) {
        _liveDataCurrentWebUrl.value = url
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val dataStore: SettingsDataStore): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(dataStore) as T
        }
    }

}