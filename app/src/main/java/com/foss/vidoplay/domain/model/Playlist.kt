package com.foss.vidoplay.domain.model

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val videoCount: Int = 0,
    val totalSize: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val isDefault: Boolean = false
)


data class PlaylistVideo(
    val id: Long = 0,
    val playlistId: Long,
    val videoId: Long,
    val videoName: String,
    val videoPath: String,
    val videoDuration: Long,
    val videoSize: Long,
    val videoFolderPath: String,
    val addedAt: Long = System.currentTimeMillis()
)