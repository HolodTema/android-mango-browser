package com.terabyte.mangobrowser.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.terabyte.mangobrowser.databinding.ActivityIncognitoBinding
import com.terabyte.mangobrowser.viewmodel.IncognitoViewModel

class IncognitoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIncognitoBinding

    private val viewModel: IncognitoViewModel by lazy {
        ViewModelProvider(this)[IncognitoViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncognitoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        binding.buttonClose.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

}