package com.foss.vidoplay.data.repos


import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.foss.vidoplay.domain.model.VideoFile
import com.foss.vidoplay.domain.repo.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File

class VideoRepositoryImpl(
    private val context: Context
) : VideoRepository {

    companion object {
        private const val TAG = "VideoRepository"
    }

    override fun getVideos(): Flow<List<VideoFile>> = flow {
        val videos = withContext(Dispatchers.IO) { loadVideosFromStorage() }
        Log.d(TAG, "Loaded ${videos.size} videos")
        emit(videos)
    }

    @SuppressLint("UseKtx")
    private fun loadVideosFromStorage(): List<VideoFile> {
        val videoList = mutableListOf<VideoFile>()

        val collection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.MIME_TYPE
        )

        Log.d(TAG, "Querying MediaStore with URI: $collection")

        try {
            context.contentResolver.query(
                collection, projection, null, null, "${MediaStore.Video.Media.DATE_ADDED} DESC"
            )?.use { cursor ->
                Log.d(TAG, "Cursor count: ${cursor.count}")

                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val name = cursor.getString(nameCol) ?: continue
                    val path = cursor.getString(dataCol) ?: continue
                    val duration = cursor.getLong(durationCol)
                    val size = cursor.getLong(sizeCol)
                    val dateAdded = cursor.getLong(dateAddedCol) * 1000L

                    Log.d(TAG, "Found video: $name, path: $path, duration: $duration, size: $size")

                    if (duration <= 0 || size <= 0) {
                        Log.d(TAG, "Skipping $name - invalid duration or size")
                        continue
                    }

                    val file = File(path)
                    if (!file.exists()) {
                        Log.d(TAG, "Skipping $name - file doesn't exist")
                        continue
                    }

                    val folderPath = file.parent ?: ""
                    val folderName = File(folderPath).name

                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id
                    )

                    val thumbnailUri: Uri = Uri.parse(
                        "content://media/external/video/media/$id/thumbnail"
                    )

                    videoList.add(
                        VideoFile(
                            id = id,
                            name = name,
                            path = path,
                            size = size,
                            duration = duration,
                            dateAdded = dateAdded,
                            folderPath = folderPath,
                            folderName = folderName,
                            uri = contentUri,
                            thumbnailUri = thumbnailUri
                        )
                    )
                }
            } ?: run {
                Log.e(TAG, "Query returned null cursor")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading videos", e)
        }

        Log.d(TAG, "Total videos found: ${videoList.size}")
        return videoList
    }
}