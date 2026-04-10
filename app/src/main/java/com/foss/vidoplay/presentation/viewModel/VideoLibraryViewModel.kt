package com.foss.vidoplay.presentation.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foss.vidoplay.domain.model.VideoFile
import com.foss.vidoplay.domain.model.VideoFolder
import com.foss.vidoplay.domain.repo.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale


class VideoViewModel(
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _videoFolders = MutableStateFlow<List<VideoFolder>>(emptyList())
    val videoFolders: StateFlow<List<VideoFolder>> = _videoFolders.asStateFlow()

    private val _allVideos = MutableStateFlow<List<VideoFile>>(emptyList())
    val allVideos: StateFlow<List<VideoFile>> = _allVideos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedFolder = MutableStateFlow<VideoFolder?>(null)
    val selectedFolder: StateFlow<VideoFolder?> = _selectedFolder.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var cachedFolders: List<VideoFolder>? = null
    private var cachedVideos: List<VideoFile>? = null
    private var lastLoadTime: Long = 0L
    private val CACHE_DURATION_MS = 5 * 60 * 1_000L

    fun loadVideos(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val cacheValid = cachedVideos != null && cachedFolders != null &&
                    (now - lastLoadTime) < CACHE_DURATION_MS

            if (!forceRefresh && cacheValid) {
                _allVideos.value = cachedVideos!!
                _videoFolders.value = cachedFolders!!
                return@launch
            }

            _isLoading.value = true
            _error.value = null

            videoRepository.getVideos()
                .flowOn(Dispatchers.IO) // collect on IO, emit on Main automatically
                .catch { e ->
                    _error.value = e.message ?: "Failed to load videos"
                    _videoFolders.value = emptyList()
                    _allVideos.value = emptyList()
                    _isLoading.value = false
                }
                .collect { videos ->
                    // File-system existence check — must run on IO
                    val existingVideos = withContext(Dispatchers.IO) {
                        videos.filter { File(it.path).exists() }
                    }
                    val folders = withContext(Dispatchers.Default) {
                        groupVideosIntoFolders(existingVideos)
                    }

                    cachedVideos = existingVideos
                    cachedFolders = folders
                    lastLoadTime = System.currentTimeMillis()

                    _allVideos.value = existingVideos
                    _videoFolders.value = folders
                    _isLoading.value = false
                }
        }
    }

    private fun groupVideosIntoFolders(videos: List<VideoFile>): List<VideoFolder> {
        return videos.groupBy { it.folderPath }.map { (folderPath, videoList) ->
            val first = videoList.firstOrNull()
            VideoFolder(
                name = if (folderPath.isEmpty()) "Root" else File(folderPath).name,
                path = folderPath,
                videos = videoList,
                videoCount = videoList.size,
                thumbnailPath = first?.path,
                thumbnailUri = first?.thumbnailUri,
                totalSize = videoList.sumOf { it.size },
                dateModified = videoList.maxOfOrNull { it.dateAdded } ?: 0L
            )
        }.sortedByDescending { it.videoCount }
    }

    fun selectFolder(folder: VideoFolder?) { _selectedFolder.value = folder }

    fun refreshVideos() {
        clearCache()
        loadVideos(forceRefresh = true)
    }

    fun clearError() { _error.value = null }

    fun clearCache() {
        cachedVideos = null
        cachedFolders = null
        lastLoadTime = 0L
    }

    // ── Search (pure in-memory — already fast) ────────────────────────────────
    fun searchVideos(query: String): List<VideoFile> =
        if (query.isBlank()) _allVideos.value
        else _allVideos.value.filter {
            it.name.contains(query, ignoreCase = true) || it.folderName.contains(query, ignoreCase = true)
        }

    fun searchFolders(query: String): List<VideoFolder> =
        if (query.isBlank()) _videoFolders.value
        else _videoFolders.value.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.videos.any { v -> v.name.contains(query, ignoreCase = true) }
        }

    fun getVideoById(id: Long): VideoFile? = _allVideos.value.find { it.id == id }
    fun getFolderByPath(path: String): VideoFolder? = _videoFolders.value.find { it.path == path }
    fun getVideosInFolder(folderPath: String): List<VideoFile> = _allVideos.value.filter { it.folderPath == folderPath }



    // ── Formatters ────────────────────────────────────────────────────────────

    fun formatSize(size: Long): String = when {
        size >= 1_073_741_824 -> String.format(Locale.US,"%.2f GB", size / 1_073_741_824.0)
        size >= 1_048_576 -> String.format(Locale.US,"%.2f MB", size / 1_048_576.0)
        size >= 1_024 -> String.format(Locale.US,"%.2f KB", size / 1_024.0)
        else -> "$size B"
    }


    fun formatDuration(duration: Long): String {
        if (duration <= 0) return "00:00"
        val s = (duration / 1000) % 60
        val m = (duration / 60_000) % 60
        val h = duration / 3_600_000
        return if (h > 0) String.format(Locale.US,"%d:%02d:%02d", h, m, s)
        else String.format(Locale.US,"%02d:%02d", m, s)
    }


}

data class FolderStats(
    val totalVideos: Int,
    val totalSize: Long,
    val totalDuration: Long,
    val folderCount: Int
)

