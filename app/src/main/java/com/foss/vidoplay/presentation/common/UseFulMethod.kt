package com.foss.vidoplay.presentation.common


import java.util.Locale


fun formatDuration(durationMs: Long): String {
    if (durationMs <= 0) return "00:00"
    val totalSeconds = durationMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format(Locale.US,"%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US,"%02d:%02d", minutes, seconds)
    }
}

