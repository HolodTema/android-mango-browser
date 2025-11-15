package com.terabyte.mangobrowser.viewmodel

import androidx.annotation.LayoutRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.terabyte.mangobrowser.DEFAULT_WEB_URL
import com.terabyte.mangobrowser.R

class MainViewModel : ViewModel() {

    private val _liveDataCurrentFragmentLayout = MutableLiveData<Int>(R.layout.fragment_web)
    val liveDataCurrentFragmentLayout: LiveData<Int> = _liveDataCurrentFragmentLayout

    private val _liveDataCurrentWebUrl = MutableLiveData<String>(DEFAULT_WEB_URL)
    val liveDataCurrentWebUrl: LiveData<String> = _liveDataCurrentWebUrl

    fun setFragment(@LayoutRes layoutId: Int) {
        _liveDataCurrentFragmentLayout.value = layoutId
    }

    fun setWebUrl(url: String) {
        _liveDataCurrentWebUrl.value = url
    }

}