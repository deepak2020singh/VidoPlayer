package com.foss.vidoplay.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foss.vidoplay.data.repos.LastPlayedInfo
import com.foss.vidoplay.data.repos.LastPlayedRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

private const val TAG = "LastPlayedViewModel"

class LastPlayedViewModel(
    private val repo: LastPlayedRepository
) : ViewModel() {

    // Validated stream - checks file existence and valid position
    val resumableVideo: StateFlow<LastPlayedInfo?> =
        repo.lastPlayed
            .catch { exception ->
                Log.e(TAG, "Error in resumableVideo flow", exception)
                emit(null)
            }
            .map { info ->
                if (info == null) {
                    Log.d(TAG, "No last played info found")
                    return@map null
                }

                try {
                    // Validate file exists
                    val file = if (info.videoPath.isNotEmpty()) {
                        File(info.videoPath)
                    } else {
                        File(info.folderPath, info.videoName)
                    }

                    val fileExists = file.exists() && file.isFile && file.canRead()

                    // Validate position (must be between 1 second and 5 seconds before end)
                    val minValidPosition = 1000L
                    val maxValidPosition = (info.duration - 5000L).coerceAtLeast(0)
                    val hasValidPosition = info.position > minValidPosition &&
                            info.position < maxValidPosition &&
                            info.position < info.duration

                    Log.d(TAG, "Checking resumable video: ${info.videoName}")
                    Log.d(TAG, "  - Path: ${file.absolutePath}")
                    Log.d(TAG, "  - File exists: $fileExists")
                    Log.d(TAG, "  - Position: ${formatDuration(info.position)} / ${formatDuration(info.duration)}")
                    Log.d(TAG, "  - Valid position: $hasValidPosition")

                    if (fileExists && hasValidPosition) {
                        Log.d(TAG, "✓ Valid resumable video found: ${info.videoName}")
                        info
                    } else {
                        val reason = when {
                            !fileExists -> "File does not exist"
                            !hasValidPosition -> "Invalid position (${info.position}ms)"
                            else -> "Unknown reason"
                        }
                        Log.d(TAG, "✗ Invalid last played entry: $reason")
                        null
                    }
                } catch (e: SecurityException) {
                    Log.e(TAG, "Security error accessing file: ${info.videoPath}", e)
                    null
                } catch (e: Exception) {
                    Log.e(TAG, "Error validating last played info", e)
                    null
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    fun save(info: LastPlayedInfo) = viewModelScope.launch {
        try {
            withContext(Dispatchers.IO) {
                Log.d(TAG, "Saving last played info: ${info.videoName}")
                Log.d(TAG, "  - Video ID: ${info.videoId}")
                Log.d(TAG, "  - Folder Path: ${info.folderPath}")
                Log.d(TAG, "  - Video Path: ${info.videoPath}")
                Log.d(TAG, "  - Position: ${formatDuration(info.position)} / ${formatDuration(info.duration)}")
                repo.save(info)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving last played info", e)
        }
    }

    fun savePosition(position: Long) = viewModelScope.launch {
        try {
            withContext(Dispatchers.IO) {
                Log.d(TAG, "Saving position: ${formatDuration(position)}")
                repo.savePosition(position)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving position", e)
        }
    }

    // Immediate save without debouncing - for final position when app closes
    suspend fun savePositionImmediate(position: Long) {
        try {
            withContext(Dispatchers.IO) {
                Log.d(TAG, "Saving position immediate: ${formatDuration(position)}")
                repo.savePosition(position)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving position immediate", e)
        }
    }

    fun clear() = viewModelScope.launch {
        try {
            withContext(Dispatchers.IO) {
                Log.d(TAG, "Clearing last played data")
                repo.clear()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing last played data", e)
        }
    }

    fun hasResumableVideo(): Boolean = resumableVideo.value != null

    fun getResumableVideo(): LastPlayedInfo? = resumableVideo.value

    fun getFormattedSavedPosition(): String {
        val info = resumableVideo.value
        return if (info != null) {
            "${formatDuration(info.position)} / ${formatDuration(info.duration)}"
        } else {
            ""
        }
    }

    fun getResumeProgress(): Float {
        val info = resumableVideo.value
        return if (info != null && info.duration > 0) {
            (info.position.toFloat() / info.duration.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    fun getWatchedPercentage(): Int {
        val info = resumableVideo.value
        return if (info != null && info.duration > 0) {
            ((info.position.toFloat() / info.duration.toFloat()) * 100).toInt()
        } else {
            0
        }
    }

    fun isNearEnd(): Boolean {
        val info = resumableVideo.value
        return if (info != null && info.duration > 0) {
            info.duration - info.position < 10000L
        } else {
            false
        }
    }

    fun shouldResume(): Boolean {
        val info = resumableVideo.value
        return info != null && info.position > 5000L && !isNearEnd()
    }

    private fun formatDuration(ms: Long): String {
        if (ms <= 0) return "00:00"
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60)) % 60
        val hours = ms / (1000 * 60 * 60)
        return if (hours > 0) {
            String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "LastPlayedViewModel cleared")
    }
}