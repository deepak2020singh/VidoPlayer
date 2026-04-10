package com.foss.vidoplay.presentation.viewModel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.util.LruCache
import androidx.core.graphics.get
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foss.vidoplay.domain.model.VideoFile
import com.foss.vidoplay.domain.repo.VideoRepository
import com.foss.vidoplay.presentation.common.AspectRatio
import com.foss.vidoplay.presentation.common.Bookmark
import com.foss.vidoplay.presentation.common.ColorSpace
import com.foss.vidoplay.presentation.common.CropMode
import com.foss.vidoplay.presentation.common.EQ_PRESETS
import com.foss.vidoplay.presentation.common.EnhancementMode
import com.foss.vidoplay.presentation.common.EqualizerPreset
import com.foss.vidoplay.presentation.common.ExoPlayerManager
import com.foss.vidoplay.presentation.common.ExoPlayerState
import com.foss.vidoplay.presentation.common.SubtitleTrack
import com.foss.vidoplay.presentation.common.VideoAdjustment
import com.foss.vidoplay.presentation.common.VideoFilter
import com.foss.vidoplay.presentation.common.ZoomPreset
import com.foss.vidoplay.presentation.utils.ScreenshotCapture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.random.Random

private const val TAG = "ExoPlayerViewModel"
private const val THUMBNAIL_CACHE_SIZE = 30

class GetVideosUseCase(private val repository: VideoRepository) {
    operator fun invoke(): Flow<List<VideoFile>> = repository.getVideos()
}

sealed class ExoPlayerEvent {
    data class ShowToast(val message: String) : ExoPlayerEvent()
    data class ScreenshotResult(val success: Boolean, val message: String = "") : ExoPlayerEvent()
    data class ApplyWindowBrightness(val brightness: Float) : ExoPlayerEvent()
    data class SaveCurrentPosition(val position: Long, val video: VideoFile) : ExoPlayerEvent()
    data class VideoSwitchComplete(val success: Boolean, val message: String = "") :
        ExoPlayerEvent()
}

class ExoPlayerViewModel(
    private val getVideosUseCase: GetVideosUseCase
) : ViewModel()
{
        private val _state = MutableStateFlow(ExoPlayerState())
        val state: StateFlow<ExoPlayerState> = _state.asStateFlow()

        private val _events = MutableSharedFlow<ExoPlayerEvent>(extraBufferCapacity = 8)
        val events: SharedFlow<ExoPlayerEvent> = _events.asSharedFlow()

        private var sleepTimerJob: Job? = null
        private var volumeBeforeMute: Float = 1f
        private var lastPlayedViewModel: LastPlayedViewModel? = null

        private val thumbnailCache = object : LruCache<String, Bitmap>(THUMBNAIL_CACHE_SIZE) {
            override fun entryRemoved(evicted: Boolean, key: String, oldValue: Bitmap, newValue: Bitmap?) {
                if (evicted && !oldValue.isRecycled) oldValue.recycle()
            }
        }

        // Playlist management (called by parent screen)

        fun setPlaylist(videos: List<VideoFile>, startVideo: VideoFile, startIndex: Int) {
            val shuffled = if (_state.value.shuffleEnabled) shuffleList(videos) else emptyList()
            val shuffledIndex = if (_state.value.shuffleEnabled) {
                shuffled.indexOfFirst { it.id == startVideo.id }.takeIf { it >= 0 } ?: 0
            } else -1

            _state.update {
                it.copy(
                    playlist = videos,
                    originalPlaylist = videos,
                    shuffledPlaylist = shuffled,
                    currentVideo = startVideo,
                    currentIndex = startIndex,
                    currentShuffledIndex = shuffledIndex,
                    currentPosition = 0L,
                    isPlaying = true,
                    bookmarks = emptyList()
                )
            }
            ExoPlayerManager.prepareVideo(startVideo.uri.toString(), 0L, true)
        }

        private fun shuffleList(list: List<VideoFile>): List<VideoFile> =
            if (list.isNotEmpty()) list.shuffled(Random(System.currentTimeMillis())) else emptyList()

        // Navigation (next / previous) with repeat mode

        fun playNext() {
            val s = _state.value
            if (!hasNext()) {
                if (s.repeatMode == 2) {
                    // Repeat all: go to first video
                    val first = if (s.shuffleEnabled) s.shuffledPlaylist.firstOrNull() else s.playlist.firstOrNull()
                    first?.let { switchToVideo(it, 0, 0) }
                }
                return
            }
            val next = if (s.shuffleEnabled) {
                s.shuffledPlaylist.getOrNull(s.currentShuffledIndex + 1)
            } else {
                s.playlist.getOrNull(s.currentIndex + 1)
            } ?: return
            val newIndex = if (s.shuffleEnabled) s.currentShuffledIndex + 1 else s.currentIndex + 1
            val origIndex = if (s.shuffleEnabled) s.originalPlaylist.indexOfFirst { it.id == next.id } else newIndex
            switchToVideo(next, origIndex, newIndex)
        }

        fun playPrevious() {
            val s = _state.value
            if (!hasPrevious()) {
                if (s.repeatMode == 2) {
                    val last = if (s.shuffleEnabled) s.shuffledPlaylist.lastOrNull() else s.playlist.lastOrNull()
                    last?.let {
                        val lastIdx = if (s.shuffleEnabled) s.shuffledPlaylist.size - 1 else s.playlist.size - 1
                        switchToVideo(it, s.originalPlaylist.indexOfFirst { v -> v.id == it.id }, lastIdx)
                    }
                }
                return
            }
            val prev = if (s.shuffleEnabled) {
                s.shuffledPlaylist.getOrNull(s.currentShuffledIndex - 1)
            } else {
                s.playlist.getOrNull(s.currentIndex - 1)
            } ?: return
            val newIndex = if (s.shuffleEnabled) s.currentShuffledIndex - 1 else s.currentIndex - 1
            val origIndex = if (s.shuffleEnabled) s.originalPlaylist.indexOfFirst { it.id == prev.id } else newIndex
            switchToVideo(prev, origIndex, newIndex)
        }

        private fun switchToVideo(video: VideoFile, originalIndex: Int, shuffledIndex: Int) {
            _state.update {
                it.copy(
                    currentVideo = video,
                    currentIndex = originalIndex,
                    currentShuffledIndex = if (it.shuffleEnabled) shuffledIndex else -1,
                    currentPosition = 0L,
                    isPlaying = true,
                    bookmarks = emptyList()
                )
            }
            ExoPlayerManager.prepareVideo(video.uri.toString(), 0L, true)
        }

        fun hasNext(): Boolean {
            val s = _state.value
            return if (s.shuffleEnabled) {
                s.currentShuffledIndex < s.shuffledPlaylist.size - 1 || s.repeatMode == 2
            } else {
                s.currentIndex < s.playlist.size - 1 || s.repeatMode == 2
            }
        }

        fun hasPrevious(): Boolean {
            val s = _state.value
            return if (s.shuffleEnabled) {
                s.currentShuffledIndex > 0 || s.repeatMode == 2
            } else {
                s.currentIndex > 0 || s.repeatMode == 2
            }
        }


        // Shuffle & Repeat
        fun toggleShuffle() {
            _state.update { s ->
                if (!s.shuffleEnabled) {
                    if (s.playlist.isEmpty()) return@update s
                    val shuffled = shuffleList(s.playlist)
                    val idx = s.currentVideo?.let { v ->
                        shuffled.indexOfFirst { it.id == v.id }.takeIf { it >= 0 }
                    } ?: 0
                    s.copy(
                        shuffleEnabled = true,
                        shuffledPlaylist = shuffled,
                        originalPlaylist = s.playlist,
                        currentShuffledIndex = idx,
                        currentVideo = shuffled.getOrNull(idx) ?: s.currentVideo,
                        currentIndex = shuffled.getOrNull(idx)?.let { findOriginalIndex(it) } ?: s.currentIndex
                    )
                } else {
                    if (s.originalPlaylist.isEmpty()) return@update s
                    val idx = s.currentVideo?.let { v ->
                        s.originalPlaylist.indexOfFirst { it.id == v.id }.takeIf { it >= 0 }
                    } ?: 0
                    s.copy(
                        shuffleEnabled = false,
                        shuffledPlaylist = emptyList(),
                        currentShuffledIndex = -1,
                        currentVideo = s.originalPlaylist.getOrNull(idx) ?: s.currentVideo,
                        currentIndex = idx
                    )
                }
            }
        }

        fun cycleRepeatMode() {
            _state.update { s ->
                s.copy(repeatMode = when (s.repeatMode) {
                    0 -> 1      // repeat one
                    1 -> 2      // repeat all
                    else -> 0   // off
                })
            }
        }

        private fun findOriginalIndex(video: VideoFile): Int =
            _state.value.originalPlaylist.indexOfFirst { it.id == video.id }



    fun setCurrentVideo(video: VideoFile, index: Int) {
        _state.update {
            it.copy(
                currentVideo = video,
                currentIndex = index,
                currentPosition = 0L
            )
        }
    }

    // ── SUGGESTION SWITCHING LOGIC ───────────────────────────────────────────
    fun switchToSuggestedVideo(
        suggestionVideo: VideoFile,
        currentVideo: VideoFile?,
        currentIndex: Int,
        allVideos: List<VideoFile>,
        onNext: (() -> Unit)?,
        onPrevious: (() -> Unit)?,
        onClose: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val newIndex = allVideos.indexOfFirst { it.id == suggestionVideo.id }
                if (newIndex != -1 && newIndex != currentIndex) {
                    Log.d(TAG, "Switching to suggested video: ${suggestionVideo.name}")

                    // Save current video's position before switching
                    if (currentVideo != null) {
                        saveCurrentVideoPosition(currentVideo)
                    }

                    // Emit event to save current position
                    _events.emit(ExoPlayerEvent.VideoSwitchComplete(false, "Switching video..."))

                    // Close the current player
                    onClose()

                    // Navigate to new video after delay
                    delay(150)

                    // Navigate based on index difference
                    when {
                        newIndex > currentIndex && onNext != null -> {
                            onNext.invoke()
                            _events.emit(
                                ExoPlayerEvent.VideoSwitchComplete(
                                    true,
                                    "Playing next video"
                                )
                            )
                        }

                        newIndex < currentIndex && onPrevious != null -> {
                            onPrevious.invoke()
                            _events.emit(
                                ExoPlayerEvent.VideoSwitchComplete(
                                    true,
                                    "Playing previous video"
                                )
                            )
                        }

                        else -> {
                            _events.emit(ExoPlayerEvent.ShowToast("Cannot navigate to video"))
                            _events.emit(
                                ExoPlayerEvent.VideoSwitchComplete(
                                    false,
                                    "Navigation failed"
                                )
                            )
                        }
                    }
                } else {
                    _events.emit(ExoPlayerEvent.ShowToast("Video already playing"))
                    _events.emit(
                        ExoPlayerEvent.VideoSwitchComplete(
                            false,
                            "Already playing this video"
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error switching to suggested video", e)
                _events.emit(ExoPlayerEvent.ShowToast("Error switching video: ${e.message}"))
                _events.emit(
                    ExoPlayerEvent.VideoSwitchComplete(
                        false,
                        e.message ?: "Unknown error"
                    )
                )
            }
        }
    }

    fun saveCurrentVideoPosition(video: VideoFile) {
        viewModelScope.launch {
            try {
                val currentPosition = ExoPlayerManager.getCurrentPosition()
                if (currentPosition > 0) {
                    lastPlayedViewModel?.savePosition(currentPosition)
                    _events.emit(ExoPlayerEvent.SaveCurrentPosition(currentPosition, video))
                    Log.d(TAG, "Saved current position: $currentPosition for video: ${video.name}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving current position", e)
            }
        }
    }

    fun validateAndPlaySuggestion(
        suggestionVideo: VideoFile,
        currentVideo: VideoFile?,
        currentIndex: Int,
        allVideos: List<VideoFile>,
        onNext: (() -> Unit)?,
        onPrevious: (() -> Unit)?,
        onClose: () -> Unit
    ): Boolean {
        // Validate the suggestion video
        if (suggestionVideo.id == currentVideo?.id) {
            viewModelScope.launch {
                _events.emit(ExoPlayerEvent.ShowToast("This video is already playing"))
            }
            return false
        }

        val newIndex = allVideos.indexOfFirst { it.id == suggestionVideo.id }
        if (newIndex == -1) {
            viewModelScope.launch {
                _events.emit(ExoPlayerEvent.ShowToast("Video not found in playlist"))
            }
            return false
        }

        // Check if navigation is possible
        if (newIndex > currentIndex && onNext == null) {
            viewModelScope.launch {
                _events.emit(ExoPlayerEvent.ShowToast("No next video available"))
            }
            return false
        }

        if (newIndex < currentIndex && onPrevious == null) {
            viewModelScope.launch {
                _events.emit(ExoPlayerEvent.ShowToast("No previous video available"))
            }
            return false
        }

        // All validations passed, proceed with switch
        switchToSuggestedVideo(
            suggestionVideo = suggestionVideo,
            currentVideo = currentVideo,
            currentIndex = currentIndex,
            allVideos = allVideos,
            onNext = onNext,
            onPrevious = onPrevious,
            onClose = onClose
        )
        return true
    }

    // ── PLAYBACK STATE ────────────────────────────────────────────────────────
    fun updatePlaybackState(
        isPlaying: Boolean = _state.value.isPlaying,
        currentPosition: Long = _state.value.currentPosition,
        duration: Long = _state.value.duration,
        playbackSpeed: Float = _state.value.playbackSpeed,
        isMuted: Boolean = _state.value.isMuted,
        volume: Float = _state.value.volume
    ) {
        _state.update {
            it.copy(
                isPlaying = isPlaying,
                currentPosition = currentPosition,
                duration = duration,
                playbackSpeed = playbackSpeed,
                isMuted = isMuted,
                volume = volume
            )
        }
    }

    fun toggleControls(visible: Boolean) {
        _state.update { it.copy(isControlsVisible = visible) }
    }

    // ── FULLSCREEN ────────────────────────────────────────────────────────────
    fun toggleFullScreen() {
        _state.update { it.copy(isFullScreen = !it.isFullScreen) }
    }

    // ── SCREEN LOCK ───────────────────────────────────────────────────────────
    fun toggleScreenLock() {
        _state.update { it.copy(isScreenLocked = !it.isScreenLocked) }
    }

    fun toggleSubtitles() {
        _state.update { it.copy(subtitleEnabled = !it.subtitleEnabled) }
    }

    fun setAudioTrack(t: String) {
        _state.update { it.copy(selectedAudioTrack = t) }
    }

    fun setZoomPreset(p: ZoomPreset) {
        _state.update { it.copy(scale = p.scale, zoomPreset = p) }
    }

    // ── MUTE / VOLUME ─────────────────────────────────────────────────────────
    fun toggleMute() {
        _state.update { s ->
            if (s.isMuted) {
                s.copy(
                    isMuted = false,
                    volume = if (volumeBeforeMute > 0f) volumeBeforeMute else 1f
                )
            } else {
                volumeBeforeMute = s.volume
                s.copy(isMuted = true, volume = 0f)
            }
        }
    }

    // ── SCREENSHOT ────────────────────────────────────────────────────────────
    fun captureScreenshot(
        context: Context,
        playerView: androidx.media3.ui.PlayerView,
        videoName: String
    ) {
        viewModelScope.launch {
            val bitmap: Bitmap? = ScreenshotCapture.capture(playerView)
            if (bitmap == null) {
                _events.emit(ExoPlayerEvent.ShowToast("Screenshot failed"))
                _events.emit(ExoPlayerEvent.ScreenshotResult(false))
                return@launch
            }
            val success = withContext(Dispatchers.IO) { saveBitmap(context, bitmap, videoName) }
            _events.emit(ExoPlayerEvent.ShowToast(if (success) "Saved to gallery" else "Save failed"))
            _events.emit(ExoPlayerEvent.ScreenshotResult(success))
            bitmap.recycle()
        }
    }

    private fun saveBitmap(context: Context, bitmap: Bitmap, name: String): Boolean {
        return try {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_$name.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/VidoPlay")
            }
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)
                    ?.use { stream -> bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream) }
                true
            } ?: false
        } catch (e: Exception) {
            Log.e(TAG, "saveBitmap failed", e)
            false
        }
    }

    suspend fun getVideoFrameAtPosition(
        context: Context,
        uri: Uri,
        positionMs: Long,
        useCache: Boolean = true
    ): Bitmap? = withContext(Dispatchers.IO) {
        val cacheKey = "${uri}_${positionMs / 800}"

        if (useCache) {
            thumbnailCache.get(cacheKey)?.takeIf { !it.isRecycled }?.let { return@withContext it }
        }

        var frame: Bitmap? = null
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            val timeUs = positionMs * 1000L

            frame = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)

            if (frame == null || isBitmapMostlyBlack(frame)) {
                frame?.recycle()
                frame = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST)
            }

            if (frame == null || isBitmapMostlyBlack(frame)) {
                frame?.recycle()
                for (offset in listOf(300L, -200L, 600L, -400L)) {
                    val t = (timeUs + offset * 1000L).coerceAtLeast(0L)
                    val candidate =
                        retriever.getFrameAtTime(t, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    if (candidate != null && !isBitmapMostlyBlack(candidate)) {
                        frame = candidate
                        break
                    }
                    candidate?.recycle()
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Frame extraction failed at ${positionMs}ms", e)
            frame?.recycle()
            frame = null
        } finally {
            try {
                retriever.release()
            } catch (_: Exception) {
            }
        }

        if (frame != null && frame.width > 80 && frame.height > 60 && !isBitmapMostlyBlack(frame)) {
            if (useCache) thumbnailCache.put(cacheKey, frame)
            frame
        } else {
            frame?.recycle()
            null
        }
    }

    private fun isBitmapMostlyBlack(
        bitmap: Bitmap,
        darkThreshold: Int = 35,
        darkPercentage: Float = 0.82f
    ): Boolean {
        if (bitmap.width < 20 || bitmap.height < 20) return true
        val sampleN = 12
        val stepX = max(1, bitmap.width / sampleN)
        val stepY = max(1, bitmap.height / sampleN)
        var dark = 0
        val total = sampleN * sampleN
        for (y in 0 until sampleN) {
            for (x in 0 until sampleN) {
                val px = bitmap[(x * stepX).coerceAtMost(bitmap.width - 1),
                    (y * stepY).coerceAtMost(bitmap.height - 1)]
                val r = (px shr 16) and 0xFF
                val g = (px shr 8) and 0xFF
                val b = px and 0xFF
                if (r < darkThreshold && g < darkThreshold && b < darkThreshold) dark++
            }
        }
        return dark.toFloat() / total > darkPercentage
    }

    fun clearThumbnailCache(uri: Uri? = null) {
        if (uri != null) {
            val prefix = uri.toString()
            val keys = thumbnailCache.snapshot().keys.filter { it.startsWith(prefix) }
            keys.forEach { thumbnailCache.remove(it) }
        } else {
            thumbnailCache.evictAll()
        }
    }


    // ── BRIGHTNESS ────────────────────────────────────────────────────────────
    fun setBrightness(v: Float) {
        val clamped = v.coerceIn(0f, 1f)
        _state.update { it.copy(brightness = clamped) }
        viewModelScope.launch { _events.emit(ExoPlayerEvent.ApplyWindowBrightness(clamped)) }
    }

    fun cycleAspectRatio() {
        _state.update { s ->
            s.copy(
                aspectRatio = when (s.aspectRatio) {
                    AspectRatio.FIT -> AspectRatio.FILL
                    AspectRatio.FILL -> AspectRatio.STRETCH
                    AspectRatio.STRETCH -> AspectRatio.ZOOM
                    AspectRatio.ZOOM -> AspectRatio.CUSTOM_16_9
                    AspectRatio.CUSTOM_16_9 -> AspectRatio.CUSTOM_4_3
                    AspectRatio.CUSTOM_4_3 -> AspectRatio.CUSTOM_21_9
                    AspectRatio.CUSTOM_21_9 -> AspectRatio.ORIGINAL
                    AspectRatio.ORIGINAL -> AspectRatio.FIT
                }
            )
        }
    }


    fun resetVideoAdjustments() {
        _state.update { it.copy(videoAdjustment = VideoAdjustment()) }
    }

    fun setVideoBrightness(v: Float) {
        _state.update {
            it.copy(
                videoAdjustment = it.videoAdjustment.copy(
                    brightness = v.coerceIn(
                        0f,
                        2f
                    )
                )
            )
        }
    }

    fun setContrast(v: Float) {
        _state.update {
            it.copy(
                videoAdjustment = it.videoAdjustment.copy(
                    contrast = v.coerceIn(
                        0f,
                        2f
                    )
                )
            )
        }
    }

    fun setSaturation(v: Float) {
        _state.update {
            it.copy(
                videoAdjustment = it.videoAdjustment.copy(
                    saturation = v.coerceIn(
                        0f,
                        2f
                    )
                )
            )
        }
    }

    fun setSharpness(v: Float) {
        _state.update {
            it.copy(
                videoAdjustment = it.videoAdjustment.copy(
                    sharpness = v.coerceIn(
                        0f,
                        2f
                    )
                )
            )
        }
    }

    fun setHue(v: Float) {
        _state.update {
            it.copy(
                videoAdjustment = it.videoAdjustment.copy(
                    hue = v.coerceIn(
                        -180f,
                        180f
                    )
                )
            )
        }
    }


    fun cycleFilter() {
        _state.update { s ->
            val filters = VideoFilter.entries.toTypedArray()
            val next = (filters.indexOf(s.activeFilter ?: VideoFilter.NONE) + 1) % filters.size
            s.copy(activeFilter = filters[next])
        }
    }

    fun getCurrentFilterLabel(): String = when (_state.value.activeFilter ?: VideoFilter.NONE) {
        VideoFilter.NONE -> "None"
        VideoFilter.GRAYSCALE -> "Grayscale"
        VideoFilter.SEPIA -> "Sepia"
        VideoFilter.NEGATIVE -> "Negative"
        VideoFilter.BLUR -> "Blur"
        VideoFilter.SHARPEN -> "Sharpen"
        VideoFilter.EDGE_DETECT -> "Edge Detect"
        VideoFilter.EMBOSS -> "Emboss"
    }



    fun cycleCropMode() {
        _state.update { s ->
            s.copy(
                cropMode = when (s.cropMode) {
                    CropMode.NONE -> CropMode.SQUARE
                    CropMode.SQUARE -> CropMode.CIRCLE
                    CropMode.CIRCLE -> CropMode.CUSTOM
                    CropMode.CUSTOM -> CropMode.NONE
                }
            )
        }
    }



    // ── ROTATION & FLIP ───────────────────────────────────────────────────────
    fun rotateClockwise() {
        _state.update { it.copy(rotation = (it.rotation + 90) % 360) }
    }

    fun rotateCounterClockwise() {
        _state.update { it.copy(rotation = (it.rotation - 90 + 360) % 360) }
    }

    fun setRotation(deg: Int) {
        _state.update { it.copy(rotation = deg % 360) }
    }

    fun flipHorizontal() {
        _state.update { it.copy(flipHorizontal = !it.flipHorizontal) }
    }

    fun flipVertical() {
        _state.update { it.copy(flipVertical = !it.flipVertical) }
    }


    // ── VIDEO ENHANCEMENT ─────────────────────────────────────────────────────
    fun toggleDeinterlace() {
        _state.update { it.copy(deinterlaceEnabled = !it.deinterlaceEnabled) }
    }


    fun cycleEnhancementMode() {
        _state.update { s ->
            s.copy(
                enhancementMode = when (s.enhancementMode) {
                    EnhancementMode.NONE -> EnhancementMode.AUTO
                    EnhancementMode.AUTO -> EnhancementMode.DYNAMIC_CONTRAST
                    EnhancementMode.DYNAMIC_CONTRAST -> EnhancementMode.DETAIL_ENHANCE
                    EnhancementMode.DETAIL_ENHANCE -> EnhancementMode.NONE
                }
            )
        }
    }

    fun toggleHdr() {
        _state.update { it.copy(hdrEnabled = !it.hdrEnabled) }
    }


    fun cycleColorSpace() {
        _state.update { s ->
            s.copy(
                colorSpace = when (s.colorSpace) {
                    ColorSpace.AUTO -> ColorSpace.REC_709
                    ColorSpace.REC_709 -> ColorSpace.REC_2020
                    ColorSpace.REC_2020 -> ColorSpace.DCI_P3
                    ColorSpace.DCI_P3 -> ColorSpace.AUTO
                }
            )
        }
    }

    // ── FRAME RATE ────────────────────────────────────────────────────────────

    fun toggleFrameRateConversion() {
        _state.update { it.copy(frameRateConversionEnabled = !it.frameRateConversionEnabled) }
    }

    // ── SLEEP TIMER ───────────────────────────────────────────────────────────
    fun setSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        val end = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(minutes.toLong())
        _state.update {
            it.copy(
                sleepTimerActive = true,
                sleepTimerEndTime = end,
                sleepTimerDuration = minutes
            )
        }
        sleepTimerJob = viewModelScope.launch {
            delay(TimeUnit.MINUTES.toMillis(minutes.toLong()))
            if (_state.value.sleepTimerActive) {
                _state.update {
                    it.copy(
                        isPlaying = false,
                        sleepTimerActive = false,
                        sleepTimerEndTime = null
                    )
                }
            }
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        _state.update {
            it.copy(
                sleepTimerActive = false,
                sleepTimerEndTime = null,
                sleepTimerDuration = 0
            )
        }
    }


    fun getSleepTimerRemainingTime(): String {
        val end = _state.value.sleepTimerEndTime ?: return "0:00"
        val rem = (end - System.currentTimeMillis()).coerceAtLeast(0)
        return String.format(
            Locale.US,
            "%d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(rem),
            TimeUnit.MILLISECONDS.toSeconds(rem) % 60
        )

    }


    // ── PLAYBACK SPEED ────────────────────────────────────────────────────────
    fun setPlaybackSpeed(speed: Float) {
        val v = speed.coerceIn(0.25f, 3f)
        _state.update {
            it.copy(
                playbackSpeed = v,
                customSpeedEnabled = !it.availableSpeeds.contains(v)
            )
        }
    }



    // ── RESET ALL ─────────────────────────────────────────────────────────────
    fun resetAllSettings() {
        sleepTimerJob?.cancel()
        _state.update {
            it.copy(
                videoAdjustment = VideoAdjustment(), activeFilter = null,
                cropMode = CropMode.NONE, customCropBounds = null,
                rotation = 0, flipHorizontal = false, flipVertical = false,
                scale = 1f, zoomPreset = ZoomPreset.NORMAL, isZoomEnabled = true,
                deinterlaceEnabled = false, enhancementMode = EnhancementMode.NONE,
                hdrEnabled = false, colorSpace = ColorSpace.AUTO,
                targetFrameRate = 60, frameRateConversionEnabled = false,
                isMuted = false, volume = 1f, brightness = 0.5f, playbackSpeed = 1f,
                aspectRatio = AspectRatio.FIT, isFullScreen = false, subtitleEnabled = false,
                repeatMode = 0, shuffleEnabled = false,
                sleepTimerActive = false, sleepTimerEndTime = null, sleepTimerDuration = 0
            )
        }
        volumeBeforeMute = 1f
        viewModelScope.launch { _events.emit(ExoPlayerEvent.ApplyWindowBrightness(-1f)) }
    }

    // ── EQUALIZER ─────────────────────────────────────────────────────────────
    fun toggleEqualizer() {
        _state.update { it.copy(equalizerEnabled = !it.equalizerEnabled) }
    }



    fun setEqualizerPreset(preset: EqualizerPreset) {
        _state.update {
            it.copy(
                equalizerPreset = preset,
                customEqBands = if (preset == EqualizerPreset.CUSTOM) it.customEqBands
                else EQ_PRESETS[preset] ?: List(10) { 0f }
            )
        }
        applyEqualizer()
    }

    fun setCustomEqBand(band: Int, value: Float) {
        _state.update { s ->
            val newBands = s.customEqBands.toMutableList()
            if (band in newBands.indices) newBands[band] = value.coerceIn(-1f, 1f)
            s.copy(customEqBands = newBands, equalizerPreset = EqualizerPreset.CUSTOM)
        }
        applyEqualizer()
    }

    fun resetEqualizer() {
        _state.update {
            it.copy(
                equalizerEnabled = false,
                equalizerPreset = EqualizerPreset.NORMAL,
                customEqBands = List(10) { 0f })
        }
        applyEqualizer()
    }

    private fun applyEqualizer() {
        val s = _state.value
        val bands = if (s.equalizerEnabled) {
            if (s.equalizerPreset == EqualizerPreset.CUSTOM) s.customEqBands
            else EQ_PRESETS[s.equalizerPreset] ?: List(10) { 0f }
        } else List(10) { 0f }
        ExoPlayerManager.setEqualizer(bands)
    }


    override fun onCleared() {
        super.onCleared()
        thumbnailCache.evictAll()
    }

    // ── BOOKMARKS ─────────────────────────────────────────────────────────────
    fun addBookmark(positionMs: Long) {
        _state.update { s ->
            val label = formatBookmarkTime(positionMs)
            val bookmark = Bookmark(
                id = System.currentTimeMillis(),
                positionMs = positionMs,
                label = label,
                videoId = s.currentVideo?.id ?: return@update s
            )
            val tooClose = s.bookmarks.any { kotlin.math.abs(it.positionMs - positionMs) < 2000L }
            if (tooClose) return@update s
            s.copy(bookmarks = (s.bookmarks + bookmark).sortedBy { it.positionMs })
        }
    }

    fun removeBookmark(id: Long) {
        _state.update { it.copy(bookmarks = it.bookmarks.filter { b -> b.id != id }) }
    }

    fun clearBookmarks() {
        _state.update { it.copy(bookmarks = emptyList()) }
    }

    fun getBookmarksForCurrentVideo(): List<Bookmark> {
        val s = _state.value
        return s.bookmarks.filter { it.videoId == s.currentVideo?.id }
    }

    private fun formatBookmarkTime(ms: Long): String {
        val totalSec = ms / 1000
        val h = totalSec / 3600
        val m = (totalSec % 3600) / 60
        val s = totalSec % 60
        return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
    }

    // ── SPEED GESTURE ─────────────────────────────────────────────────────────
    fun enterSpeedGesture(boostSpeed: Float = 2f) {
        val current = _state.value.playbackSpeed
        if (_state.value.isSpeedGestureActive) return
        _state.update { it.copy(isSpeedGestureActive = true, speedBeforeGesture = current) }
        ExoPlayerManager.setPlaybackSpeed(boostSpeed)
    }

    fun exitSpeedGesture() {
        if (!_state.value.isSpeedGestureActive) return
        val restore = _state.value.speedBeforeGesture
        _state.update { it.copy(isSpeedGestureActive = false) }
        ExoPlayerManager.setPlaybackSpeed(restore)
    }

    fun loadSubtitleTracks() {
        viewModelScope.launch {
            val tracks = ExoPlayerManager.getSubtitleTracks()
            val active = ExoPlayerManager.isSubtitleActive()
            _state.update {
                it.copy(
                    availableSubtitleTracks = tracks,
                    subtitleEnabled = active
                )
            }
        }
    }

    fun selectSubtitleTrack(track: SubtitleTrack?) {
        if (track != null) {
            ExoPlayerManager.selectSubtitleTrack(track.groupIndex, track.trackIndex)
            _state.update { it.copy(subtitleEnabled = true, selectedSubtitleTrack = track) }
        } else {
            ExoPlayerManager.disableSubtitles()
            _state.update { it.copy(subtitleEnabled = false, selectedSubtitleTrack = null) }
        }
    }


}