package com.terabyte.mangobrowser.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.terabyte.mangobrowser.R
import com.terabyte.mangobrowser.databinding.FragmentWebBinding
import com.terabyte.mangobrowser.viewmodel.MainViewModel
import com.terabyte.mangobrowser.web.CustomWebViewClient

class WebFragment : Fragment() {
    private lateinit var binding: FragmentWebBinding

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWebBinding.inflate(inflater, container, false)

        binding.webView.apply {
            webViewClient = CustomWebViewClient()
        }
        
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.buttonSettings.setOnClickListener {
            viewModel.setFragment(R.layout.fragment_settings)
        }

        viewModel.liveDataCurrentWebUrl.observe(viewLifecycleOwner) {
            binding.webView.loadUrl(it)
        }
    }

    companion object {
        fun newInstance(): WebFragment {
            return WebFragment()
        }
    }
}