package com.foss.vidoplay.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.foss.vidoplay.data.local.dao.PlaylistDao
import com.foss.vidoplay.data.local.dao.PlaylistVideoDao
import com.foss.vidoplay.data.local.entity.PlaylistEntity
import com.foss.vidoplay.data.local.entity.PlaylistVideoEntity

@Database(
    entities = [PlaylistEntity::class, PlaylistVideoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistVideoDao(): PlaylistVideoDao
}