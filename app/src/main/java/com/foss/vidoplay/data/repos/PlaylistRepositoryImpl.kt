package com.foss.vidoplay.data.repos

import com.foss.vidoplay.data.local.dao.PlaylistDao
import com.foss.vidoplay.data.local.dao.PlaylistVideoDao
import com.foss.vidoplay.data.local.entity.PlaylistEntity
import com.foss.vidoplay.data.local.entity.PlaylistVideoEntity
import com.foss.vidoplay.domain.model.Playlist
import com.foss.vidoplay.domain.model.PlaylistVideo
import com.foss.vidoplay.domain.model.VideoFile
import com.foss.vidoplay.domain.repo.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao, private val playlistVideoDao: PlaylistVideoDao
) : PlaylistRepository
{
    override fun getAllPlaylists(): Flow<List<Playlist>> =
        playlistDao.getAllPlaylists().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getVideosForPlaylist(playlistId: Long): Flow<List<PlaylistVideo>> =
        playlistVideoDao.getVideosForPlaylist(playlistId).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getRecentVideos(): Flow<List<PlaylistVideo>> =
        playlistVideoDao.getRecentVideos().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun createPlaylist(name: String, description: String): Long =
        playlistDao.insertPlaylist(
            PlaylistEntity(name = name, description = description)
        )

    override suspend fun updatePlaylist(id: Long, name: String, description: String) {
        playlistDao.getPlaylistById(id)?.let { existing ->
            playlistDao.updatePlaylist(existing.copy(name = name, description = description))
        }
    }

    override suspend fun deletePlaylist(id: Long) {
        playlistDao.deletePlaylistById(id)
    }

    override suspend fun addVideoToPlaylist(playlistId: Long, video: VideoFile) {
        playlistVideoDao.insertVideo(
            PlaylistVideoEntity(
                playlistId = playlistId,
                videoId = video.id,
                videoName = video.name,
                videoPath = video.path,
                videoDuration = video.duration,
                videoSize = video.size,
                videoFolderPath = video.folderPath
            )
        )
    }

    override suspend fun removeVideoFromPlaylist(playlistId: Long, videoId: Long) {
        playlistVideoDao.removeVideoFromPlaylist(playlistId, videoId)
    }

    override suspend fun isVideoInPlaylist(playlistId: Long, videoId: Long): Boolean =
        playlistVideoDao.isVideoInPlaylist(playlistId, videoId)

    private fun PlaylistEntity.toDomain() = Playlist(
        id = id,
        name = name,
        description = description,
        createdAt = createdAt,
        isDefault = isDefault
    )

    private fun PlaylistVideoEntity.toDomain() = PlaylistVideo(
        id = id,
        playlistId = playlistId,
        videoId = videoId,
        videoName = videoName,
        videoPath = videoPath,
        videoDuration = videoDuration,
        videoSize = videoSize,
        videoFolderPath = videoFolderPath,
        addedAt = addedAt
    )
}