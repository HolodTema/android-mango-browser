package com.terabyte.mangobrowser.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.terabyte.mangobrowser.databinding.BottomSheetAboutIncognitoBinding
import com.terabyte.mangobrowser.databinding.BottomSheetAddFavoriteBinding
import com.terabyte.mangobrowser.datastore.SettingsDataStore
import com.terabyte.mangobrowser.viewmodel.MainViewModel
import kotlinx.coroutines.flow.MutableSharedFlow

class AboutIncognitoBottomSheet :
    BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetAboutIncognitoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAboutIncognitoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.buttonCloseBottomSheet.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val FRAGMENT_TAG = "AboutIncognitoBottomSheet"

        fun newInstance(): AboutIncognitoBottomSheet {
            return AboutIncognitoBottomSheet()
        }
    }
}