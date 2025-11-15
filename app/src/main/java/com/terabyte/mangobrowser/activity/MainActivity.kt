package com.terabyte.mangobrowser.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.terabyte.mangobrowser.R
import com.terabyte.mangobrowser.databinding.ActivityMainBinding
import com.terabyte.mangobrowser.fragment.FavoriteTabsFragment
import com.terabyte.mangobrowser.fragment.OpenedTabsFragment
import com.terabyte.mangobrowser.fragment.SettingsFragment
import com.terabyte.mangobrowser.fragment.WebFragment
import com.terabyte.mangobrowser.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.liveDataCurrentFragmentLayout.observe(this) { layoutId ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_main_fragment_container)

            val fragmentToSet = when(layoutId) {
                R.layout.fragment_web -> {
                    WebFragment.newInstance()
                }
                R.layout.fragment_settings -> {
                    SettingsFragment.newInstance()
                }
                R.layout.fragment_opened_tabs -> {
                    OpenedTabsFragment.newInstance()
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
            }
            else {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_main_fragment_container, fragmentToSet)
                    .commit()
            }

        }
    }


}