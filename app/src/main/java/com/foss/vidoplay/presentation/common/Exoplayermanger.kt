package com.foss.vidoplay.presentation.common

import android.content.Context
import android.media.audiofx.Equalizer
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Data class for subtitle track information
data class SubtitleTrack(
    val id: String,
    val label: String,
    val language: String,
    val groupIndex: Int,
    val trackIndex: Int
)

object ExoPlayerManager {

    private var exoPlayer: ExoPlayer? = null
    private var mediaSession: MediaSession? = null
    private var serviceScope: CoroutineScope? = null
    private var isInitialized = false

    private var currentVideoUri: String? = null
    private var pendingStartPosition: Long = 0L
    private var pendingPlay: Boolean = false

    private val _isPlayerReady = MutableStateFlow(false)
    val isPlayerReady: StateFlow<Boolean> = _isPlayerReady.asStateFlow()

    // Equalizer variables
    private var equalizer: Equalizer? = null
    private var audioSessionId: Int = 0
    private var _equalizerEnabled = false
    private var _equalizerBands: List<Float> = List(10) { 0f }

    // Audio-Only Mode variables
    private var audioOnlyMode = false

    @OptIn(UnstableApi::class)
    fun initialize(context: Context): ExoPlayer {
        if (!isInitialized) {
            val trackSelector = DefaultTrackSelector(context).apply {
                setParameters(buildUponParameters().setMaxVideoSize(Int.MAX_VALUE, Int.MAX_VALUE))
            }

            exoPlayer = ExoPlayer.Builder(context)
                .setTrackSelector(trackSelector)
                .setAudioAttributes(
                    androidx.media3.common.AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .build(),
                    true
                )
                .build()
                .also { player ->
                    audioSessionId = player.audioSessionId
                    Log.d("ExoPlayerManager", "Audio session ID: $audioSessionId")

                    player.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            when (playbackState) {
                                Player.STATE_READY -> {
                                    Log.d("ExoPlayerManager", "STATE_READY, uri=$currentVideoUri")
                                    _isPlayerReady.value = true
                                    if (pendingStartPosition > 0L) {
                                        Log.d("ExoPlayerManager", "Applying pending seek: $pendingStartPosition")
                                        player.seekTo(pendingStartPosition)
                                        pendingStartPosition = 0L
                                    }
                                    if (pendingPlay) {
                                        player.play()
                                        pendingPlay = false
                                    }
                                    if (_equalizerEnabled) {
                                        applyEqualizerSettings()
                                    }
                                    if (audioOnlyMode) {
                                        applyAudioOnlyMode()
                                    }
                                }
                                Player.STATE_BUFFERING -> { }
                                Player.STATE_ENDED -> {
                                    Log.d("ExoPlayerManager", "STATE_ENDED")
                                }
                                Player.STATE_IDLE -> {
                                    Log.d("ExoPlayerManager", "STATE_IDLE")
                                    _isPlayerReady.value = false
                                }
                            }
                        }

                        override fun onAudioSessionIdChanged(audioSessionId: Int) {
                            Log.d("ExoPlayerManager", "Audio session ID changed to: $audioSessionId")
                            this@ExoPlayerManager.audioSessionId = audioSessionId
                            if (_equalizerEnabled) {
                                initializeEqualizer()
                                applyEqualizerSettings()
                            }
                        }
                    })
                }

            mediaSession = MediaSession.Builder(context, exoPlayer!!).build()
            serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
            isInitialized = true
        }
        return exoPlayer!!
    }

    fun getExoPlayer(): ExoPlayer? = exoPlayer
    fun getMediaSession(): MediaSession? = mediaSession

    fun prepareVideo(uri: String, startPosition: Long = 0L, shouldPlay: Boolean = false, audioOnly: Boolean = false) {
        val player = exoPlayer ?: return

        val sameUri = currentVideoUri == uri
        val alreadyReady = _isPlayerReady.value

        if (sameUri && alreadyReady) {
            if (startPosition > 0L && kotlin.math.abs(player.currentPosition - startPosition) > 500) {
                Log.d("ExoPlayerManager", "Same video, seeking to: $startPosition")
                player.seekTo(startPosition)
            }
            if (shouldPlay) player.play()
            return
        }

        Log.d("ExoPlayerManager", "Preparing: $uri  startPos=$startPosition, audioOnly=$audioOnly")

        currentVideoUri = uri
        _isPlayerReady.value = false
        pendingStartPosition = startPosition
        pendingPlay = shouldPlay
        audioOnlyMode = audioOnly

        player.stop()
        player.clearMediaItems()
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
    }

    fun setAudioOnlyMode(enabled: Boolean) {
        audioOnlyMode = enabled
        val player = exoPlayer ?: return
        if (enabled) {
            applyAudioOnlyMode()
        } else {
            restoreVideoMode()
        }
        Log.d("ExoPlayerManager", "Audio-Only Mode: ${if (enabled) "Enabled" else "Disabled"}")
    }

    @OptIn(UnstableApi::class)
    private fun applyAudioOnlyMode() {
        val player = exoPlayer ?: return
        player.clearVideoSurface()
        player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
    }

    private fun restoreVideoMode() {
        val player = exoPlayer ?: return
        Log.d("ExoPlayerManager", "Video mode restored")
    }

    fun isAudioOnlyMode(): Boolean = audioOnlyMode
    fun toggleAudioOnlyMode() { setAudioOnlyMode(!audioOnlyMode) }

    fun seekTo(position: Long) {
        val player = exoPlayer ?: return
        if (_isPlayerReady.value) {
            player.seekTo(position)
        } else {
            pendingStartPosition = position
        }
    }

    fun play() {
        val player = exoPlayer ?: return
        if (_isPlayerReady.value) player.play()
        else pendingPlay = true
    }

    fun pause() {
        pendingPlay = false
        exoPlayer?.pause()
    }

    fun isPlaying(): Boolean = exoPlayer?.isPlaying == true
    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0L
    fun getDuration(): Long = exoPlayer?.duration?.coerceAtLeast(0) ?: 0L
    fun setVolume(volume: Float) { exoPlayer?.volume = volume }
    fun setPlaybackSpeed(speed: Float) { exoPlayer?.setPlaybackSpeed(speed) }

    // ──────────────────────────────────────────────────────────────────────────
    // Subtitle Track Management
    // ──────────────────────────────────────────────────────────────────────────


    fun getSubtitleTracks(): List<SubtitleTrack> {
        val player = exoPlayer ?: return emptyList()
        val mapped = mutableListOf<SubtitleTrack>()
        val groups = player.currentTracks.groups

        for (i in groups.indices) {
            val group = groups[i]
            if (group.type == C.TRACK_TYPE_TEXT) {
                for (j in 0 until group.length) {
                    val format = group.getTrackFormat(j)
                    val label = format.label ?: "Subtitle ${j + 1}"
                    val language = format.language ?: "und"
                    val id = format.id ?: "$i-$j"

                    mapped.add(SubtitleTrack(id, label, language, i, j))
                }
            }
        }
        return mapped
    }

    /**
     * Selects a specific subtitle track.
     * Use the groupIndex and trackIndex from getSubtitleTracks().
     */
    @OptIn(UnstableApi::class)
    fun selectSubtitleTrack(groupIndex: Int, trackIndex: Int) {
        val player = exoPlayer ?: return

        try {
            val groups = player.currentTracks.groups
            if (groupIndex !in groups.indices) return

            val group = groups[groupIndex]
            if (group.type != C.TRACK_TYPE_TEXT || trackIndex !in 0 until group.length) return

            val override = TrackSelectionOverride(group.mediaTrackGroup, trackIndex)

            val parameters = player.trackSelectionParameters
                .buildUpon()
                .clearOverridesOfType(C.TRACK_TYPE_TEXT)   // Remove any previous subtitle override
                .addOverride(override)
                .build()

            player.trackSelectionParameters = parameters

            Log.d("ExoPlayerManager", "Subtitle selected → group=$groupIndex, track=$trackIndex, lang=${group.getTrackFormat(trackIndex).language}")
        } catch (e: Exception) {
            Log.e("ExoPlayerManager", "Failed to select subtitle track", e)
        }
    }

    /**
     * Disables all subtitles (removes any override and lets ExoPlayer decide).
     */
    @OptIn(UnstableApi::class)
    fun disableSubtitles() {
        val player = exoPlayer ?: return

        try {
            val parameters = player.trackSelectionParameters
                .buildUpon()
                .clearOverridesOfType(C.TRACK_TYPE_TEXT)
                // Optional: explicitly disable text selection
                // .setPreferredTextLanguage(null)
                .build()

            player.trackSelectionParameters = parameters
            Log.d("ExoPlayerManager", "Subtitles disabled")
        } catch (e: Exception) {
            Log.e("ExoPlayerManager", "Failed to disable subtitles", e)
        }
    }

    /**
     * Checks if any subtitle track is currently active/selected.
     */
    fun isSubtitleActive(): Boolean {
        val player = exoPlayer ?: return false

        // Better way: check if any text track group has isSelected = true
        return player.currentTracks.groups.any { group ->
            group.type == C.TRACK_TYPE_TEXT && group.isSelected
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Equalizer
    // ──────────────────────────────────────────────────────────────────────────

    private fun initializeEqualizer() {
        try {
            if (audioSessionId != 0) {
                equalizer?.release()
                equalizer = Equalizer(0, audioSessionId)
                Log.d("ExoPlayerManager", "Equalizer initialized with session: $audioSessionId")
            }
        } catch (e: Exception) {
            Log.e("ExoPlayerManager", "Failed to initialize equalizer", e)
        }
    }

    fun setEqualizer(bands: List<Float>) {
        _equalizerBands = bands
        if (_equalizerEnabled) {
            applyEqualizerSettings()
        }
    }

    fun enableEqualizer(enabled: Boolean) {
        _equalizerEnabled = enabled
        if (enabled) {
            initializeEqualizer()
            applyEqualizerSettings()
        } else {
            disableEqualizer()
        }
    }

    fun isEqualizerEnabled(): Boolean = _equalizerEnabled
    fun getEqualizerBands(): List<Float> = _equalizerBands

    private fun applyEqualizerSettings() {
        try {
            if (audioSessionId == 0) {
                Log.w("ExoPlayerManager", "No audio session available for equalizer")
                return
            }
            if (equalizer == null) {
                initializeEqualizer()
            }
            equalizer?.apply {
                enabled = true
                val numberOfBands = numberOfBands.coerceAtMost(10)
                for (i in 0 until numberOfBands) {
                    val gain = (_equalizerBands.getOrNull(i) ?: 0f) * 1000
                    setBandLevel(i.toShort(), gain.toInt().toShort())
                }
            }
        } catch (e: Exception) {
            Log.e("ExoPlayerManager", "Failed to apply equalizer settings", e)
        }
    }

    private fun disableEqualizer() {
        try {
            equalizer?.apply {
                enabled = false
                val numberOfBands = numberOfBands.coerceAtMost(10)
                for (i in 0 until numberOfBands) {
                    setBandLevel(i.toShort(), 0)
                }
            }
            Log.d("ExoPlayerManager", "Equalizer disabled")
        } catch (e: Exception) {
            Log.e("ExoPlayerManager", "Failed to disable equalizer", e)
        }
    }

    fun resetEqualizer() {
        _equalizerEnabled = false
        _equalizerBands = List(10) { 0f }
        disableEqualizer()
        Log.d("ExoPlayerManager", "Equalizer reset")
    }

    fun resetForNewVideo() {
        Log.d("ExoPlayerManager", "resetForNewVideo")
        currentVideoUri = null
        pendingStartPosition = 0L
        pendingPlay = false
        _isPlayerReady.value = false
    }

    fun release() {
        try {
            equalizer?.release()
            equalizer = null
        } catch (e: Exception) { }
        serviceScope?.cancel()
        mediaSession?.release()
        exoPlayer?.release()
        exoPlayer = null
        mediaSession = null
        serviceScope = null
        isInitialized = false
        currentVideoUri = null
        _isPlayerReady.value = false
        pendingStartPosition = 0L
        pendingPlay = false
        _equalizerEnabled = false
        _equalizerBands = List(10) { 0f }
        audioSessionId = 0
        audioOnlyMode = false
    }
}