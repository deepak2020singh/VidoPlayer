package com.foss.vidoplay.domain.repo

import com.foss.vidoplay.domain.model.VideoFile
import kotlinx.coroutines.flow.Flow


interface VideoRepository {
    fun getVideos(): Flow<List<VideoFile>>
}