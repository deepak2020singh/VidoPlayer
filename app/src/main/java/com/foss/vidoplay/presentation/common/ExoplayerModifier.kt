package com.foss.vidoplay.presentation.common


import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalView
import androidx.compose.runtime.DisposableEffect
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import java.util.Locale


fun Modifier.keepScreenOn(enabled: Boolean = true): Modifier = composed {
    val view = LocalView.current
    DisposableEffect(enabled) {
        val wasKeepScreenOn = view.keepScreenOn
        view.keepScreenOn = enabled
        onDispose { view.keepScreenOn = wasKeepScreenOn }
    }
    this
}


@UnstableApi
fun resolveResizeMode(isShortVideoMode: Boolean, state: ExoPlayerState): Int = when {
    isShortVideoMode && state.isPortraitVideo -> AspectRatioFrameLayout.RESIZE_MODE_FIT
    else -> when (state.aspectRatio) {
        AspectRatio.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
        AspectRatio.FILL -> AspectRatioFrameLayout.RESIZE_MODE_FILL
        AspectRatio.STRETCH -> AspectRatioFrameLayout.RESIZE_MODE_FILL
        AspectRatio.ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
    }
}


fun formatFileSize(size: Long): String = when {
    size >= 1_073_741_824 -> String.format(Locale.US, "%.2f GB", size / 1_073_741_824.0)
    size >= 1_048_576 -> String.format(Locale.US, "%.2f MB", size / 1_048_576.0)
    size >= 1_024 -> String.format(Locale.US, "%.2f KB", size / 1_024.0)
    else -> "$size B"
}