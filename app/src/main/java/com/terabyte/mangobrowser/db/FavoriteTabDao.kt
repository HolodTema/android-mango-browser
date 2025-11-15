package com.terabyte.mangobrowser.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FavoriteTabDao {

    @Insert
    fun insert(tab: FavoriteTab)

    @Update
    fun update(tab: FavoriteTab)

    @Delete
    fun delete(tab: FavoriteTab)

    @Query("SELECT * FROM favorite_tabs")
    fun getAll(): List<FavoriteTab>
}