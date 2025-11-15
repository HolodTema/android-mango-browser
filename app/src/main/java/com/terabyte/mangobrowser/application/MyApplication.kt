package com.terabyte.mangobrowser.application

import android.app.Application
import com.terabyte.mangobrowser.db.RoomHelper

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        RoomHelper.init(applicationContext)
    }

}