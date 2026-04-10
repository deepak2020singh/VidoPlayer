package com.foss.vidoplay.domain.repo

import com.foss.vidoplay.domain.model.Playlist
import com.foss.vidoplay.domain.model.PlaylistVideo
import com.foss.vidoplay.domain.model.VideoFile
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getVideosForPlaylist(playlistId: Long): Flow<List<PlaylistVideo>>
    fun getRecentVideos(): Flow<List<PlaylistVideo>>
    suspend fun createPlaylist(name: String, description: String): Long
    suspend fun updatePlaylist(id: Long, name: String, description: String)
    suspend fun deletePlaylist(id: Long)
    suspend fun addVideoToPlaylist(playlistId: Long, video: VideoFile)
    suspend fun removeVideoFromPlaylist(playlistId: Long, videoId: Long)
    suspend fun isVideoInPlaylist(playlistId: Long, videoId: Long): Boolean
}