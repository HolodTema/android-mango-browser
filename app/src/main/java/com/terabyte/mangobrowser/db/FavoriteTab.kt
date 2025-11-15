package com.terabyte.mangobrowser.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "favorite_tabs")
data class FavoriteTab(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val name: String,
    val url: String,
    val numberRequests: Int = 0,
    val date: Date = Date()
)
