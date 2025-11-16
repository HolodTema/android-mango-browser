package com.terabyte.mangobrowser.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryTabDao {
    @Insert
    fun insert(tab: HistoryTab)

    @Delete
    fun delete(tab: HistoryTab)

    @Query("SELECT * FROM history_tabs")
    fun getAll(): List<HistoryTab>

    @Query("DELETE FROM history_tabs")
    fun deleteAll()
}