package com.foss.vidoplay.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlist_videos", foreignKeys = [ForeignKey(
        entity = PlaylistEntity::class,
        parentColumns = ["id"],
        childColumns = ["playlistId"],
        onDelete = ForeignKey.CASCADE   // delete playlist → delete its videos too
    )], indices = [Index("playlistId")]
)
data class PlaylistVideoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playlistId: Long,
    val videoId: Long,
    val videoName: String,
    val videoPath: String,
    val videoDuration: Long,
    val videoSize: Long,
    val videoFolderPath: String,
    val addedAt: Long = System.currentTimeMillis()
)