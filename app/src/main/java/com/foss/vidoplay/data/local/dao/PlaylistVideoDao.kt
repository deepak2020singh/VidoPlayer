package com.foss.vidoplay.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.foss.vidoplay.data.local.entity.PlaylistVideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistVideoDao {
    @Query("SELECT * FROM playlist_videos WHERE playlistId = :playlistId ORDER BY addedAt DESC")
    fun getVideosForPlaylist(playlistId: Long): Flow<List<PlaylistVideoEntity>>

    @Query("SELECT * FROM playlist_videos ORDER BY addedAt DESC LIMIT 20")
    fun getRecentVideos(): Flow<List<PlaylistVideoEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVideo(video: PlaylistVideoEntity)

    @Query("DELETE FROM playlist_videos WHERE playlistId = :playlistId AND videoId = :videoId")
    suspend fun removeVideoFromPlaylist(playlistId: Long, videoId: Long)

    @Query("DELETE FROM playlist_videos WHERE playlistId = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)

    @Query("SELECT COUNT(*) FROM playlist_videos WHERE playlistId = :playlistId")
    fun getVideoCount(playlistId: Long): Flow<Int>

    @Query("SELECT SUM(videoSize) FROM playlist_videos WHERE playlistId = :playlistId")
    fun getTotalSize(playlistId: Long): Flow<Long?>

    @Query("SELECT EXISTS(SELECT 1 FROM playlist_videos WHERE playlistId = :playlistId AND videoId = :videoId)")
    suspend fun isVideoInPlaylist(playlistId: Long, videoId: Long): Boolean
}