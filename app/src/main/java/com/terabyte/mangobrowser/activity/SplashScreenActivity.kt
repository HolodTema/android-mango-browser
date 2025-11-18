package com.terabyte.mangobrowser.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.dataStore
import androidx.lifecycle.lifecycleScope
import com.terabyte.mangobrowser.R
import com.terabyte.mangobrowser.datastore.SettingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        lifecycleScope.launch {
            val dataStore = SettingsDataStore(this@SplashScreenActivity)

            dataStore.flowIsFirstLaunch.collect { isFirstLaunch ->
                if (isFirstLaunch) {
                    dataStore.saveIsFirstLaunch(false)
                    startActivityClearBackStack(OnboardingActivity::class.java)
                }
                else {
                    startActivityClearBackStack(MainActivity::class.java)
                }
                finish()
            }
        }
    }

    private fun startActivityClearBackStack(activityClass: Class<out Activity>) {
        val intent = Intent(this, activityClass)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
    }
}