package com.terabyte.mangobrowser.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.terabyte.mangobrowser.ROOM_DB_VERSION

@Database(entities = [FavoriteTab::class, HistoryTab::class], version = ROOM_DB_VERSION)
@TypeConverters(FavoriteTabTypeConverters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun favoriteTabDao(): FavoriteTabDao

    abstract fun historyTabDao(): HistoryTabDao

}