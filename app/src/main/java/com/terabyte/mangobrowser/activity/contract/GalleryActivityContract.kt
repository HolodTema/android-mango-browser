package com.terabyte.mangobrowser.activity.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient.FileChooserParams
import androidx.activity.result.contract.ActivityResultContract
import com.terabyte.mangobrowser.R

class GalleryActivityContract : ActivityResultContract<FileChooserParams?, Array<Uri>?>() {

    override fun createIntent(context: Context, input: FileChooserParams?): Intent {
        val intent = input?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT)

        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        val strSelectImage = context.getString(R.string.select_image)
        return Intent.createChooser(intent, strSelectImage)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Array<Uri>? {
        if (resultCode != Activity.RESULT_OK) {
            return null
        }

        if (intent != null) {
            return getFilesFromDataIntent(intent)
        }
        return null
    }

    private fun getFilesFromDataIntent(intent: Intent): Array<Uri> {
        return if (intent.clipData != null) {
            val count = intent.clipData!!.itemCount
            Array(count) { i ->
                intent.clipData!!.getItemAt(i).uri
            }
        }
        else {
            arrayOf(intent.data!!)
        }
    }
}