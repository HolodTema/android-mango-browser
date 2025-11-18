package com.terabyte.mangobrowser.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
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
        configureOnBackPressedCallback()

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

            val isInBackStack = layoutId != R.layout.fragment_web

            val transaction = supportFragmentManager.beginTransaction()
            if (currentFragment == null) {
                transaction.add(R.id.frame_main_fragment_container, fragmentToSet)
            } else {
                transaction.replace(R.id.frame_main_fragment_container, fragmentToSet)
                if (isInBackStack) {
                    transaction.addToBackStack(null)
                }
            }
            transaction.commit()
        }
    }

    private fun configureLightDarkTheme() {
        lifecycleScope.launch {
            viewModel.flowDarkTheme.collect { isDarkTheme ->
                val themeMode = if (isDarkTheme) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
                AppCompatDelegate.setDefaultNightMode(themeMode)
            }
        }
    }

    private fun configureOnBackPressedCallback() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.liveDataCurrentFragmentLayout.value == R.layout.fragment_web) {
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }
}