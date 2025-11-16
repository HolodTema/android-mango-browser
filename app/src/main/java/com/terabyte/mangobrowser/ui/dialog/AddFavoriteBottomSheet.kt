package com.terabyte.mangobrowser.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.terabyte.mangobrowser.databinding.BottomSheetAddFavoriteBinding
import com.terabyte.mangobrowser.datastore.SettingsDataStore
import com.terabyte.mangobrowser.viewmodel.MainViewModel

class AddFavoriteBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetAddFavoriteBinding

    private val viewModel: MainViewModel by lazy {
        val factory = MainViewModel.Factory(SettingsDataStore(requireActivity()))
        ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddFavoriteBinding.inflate(inflater, container, false)
        binding.textSiteUrl.text = viewModel.liveDataCurrentWebUrl.value
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.buttonCloseBottomSheet.setOnClickListener {
            dismiss()
        }

        binding.buttonAddToFavorites.setOnClickListener {
            val name = binding.editSiteName.text.toString()
            viewModel.addTabToFavorites(name, binding.textSiteUrl.text.toString())
            dismiss()
        }
    }

    companion object {
        const val FRAGMENT_TAG = "AddFavoriteBottomSheet"

        fun newInstance(): AddFavoriteBottomSheet {
            return AddFavoriteBottomSheet()
        }
    }
}