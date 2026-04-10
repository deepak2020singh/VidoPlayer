package com.foss.vidoplay.domain.model

import android.net.Uri

data class VideoFile(
    val id: Long,
    val name: String,
    val path: String,
    val size: Long,
    val duration: Long,
    val dateAdded: Long,
    val folderPath: String,
    val folderName: String,
    val uri: Uri,
    val thumbnailUri: Uri? = null
)


data class VideoFolder(
    val name: String,
    val path: String,
    val videos: List<VideoFile>,
    val videoCount: Int,
    val thumbnailPath: String?,
    val thumbnailUri: Uri?,
    val totalSize: Long,
    val dateModified: Long = 0L
)





