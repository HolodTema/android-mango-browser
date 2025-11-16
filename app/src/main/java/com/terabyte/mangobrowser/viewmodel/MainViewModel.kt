package com.terabyte.mangobrowser.viewmodel

import androidx.annotation.LayoutRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.terabyte.mangobrowser.R
import com.terabyte.mangobrowser.datastore.SettingsDataStore
import com.terabyte.mangobrowser.db.FavoriteTab
import com.terabyte.mangobrowser.db.HistoryTab
import com.terabyte.mangobrowser.db.RoomHelper
import com.terabyte.mangobrowser.web.HomePageTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val dataStore: SettingsDataStore) : ViewModel() {

    private val _liveDataCurrentFragmentLayout = MutableLiveData<Int>(R.layout.fragment_web)
    val liveDataCurrentFragmentLayout: LiveData<Int> = _liveDataCurrentFragmentLayout

    private val _liveDataCurrentWebUrl = MutableLiveData<String>(HomePageTypes.GOOGLE)
    val liveDataCurrentWebUrl: LiveData<String> = _liveDataCurrentWebUrl

    private val _liveDataFavoriteTabs = MutableLiveData<List<FavoriteTab>>(emptyList())
    val liveDataFavoriteTabs: LiveData<List<FavoriteTab>> = _liveDataFavoriteTabs

    private val _liveDataHistoryTabs = MutableLiveData<List<HistoryTab>>(emptyList())
    val liveDataHistoryTabs: LiveData<List<HistoryTab>> = _liveDataHistoryTabs

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
        if (layoutId == R.layout.fragment_favorite_tabs) {
            loadFavoriteTabs()
        }
        if (layoutId == R.layout.fragment_search_history) {
            loadHistoryTabs()
        }
    }

    fun setWebUrl(url: String) {
        _liveDataCurrentWebUrl.value = url
    }

    fun addTabToFavorites(tabName: String, tabUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val favoriteTab = FavoriteTab(
                name = tabName,
                url = tabUrl
            )
            RoomHelper.get().insertFavoriteTab(favoriteTab)
        }
    }

    fun deleteFavoriteTab(favoriteTab: FavoriteTab) {
        viewModelScope.launch(Dispatchers.IO) {
            RoomHelper.get().deleteFavoriteTab(favoriteTab)
            withContext(Dispatchers.Main) {
                loadFavoriteTabs()
            }
        }
    }

    fun checkCurrentUrlInFavorites(listener: (Boolean) -> Unit) {
        val currentUrl = liveDataCurrentWebUrl.value
        viewModelScope.launch(Dispatchers.Default) {
            val favoriteTabs = RoomHelper.get().getAllFavoriteTabs()
            val isFavoriteTabFound = favoriteTabs.find { it.url == currentUrl } != null
            withContext(Dispatchers.Main) {
                listener(isFavoriteTabFound)
            }
        }
    }

    private fun loadFavoriteTabs() {
        viewModelScope.launch(Dispatchers.IO) {
            val favoriteTabs = RoomHelper.get().getAllFavoriteTabs()
            withContext(Dispatchers.Main) {
                _liveDataFavoriteTabs.value = favoriteTabs
            }
        }
    }


    fun deleteHistoryTab(historyTab: HistoryTab) {
        viewModelScope.launch(Dispatchers.IO) {
            RoomHelper.get().deleteHistoryTab(historyTab)
            withContext(Dispatchers.Main) {
                loadHistoryTabs()
            }
        }
    }

    private fun loadHistoryTabs() {
        viewModelScope.launch(Dispatchers.IO) {
            val historyTabs = RoomHelper.get().getAllHistoryTabs().sorted()
            withContext(Dispatchers.Main) {
                _liveDataHistoryTabs.value = historyTabs
            }
        }
    }

    fun insertHistoryTab(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val historyTab = HistoryTab(
                url = url
            )
            RoomHelper.get().insertHistoryTab(historyTab)
        }
    }

    fun deleteAllHistoryTabs() {
        viewModelScope.launch(Dispatchers.IO) {
            RoomHelper.get().deleteAllHistoryTabs()
            withContext(Dispatchers.Main) {
                _liveDataHistoryTabs.value = emptyList()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val dataStore: SettingsDataStore) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(dataStore) as T
        }
    }

}