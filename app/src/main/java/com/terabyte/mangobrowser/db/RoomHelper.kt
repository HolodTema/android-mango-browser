package com.terabyte.mangobrowser.db

import android.content.Context
import androidx.room.Room
import com.terabyte.mangobrowser.ROOM_DB_NAME

class RoomHelper private constructor(context: Context) {
    val db = Room.databaseBuilder(context, AppDatabase::class.java, ROOM_DB_NAME)
        .build()

    suspend fun insertFavoriteTab(favoriteTab: FavoriteTab) {
        db.favoriteTabDao().insert(favoriteTab)
    }

    companion object {
        private lateinit var instance: RoomHelper

        fun init(context: Context) {
            instance = RoomHelper(context)
        }

        fun get(): RoomHelper {
            return instance
        }
    }
}