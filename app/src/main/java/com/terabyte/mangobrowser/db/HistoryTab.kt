package com.terabyte.mangobrowser.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "history_tabs")
data class HistoryTab(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val url: String,
    val date: Date = Date()
) : Comparable<HistoryTab> {

    override fun compareTo(other: HistoryTab): Int {
        return date.time.compareTo(other.date.time)
    }

    fun equalDayTo(other: HistoryTab): Boolean {
        val millsInDay = 86400000
        return (date.time / millsInDay) == (date.time / millsInDay)
    }

}
