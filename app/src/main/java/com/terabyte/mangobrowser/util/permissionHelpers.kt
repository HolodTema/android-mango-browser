package com.terabyte.mangobrowser.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

fun Context.checkPermissionGranted(permission: String, listener: (Boolean) -> Unit) {
    val isGranted =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    listener(isGranted)
}

fun Context.checkAllPermissionsGranted(permissions: Array<String>): Boolean {
    return permissions.all { permission ->
       ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
}

fun Context.checkCameraPermission(listener: (Boolean) -> Unit) {
    checkPermissionGranted(Manifest.permission.CAMERA) {
        listener(it)
    }
}

fun getFileUploadingPermissionsList(): Array<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    }
    else {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}