package com.foss.vidoplay.presentation.common

import com.foss.vidoplay.domain.model.VideoFile
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Storage
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.Locale


enum class ViewMode { GRID, LIST }

enum class AspectRatio {
    FIT, FILL, STRETCH, ZOOM, CUSTOM_16_9, CUSTOM_4_3, CUSTOM_21_9, ORIGINAL
}

enum class VideoFilter {
    NONE, GRAYSCALE, SEPIA, NEGATIVE, BLUR, SHARPEN, EDGE_DETECT, EMBOSS
}

enum class CropMode {
    NONE, SQUARE, CIRCLE, CUSTOM
}

enum class ZoomPreset(val scale: Float) {
    NORMAL(1f), ZOOM_2X(2f), ZOOM_3X(3f), ZOOM_4X(4f), WIDE(0.75f)
}

enum class EnhancementMode {
    NONE, AUTO, DYNAMIC_CONTRAST, DETAIL_ENHANCE
}

enum class ColorSpace {
    AUTO, REC_709, REC_2020, DCI_P3
}

data class CropBounds(
    val left: Float, val top: Float, val right: Float, val bottom: Float
)

data class VideoAdjustment(
    val saturation: Float = 1f,
    val contrast: Float = 1f,
    val brightness: Float = 1f,
    val sharpness: Float = 1f,
    val hue: Float = 0f
)




data class Bookmark(
    val id: Long,
    val positionMs: Long,
    val label: String,
    val videoId: Long
)


data class ExoPlayerState(
    val currentVideo: VideoFile? = null,
    val playlist: List<VideoFile> = emptyList(),
    val currentIndex: Int = -1,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val playbackSpeed: Float = 1f,

    val isMuted: Boolean = false,
    val volume: Float = 1f,
    val selectedAudioTrack: String = "Original",

    val isFullScreen: Boolean = false,
    val selectedQuality: String = "Auto",
    val isZoomEnabled: Boolean = true,
    val brightness: Float = 0.5f,
    val aspectRatio: AspectRatio = AspectRatio.FIT,

    val videoAdjustment: VideoAdjustment = VideoAdjustment(),
    val activeFilter: VideoFilter? = null,

    val cropMode: CropMode = CropMode.NONE,
    val customCropBounds: CropBounds? = null,

    val rotation: Int = 0,
    val flipHorizontal: Boolean = false,
    val flipVertical: Boolean = false,

    val scale: Float = 1f,
    val zoomPreset: ZoomPreset = ZoomPreset.NORMAL,

    val deinterlaceEnabled: Boolean = false,
    val enhancementMode: EnhancementMode = EnhancementMode.NONE,
    val hdrEnabled: Boolean = false,
    val colorSpace: ColorSpace = ColorSpace.AUTO,

    val targetFrameRate: Int = 60,
    val frameRateConversionEnabled: Boolean = false,

    val subtitleEnabled: Boolean = false,
    val availableSubtitleTracks: List<SubtitleTrack> = emptyList(),
    val selectedSubtitleTrack: SubtitleTrack? = null,

    val isControlsVisible: Boolean = true,
    val isScreenLocked: Boolean = false,

    val sleepTimerActive: Boolean = false,
    val sleepTimerEndTime: Long? = null,
    val sleepTimerDuration: Int = 0,

    val availableSpeeds: List<Float> = listOf(0.25f, 0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f),
    val customSpeedEnabled: Boolean = false,
    val pitchShiftEnabled: Boolean = false,

    val repeatMode: Int = 0,

    val shuffleEnabled: Boolean = false,
    val shuffledPlaylist: List<VideoFile> = emptyList(),
    val originalPlaylist: List<VideoFile> = emptyList(),
    val currentShuffledIndex: Int = -1,

    val isShortVideoMode: Boolean = false,
    val isPortraitVideo: Boolean = false,
    val videoWidth: Int = 0,
    val videoHeight: Int = 0,
    val autoPlayNextShort: Boolean = true,

    val equalizerEnabled: Boolean = false,
    val equalizerPreset: EqualizerPreset = EqualizerPreset.NORMAL,
    val customEqBands: List<Float> = List(10) { 0f }, // 10 bands from 0 to 1

    val bookmarks: List<Bookmark> = emptyList(),
    val isSpeedGestureActive: Boolean = false,
    val speedBeforeGesture: Float = 1f,

    val audioOnlyMode: Boolean = false,  // Add this line

)



enum class EqualizerPreset(val displayName: String) {
    NORMAL("Normal"),
    CLASSICAL("Classical"),
    DANCE("Dance"),
    FLAT("Flat"),
    FOLK("Folk"),
    HEAVY_METAL("Heavy Metal"),
    HIP_HOP("Hip Hop"),
    JAZZ("Jazz"),
    POP("Pop"),
    ROCK("Rock"),
    CUSTOM("Custom")
}

// Preset EQ values (10 bands: 31, 62, 125, 250, 500, 1k, 2k, 4k, 8k, 16k Hz)
val EQ_PRESETS = mapOf(
    EqualizerPreset.NORMAL to listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
    EqualizerPreset.CLASSICAL to listOf(0.3f, 0.2f, 0.1f, 0f, 0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f),
    EqualizerPreset.DANCE to listOf(0.5f, 0.4f, 0.3f, 0.2f, 0.1f, 0f, 0f, 0.2f, 0.3f, 0.4f),
    EqualizerPreset.FLAT to listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
    EqualizerPreset.FOLK to listOf(0.2f, 0.2f, 0.2f, 0.1f, 0f, 0.1f, 0.2f, 0.3f, 0.3f, 0.2f),
    EqualizerPreset.HEAVY_METAL to listOf(0.6f, 0.5f, 0.4f, 0.3f, 0.2f, 0f, -0.2f, -0.3f, -0.2f, 0f),
    EqualizerPreset.HIP_HOP to listOf(0.4f, 0.4f, 0.3f, 0.2f, 0.1f, 0f, -0.1f, -0.2f, -0.1f, 0f),
    EqualizerPreset.JAZZ to listOf(0.3f, 0.3f, 0.2f, 0.1f, 0f, 0.1f, 0.2f, 0.3f, 0.3f, 0.2f),
    EqualizerPreset.POP to listOf(0.2f, 0.2f, 0.2f, 0.1f, 0f, 0.1f, 0.2f, 0.3f, 0.3f, 0.2f),
    EqualizerPreset.ROCK to listOf(0.5f, 0.4f, 0.3f, 0.2f, 0f, -0.1f, -0.2f, -0.1f, 0f, 0.1f)
)







enum class SortOption(
    val displayName: String,
    val icon: ImageVector
) {
    NAME_ASC("Name (A-Z)", Icons.Default.SortByAlpha),
    NAME_DESC("Name (Z-A)", Icons.Default.SortByAlpha),
    DATE_ADDED_ASC("Date (Oldest first)", Icons.Default.DateRange),
    DATE_ADDED_DESC("Date (Newest first)", Icons.Default.DateRange),
    DURATION_ASC("Duration (Shortest)", Icons.Default.Timer),
    DURATION_DESC("Duration (Longest)", Icons.Default.Timer),
    SIZE_ASC("Size (Smallest)", Icons.Default.Storage),
    SIZE_DESC("Size (Largest)", Icons.Default.Storage)
}



enum class SearchFilter {
    ALL, VIDEOS, FOLDERS
}

