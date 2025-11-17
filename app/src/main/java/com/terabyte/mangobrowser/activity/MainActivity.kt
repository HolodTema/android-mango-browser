package com.terabyte.mangobrowser.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.terabyte.mangobrowser.R
import com.terabyte.mangobrowser.databinding.ActivityMainBinding
import com.terabyte.mangobrowser.datastore.SettingsDataStore
import com.terabyte.mangobrowser.ui.fragment.FavoriteTabsFragment
import com.terabyte.mangobrowser.ui.fragment.SearchHistoryFragment
import com.terabyte.mangobrowser.ui.fragment.SettingsFragment
import com.terabyte.mangobrowser.ui.fragment.WebFragment
import com.terabyte.mangobrowser.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by lazy {
        val factory = MainViewModel.Factory(SettingsDataStore(this))
        ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureLightDarkTheme()

        viewModel.liveDataCurrentFragmentLayout.observe(this) { layoutId ->
            val currentFragment =
                supportFragmentManager.findFragmentById(R.id.frame_main_fragment_container)

            val fragmentToSet = when (layoutId) {
                R.layout.fragment_web -> {
                    WebFragment.newInstance()
                }

                R.layout.fragment_settings -> {
                    SettingsFragment.newInstance()
                }

                R.layout.fragment_search_history -> {
                    SearchHistoryFragment.newInstance()
                }

                R.layout.fragment_favorite_tabs -> {
                    FavoriteTabsFragment.newInstance()
                }

                else -> {
                    WebFragment.newInstance()
                }
            }

            if (currentFragment == null) {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.frame_main_fragment_container, fragmentToSet)
                    .commit()
            } else {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_main_fragment_container, fragmentToSet)
                    .commit()
            }

        }
    }

    private fun configureLightDarkTheme() {
        lifecycleScope.launch {
            viewModel.flowDarkTheme.collect { isDarkTheme ->
                val themeMode = if(isDarkTheme) {
                    AppCompatDelegate.MODE_NIGHT_YES
                }
                else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
                AppCompatDelegate.setDefaultNightMode(themeMode)
            }
        }
    }


}