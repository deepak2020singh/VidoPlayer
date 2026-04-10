package com.foss.vidoplay.presentation.ui

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.HdrOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AspectRatio
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Subtitles
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Popup
import androidx.core.app.PictureInPictureModeChangedInfo
import androidx.core.graphics.scale
import androidx.core.util.Consumer
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.foss.vidoplay.MainActivity
import com.foss.vidoplay.data.repos.LastPlayedInfo
import com.foss.vidoplay.domain.model.VideoFile
import com.foss.vidoplay.presentation.common.Bookmark
import com.foss.vidoplay.presentation.common.CropMode
import com.foss.vidoplay.presentation.common.EnhancementMode
import com.foss.vidoplay.presentation.common.EqualizerPreset
import com.foss.vidoplay.presentation.common.ExoPlayerManager
import com.foss.vidoplay.presentation.common.ExoPlayerState
import com.foss.vidoplay.presentation.common.GlassAnimations
import com.foss.vidoplay.presentation.common.GlassTokens
import com.foss.vidoplay.presentation.common.SubtitleTrack
import com.foss.vidoplay.presentation.common.ZoomPreset
import com.foss.vidoplay.presentation.common.formatDuration
import com.foss.vidoplay.presentation.common.formatFileSize
import com.foss.vidoplay.presentation.common.glass
import com.foss.vidoplay.presentation.common.glassPanel
import com.foss.vidoplay.presentation.common.glassPill
import com.foss.vidoplay.presentation.common.keepScreenOn
import com.foss.vidoplay.presentation.common.resolveResizeMode
import com.foss.vidoplay.presentation.viewModel.ExoPlayerViewModel
import com.foss.vidoplay.presentation.viewModel.LastPlayedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin


@OptIn(UnstableApi::class)
@Composable
fun ExoPlayerScreen(
    video: VideoFile,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onClose: () -> Unit,
    viewModel: ExoPlayerViewModel,
    startPosition: Long = 0L,
    isShortVideoMode: Boolean = false,
    innerPadding: PaddingValues
) {
    val lastPlayedViewModel: LastPlayedViewModel = koinViewModel()
    val context = LocalContext.current
    val activity = context as? Activity
    val mainActivity = activity as? MainActivity
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current
    val scope = rememberCoroutineScope()

    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.primary
    val secondaryColor = colorScheme.secondary
    val onSurfaceColor = colorScheme.onSurface
    val errorColor = colorScheme.error
    val onPrimary = colorScheme.onPrimary

    // States
    var isTransitioning by remember { mutableStateOf(false) }
    var isInPipMode by remember { mutableStateOf(false) }

    // Observe playlist and index from ViewModel
    val state by viewModel.state.collectAsState()
    val playlist = state.playlist
    val currentIndex = state.currentIndex

    val hasPrevious = viewModel.hasPrevious()
    val hasNext = viewModel.hasNext()

    var thumbnailBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Update when video changes (to reset transition flag)
    LaunchedEffect(state.currentVideo?.id) {
        if (state.currentVideo?.id != video.id) {
            isTransitioning = false
        }
    }

    // Debug logging
    LaunchedEffect(currentIndex, playlist.size, state.currentVideo) {
        Log.d("ExoPlayerDebug", "=== Navigation State ===")
        Log.d("ExoPlayerDebug", "Current Video: ${state.currentVideo?.name}")
        Log.d("ExoPlayerDebug", "Current Index: $currentIndex")
        Log.d("ExoPlayerDebug", "Total Videos: ${playlist.size}")
        Log.d("ExoPlayerDebug", "Has Previous: $hasPrevious")
        Log.d("ExoPlayerDebug", "Has Next: $hasNext")
    }

    // Glass theme values
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val textTertiary = GlassTokens.getTextTertiary()
    val chipBg = GlassTokens.getChipBg()
    val chipBorder = GlassTokens.getChipBorder()
    val isDark = GlassTokens.isDarkTheme()
    val redAccent = GlassTokens.RedAccent
    val amberDot = GlassTokens.AmberDot

    // UI States
    var isLandscape by remember { mutableStateOf(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) }
    var isControlsVisible by remember { mutableStateOf(true) }
    var showBottomControls by remember { mutableStateOf(true) }
    var showVideoInfo by remember { mutableStateOf(false) }
    var showEqualizerPanel by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }
    var isSeeking by remember { mutableStateOf(false) }
    var seekPosition by remember { mutableLongStateOf(0L) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showSleepTimerMenu by remember { mutableStateOf(false) }
    var showVideoAdjustments by remember { mutableStateOf(false) }
    var showAudioMenu by remember { mutableStateOf(false) }
    var showSpeedMenu by remember { mutableStateOf(false) }
    var showAdvancedMenu by remember { mutableStateOf(false) }
    var showScreenshotMessage by remember { mutableStateOf(false) }
    var screenshotMessage by remember { mutableStateOf("") }
    var screenshotSuccess by remember { mutableStateOf(true) }
    var isDraggingVolume by remember { mutableStateOf(false) }
    var isDraggingBrightness by remember { mutableStateOf(false) }
    var dragStartVolume by remember { mutableFloatStateOf(0f) }
    var dragStartBrightness by remember { mutableFloatStateOf(0f) }
    var showVolumeIndicator by remember { mutableStateOf(false) }
    var showBrightnessIndicator by remember { mutableStateOf(false) }
    var isCapturingScreenshot by remember { mutableStateOf(false) }
    var playerViewRef by remember { mutableStateOf<PlayerView?>(null) }
    var speedBadgeVisible by remember { mutableStateOf(false) }
    var speedBoostValue by remember { mutableFloatStateOf(2f) }
    var showSubtitleMenu by remember { mutableStateOf(false) }

    data class SeekTrigger(val side: SeekSide, val seconds: Int, val id: Int = 0)

    var seekTrigger by remember { mutableStateOf<SeekTrigger?>(null) }

    val playerReady by ExoPlayerManager.isPlayerReady.collectAsState()
    val currentBookmarks = remember(state.bookmarks, state.currentVideo) {
        viewModel.getBookmarksForCurrentVideo()
    }
    val hasBookmarks = currentBookmarks.isNotEmpty()

    val effectiveStartPosition: Long = remember(video.id, startPosition) {
        when {
            startPosition > 0L -> startPosition
            else -> {
                val info = lastPlayedViewModel.getResumableVideo()
                if (info != null && info.videoId == video.id && info.position in 1L until video.duration)
                    info.position else 0L
            }
        }
    }

    val exoPlayer = remember { ExoPlayerManager.initialize(context) }

    LaunchedEffect(video.uri) {
        Log.d("ExoPlayerScreen", "Preparing initial video: ${video.name}")
        ExoPlayerManager.prepareVideo(
            uri = video.uri.toString(),
            startPosition = effectiveStartPosition,
            shouldPlay = isPlaying
        )
    }

    LaunchedEffect(state.currentVideo) {
        val newVideo = state.currentVideo
        if (newVideo != null && newVideo.id != video.id) {
            Log.d("ExoPlayerScreen", "Switching to: ${newVideo.name}")
            ExoPlayerManager.prepareVideo(
                uri = newVideo.uri.toString(),
                startPosition = 0L,
                shouldPlay = true
            )
        }
    }

    LaunchedEffect(isPlaying) {
        if (!isInPipMode && !isTransitioning) {
            if (isPlaying) ExoPlayerManager.play() else ExoPlayerManager.pause()
        }
        viewModel.updatePlaybackState(isPlaying = isPlaying)
    }

    LaunchedEffect(state.isMuted, state.volume) {
        ExoPlayerManager.setVolume(if (state.isMuted) 0f else state.volume)
    }

    LaunchedEffect(state.playbackSpeed) {
        ExoPlayerManager.setPlaybackSpeed(state.playbackSpeed)
    }

    LaunchedEffect(configuration) {
        isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    // === Navigation: delegate to ViewModel ===
    val performSmoothNext = {
        if (!isTransitioning && hasNext) {
            isTransitioning = true
            viewModel.playNext()
            scope.launch {
                delay(300)
                isTransitioning = false
            }
        }
    }

    val performSmoothPrevious = {
        if (!isTransitioning && hasPrevious) {
            isTransitioning = true
            viewModel.playPrevious()
            scope.launch {
                delay(300)
                isTransitioning = false
            }
        }
    }

    // Handle Close - ONLY THIS BUTTON CHANGES ORIENTATION TO PORTRAIT
    val handleClose = {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        if (state.isFullScreen) viewModel.toggleFullScreen()
        ExoPlayerManager.pause()
        ExoPlayerManager.resetForNewVideo()
        onClose()
    }

    // Polling for position
    LaunchedEffect(Unit) {
        var lastSavedPos = -1L
        while (true) {
            delay(1000)
            if (!playerReady || isSeeking || isTransitioning) continue
            try {
                val pos = ExoPlayerManager.getCurrentPosition()
                val dur = ExoPlayerManager.getDuration()
                val playing = ExoPlayerManager.isPlaying()
                viewModel.updatePlaybackState(
                    isPlaying = playing,
                    currentPosition = pos,
                    duration = dur
                )
                if (playing && abs(pos - lastSavedPos) >= 2000L) {
                    lastPlayedViewModel.savePosition(pos)
                    lastSavedPos = pos
                }
            } catch (e: Exception) {
                Log.w("ExoPlayerScreen", "Polling skipped: ${e.message}")
            }
        }
    }

    LaunchedEffect(video.id) {
        lastPlayedViewModel.save(
            LastPlayedInfo(
                videoId = video.id,
                folderPath = video.folderPath,
                videoName = video.name,
                videoPath = video.path,
                thumbnailUri = video.thumbnailUri?.toString(),
                duration = video.duration,
                position = effectiveStartPosition
            )
        )
        viewModel.clearBookmarks()
    }

    // Load subtitle tracks when player is ready
    LaunchedEffect(playerReady, state.currentVideo) {
        if (playerReady) {
            viewModel.loadSubtitleTracks()
        }
    }

    // Auto-hide controls
    LaunchedEffect(isControlsVisible, state.isScreenLocked, showMoreMenu, isCapturingScreenshot) {
        if (isControlsVisible && !state.isScreenLocked && !isDraggingVolume && !isDraggingBrightness && !showMoreMenu && !isCapturingScreenshot && !isTransitioning) {
            delay(3000)
            isControlsVisible = false
        }
    }

    // Fullscreen handling - Keep landscape mode when in fullscreen
    LaunchedEffect(state.isFullScreen) {
        activity?.let {
            if (state.isFullScreen) {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                WindowCompat.setDecorFitsSystemWindows(it.window, false)
                WindowInsetsControllerCompat(it.window, it.window.decorView).apply {
                    hide(WindowInsetsCompat.Type.systemBars())
                    systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                WindowCompat.setDecorFitsSystemWindows(it.window, true)
                WindowInsetsControllerCompat(
                    it.window,
                    it.window.decorView
                ).show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    // Lifecycle & PIP handling
    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (!isInPipMode && !isTransitioning) {
                        ExoPlayerManager.pause()
                        viewModel.updatePlaybackState(isPlaying = false)
                        scope.launch {
                            lastPlayedViewModel.savePositionImmediate(ExoPlayerManager.getCurrentPosition())
                        }
                    }
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (state.isPlaying && !isInPipMode && !isTransitioning)
                        ExoPlayerManager.play()
                }

                Lifecycle.Event.ON_DESTROY -> {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    ExoPlayerManager.pause()
                    ExoPlayerManager.resetForNewVideo()
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            scope.launch {
                lastPlayedViewModel.savePositionImmediate(ExoPlayerManager.getCurrentPosition())
            }
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            ExoPlayerManager.pause()
            ExoPlayerManager.resetForNewVideo()
        }
    }

    // PIP Handlers
    LaunchedEffect(state.videoWidth, state.videoHeight, state.isPlaying, hasNext, hasPrevious) {
        mainActivity?.onPipPlayPause = {
            val currentlyPlaying = ExoPlayerManager.isPlaying()
            val newIsPlaying = !currentlyPlaying
            if (newIsPlaying) ExoPlayerManager.play() else ExoPlayerManager.pause()
            onPlayPause()
            mainActivity.updatePipParams(
                state.videoWidth,
                state.videoHeight,
                newIsPlaying,
                hasNext,
                hasPrevious
            )
        }
        mainActivity?.onPipPrevious = {
            if (hasPrevious) performSmoothPrevious()
            mainActivity.updatePipParams(
                state.videoWidth,
                state.videoHeight,
                state.isPlaying,
                hasNext,
                hasPrevious
            )
        }
        mainActivity?.onPipNext = {
            if (hasNext) performSmoothNext()
            mainActivity.updatePipParams(
                state.videoWidth,
                state.videoHeight,
                state.isPlaying,
                hasNext,
                hasPrevious
            )
        }
        mainActivity?.updatePipParams(
            state.videoWidth,
            state.videoHeight,
            state.isPlaying,
            hasNext,
            hasPrevious
        )
    }

    DisposableEffect(activity) {
        if (activity !is MainActivity) return@DisposableEffect onDispose {}
        val listener = Consumer<PictureInPictureModeChangedInfo> { info ->
            isInPipMode = info.isInPictureInPictureMode
            if (isInPipMode) {
                isControlsVisible = false
                showBottomControls = false
                showMoreMenu = false
                showVideoAdjustments = false
                showAdvancedMenu = false
                showVideoInfo = false
                showEqualizerPanel = false
                showSuggestions = false
                showAudioMenu = false
                showSpeedMenu = false
                showSleepTimerMenu = false
                showSubtitleMenu = false
                isDraggingVolume = false
                isDraggingBrightness = false
                showVolumeIndicator = false
                showBrightnessIndicator = false
            }
        }
        activity.addOnPictureInPictureModeChangedListener(listener)
        onDispose { activity.removeOnPictureInPictureModeChangedListener(listener) }
    }

    val isBuffering = exoPlayer.playbackState == Player.STATE_BUFFERING

    // Animations
    val controlsAlpha by animateFloatAsState(
        targetValue = if ((isControlsVisible && !state.isScreenLocked && !isInPipMode && !isBuffering) || showMoreMenu) 1f else 0f,
        animationSpec = tween(
            250,
            delayMillis = if (isBuffering) 0 else 350,
            easing = FastOutSlowInEasing
        ),
        label = "controlsAlpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        0.25f,
        0.6f,
        infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glowAlpha"
    )
    val glowScale by infiniteTransition.animateFloat(
        1f,
        1.18f,
        infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glowScale"
    )

    // Custom slide animations for bottom controls row
    val bottomRowSlideIn = slideInHorizontally(
        initialOffsetX = { it },   // start from right
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    ) + fadeIn(tween(200))

    val bottomRowSlideOut = slideOutHorizontally(
        targetOffsetX = { it },    // exit to right
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    ) + fadeOut(tween(200))

    // Main UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color.Black else Color.White)
            .padding(innerPadding)
            .keepScreenOn(enabled = state.isPlaying && !isInPipMode)
            .pointerInput(
                isInPipMode,
                state.isScreenLocked,
                isCapturingScreenshot,
                showMoreMenu,
                isTransitioning
            ) {
                if (isInPipMode || state.isScreenLocked || isCapturingScreenshot || showMoreMenu || isTransitioning) return@pointerInput
                detectTapGestures(
                    onTap = {
                        isControlsVisible = !isControlsVisible
                        viewModel.toggleControls(isControlsVisible)
                    },
                    onDoubleTap = { tapOffset ->
                        val thirds = size.width / 3f
                        when {
                            tapOffset.x < thirds -> {
                                val newPos =
                                    (ExoPlayerManager.getCurrentPosition() - 10_000L).coerceAtLeast(
                                        0
                                    )
                                ExoPlayerManager.seekTo(newPos)
                                viewModel.updatePlaybackState(currentPosition = newPos)
                                seekTrigger =
                                    SeekTrigger(SeekSide.LEFT, 10, (seekTrigger?.id ?: 0) + 1)
                            }

                            tapOffset.x > thirds * 2 -> {
                                val newPos =
                                    (ExoPlayerManager.getCurrentPosition() + 10_000L).coerceAtMost(
                                        ExoPlayerManager.getDuration()
                                    )
                                ExoPlayerManager.seekTo(newPos)
                                viewModel.updatePlaybackState(currentPosition = newPos)
                                seekTrigger =
                                    SeekTrigger(SeekSide.RIGHT, 10, (seekTrigger?.id ?: 0) + 1)
                            }

                            else -> {
                                scale = if (scale > 1f) 1f else 2f
                                offset = Offset.Zero
                            }
                        }
                    },
                    onLongPress = {
                        if (!isInPipMode) {
                            speedBoostValue = 2f
                            speedBadgeVisible = true
                            viewModel.enterSpeedGesture(boostSpeed = 2f)
                        }
                    },
                    onPress = {
                        tryAwaitRelease()
                        if (speedBadgeVisible) {
                            speedBadgeVisible = false
                            viewModel.exitSpeedGesture()
                        }
                    }
                )
            }
    ) {
        // Ambient background
        if (!isInPipMode) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2
                val cy = size.height / 2
                for (i in 0..5) {
                    val angle = (System.currentTimeMillis() / 50.0 + i * 60).toFloat()
                    val x = cx + cos(angle * PI.toFloat() / 180f) * size.width * 0.3f
                    val y = cy + sin(angle * PI.toFloat() / 180f) * size.height * 0.3f
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(primaryColor.copy(alpha = 0.08f), Color.Transparent),
                            Offset(x, y)
                        ),
                        radius = size.width * 0.4f,
                        center = Offset(x, y)
                    )
                }
            }
        }

        // Video surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isInPipMode) {
                    if (isInPipMode) return@pointerInput
                    detectTransformGestures { _, pan, zoom, _ ->
                        if (state.isZoomEnabled && !state.isScreenLocked) {
                            val newScale = max(1f, min(3f, scale * zoom))
                            scale = newScale
                            val maxX = (size.width * (scale - 1)) / 2
                            val maxY = (size.height * (scale - 1)) / 2
                            offset = Offset(
                                max(-maxX, min(maxX, offset.x + pan.x)),
                                max(-maxY, min(maxY, offset.y + pan.y))
                            )
                        }
                    }
                }
                .graphicsLayer {
                    scaleX = scale; scaleY = scale
                    translationX = offset.x; translationY = offset.y
                    rotationZ = state.rotation.toFloat()
                    if (state.flipHorizontal) scaleX = -scaleX
                    if (state.flipVertical) scaleY = -scaleY
                    transformOrigin = TransformOrigin.Center
                }
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        resizeMode = resolveResizeMode(isShortVideoMode, state)
                        setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                    }.also { playerViewRef = it }
                },
                update = { pv ->
                    pv.resizeMode = resolveResizeMode(isShortVideoMode, state)
                    playerViewRef = pv
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        if (!isInPipMode) {
            // Buffering overlay
            AnimatedVisibility(
                visible = isBuffering,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(400, delayMillis = 300)),
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .drawWithCache {
                                    onDrawBehind {
                                        val sweep =
                                            (System.currentTimeMillis() % 1800) / 1800f * 360f
                                        drawArc(
                                            Brush.sweepGradient(
                                                listOf(
                                                    Color.Transparent,
                                                    primaryColor,
                                                    secondaryColor
                                                )
                                            ), sweep, 270f, false, style = Stroke(5f)
                                        )
                                    }
                                }, contentAlignment = Alignment.Center
                        ) {}
                        Spacer(Modifier.height(14.dp))
                        Text(
                            "Buffering…",
                            color = textSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Volume Control
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(if (isLandscape) 100.dp else 80.dp)
                    .align(Alignment.CenterStart)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { _ ->
                                if (!state.isScreenLocked && !showMoreMenu && !isCapturingScreenshot && !isTransitioning) {
                                    isDraggingVolume = true
                                    dragStartVolume = if (state.isMuted) 0f else state.volume
                                    showVolumeIndicator = true
                                    isControlsVisible = true
                                }
                            },
                            onDrag = { _, dragAmount ->
                                if (!state.isScreenLocked && !showMoreMenu && !isCapturingScreenshot && !isTransitioning) {
                                    val dragDelta = -dragAmount.y / (size.height / 1.5f)
                                    val newVol = (dragStartVolume + dragDelta).coerceIn(0f, 1f)
                                    viewModel.updatePlaybackState(
                                        volume = newVol,
                                        isMuted = newVol == 0f
                                    )
                                    ExoPlayerManager.setVolume(newVol)
                                    dragStartVolume = newVol
                                    showVolumeIndicator = true
                                }
                            },
                            onDragEnd = {
                                isDraggingVolume = false
                                scope.launch {
                                    delay(1500); if (!isDraggingVolume) showVolumeIndicator = false
                                }
                            },
                            onDragCancel = { isDraggingVolume = false; showVolumeIndicator = false }
                        )
                    }
            )

            // Volume Vertical Slider
            GlassVerticalSlider(
                value = if (state.isMuted) 0f else state.volume,
                onValueChange = { newVol ->
                    viewModel.updatePlaybackState(volume = newVol, isMuted = newVol == 0f)
                    ExoPlayerManager.setVolume(newVol)
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 80.dp),
                color = primaryColor,
                label = "Volume",
                isVisible = showVolumeIndicator && isDraggingVolume
            )

            // Volume Compact Indicator
            AnimatedVisibility(
                visible = showVolumeIndicator && !isDraggingVolume,
                enter = GlassAnimations.IndicatorEnter, exit = GlassAnimations.IndicatorExit,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 24.dp)
                        .size(80.dp)
                        .glass(cornerRadius = 20.dp, bgAlpha = 0.72f)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            if (state.isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                            null,
                            tint = if (state.isMuted) errorColor else primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "${(if (state.isMuted) 0f else state.volume * 100).toInt()}%",
                            color = textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        GlassProgressBar(
                            progress = if (state.isMuted) 0f else state.volume,
                            color = primaryColor,
                            modifier = Modifier.width(50.dp)
                        )
                    }
                }
            }

            // Brightness Control
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(if (isLandscape) 100.dp else 80.dp)
                    .align(Alignment.CenterEnd)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { _ ->
                                if (!state.isScreenLocked && !showMoreMenu && !isCapturingScreenshot && !isTransitioning) {
                                    isDraggingBrightness = true
                                    dragStartBrightness = state.brightness
                                    showBrightnessIndicator = true
                                    isControlsVisible = true
                                }
                            },
                            onDrag = { _, dragAmount ->
                                if (!state.isScreenLocked && !showMoreMenu && !isCapturingScreenshot && !isTransitioning) {
                                    val dragDelta = -dragAmount.y / (size.height / 1.5f)
                                    val newB = (dragStartBrightness + dragDelta).coerceIn(0f, 1f)
                                    viewModel.setBrightness(newB)
                                    dragStartBrightness = newB
                                    showBrightnessIndicator = true
                                }
                            },
                            onDragEnd = {
                                isDraggingBrightness = false
                                scope.launch {
                                    delay(1500); if (!isDraggingBrightness) showBrightnessIndicator =
                                    false
                                }
                            },
                            onDragCancel = {
                                isDraggingBrightness = false; showBrightnessIndicator = false
                            }
                        )
                    }
            )

            // Brightness Vertical Slider
            GlassVerticalSlider(
                value = state.brightness,
                onValueChange = { newB -> viewModel.setBrightness(newB) },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 80.dp),
                color = amberDot,
                label = "Brightness",
                isVisible = showBrightnessIndicator && isDraggingBrightness
            )

            // Brightness Compact Indicator
            AnimatedVisibility(
                visible = showBrightnessIndicator && !isDraggingBrightness,
                enter = GlassAnimations.IndicatorEnter, exit = GlassAnimations.IndicatorExit,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = 24.dp)
                        .size(80.dp)
                        .glass(cornerRadius = 20.dp, bgAlpha = 0.72f)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.BrightnessHigh,
                            null,
                            tint = amberDot,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "${(state.brightness * 100).toInt()}%",
                            color = textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        GlassProgressBar(
                            progress = state.brightness,
                            color = amberDot,
                            modifier = Modifier.width(50.dp)
                        )
                    }
                }
            }

            // Screenshot toast
            AnimatedVisibility(
                visible = showScreenshotMessage,
                enter = GlassAnimations.ToastEnter, exit = GlassAnimations.ToastExit,
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 88.dp, start = 28.dp, end = 28.dp)
                        .glass(cornerRadius = 16.dp, bgAlpha = 0.80f)
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        if (screenshotSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                        null,
                        tint = if (screenshotSuccess) Color(0xFF4CAF50) else errorColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        screenshotMessage,
                        color = textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Lock screen overlay
            AnimatedVisibility(
                visible = state.isScreenLocked,
                enter = GlassAnimations.OverlayEnter, exit = GlassAnimations.OverlayExit
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .glass(cornerRadius = 44.dp, bgAlpha = 0.60f),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                null,
                                tint = textPrimary,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                        Text(
                            "Screen Locked",
                            color = textPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Double tap to unlock", color = textSecondary, fontSize = 14.sp)
                    }
                }
            }

            // Sleep timer pill
            AnimatedVisibility(
                visible = state.sleepTimerActive,
                enter = fadeIn(tween(300)) + scaleIn(
                    initialScale = 0.7f,
                    animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh)
                ),
                exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.7f),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp, end = 16.dp)
                        .glassPill()
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Timer,
                        null,
                        tint = primaryColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        viewModel.getSleepTimerRemainingTime(),
                        color = textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Speed badge
            SpeedGestureBadge(
                visible = speedBadgeVisible,
                boostSpeed = speedBoostValue,
                boostDurationMs = 0L,
                accentColor = redAccent,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // Seek ripple overlays
            key(seekTrigger?.id to SeekSide.LEFT) {
                SeekRippleOverlay(
                    side = if (seekTrigger?.side == SeekSide.LEFT) SeekSide.LEFT else SeekSide.NONE,
                    seekSeconds = seekTrigger?.seconds ?: 10,
                    triggerKey = seekTrigger?.id ?: 0,
                    accentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxHeight()
                        .fillMaxWidth(0.38f)
                )
            }
            key(seekTrigger?.id to SeekSide.RIGHT) {
                SeekRippleOverlay(
                    side = if (seekTrigger?.side == SeekSide.RIGHT) SeekSide.RIGHT else SeekSide.NONE,
                    seekSeconds = seekTrigger?.seconds ?: 10,
                    triggerKey = seekTrigger?.id ?: 0,
                    accentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .fillMaxWidth(0.38f)
                )
            }

            // ── MAIN CONTROLS OVERLAY ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = controlsAlpha }) {

                // Top Bar
                AnimatedVisibility(
                    visible = (isControlsVisible && !state.isScreenLocked) || showMoreMenu,
                    enter = GlassAnimations.TopBarEnter, exit = GlassAnimations.TopBarExit,
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = if (isLandscape) 20.dp else 12.dp,
                                vertical = if (isLandscape) 10.dp else 12.dp
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Combined Back + Title Pill
                        Row(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .glassPill(cornerRadius = 30.dp)
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = if (isLandscape) 8.dp else 6.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Back button inside the pill
                            Box(
                                modifier = Modifier
                                    .size(if (isLandscape) 28.dp else 24.dp)
                                    .clip(CircleShape)
                                    .clickable { handleClose() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    "Back",
                                    tint = textPrimary,
                                    modifier = Modifier.size(if (isLandscape) 18.dp else 16.dp)
                                )
                            }

                            // Index badge (if playlist not empty)
                            if (playlist.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(if (isLandscape) 22.dp else 18.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(primaryColor, secondaryColor)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${currentIndex + 1}",
                                        color = onPrimary,
                                        fontSize = if (isLandscape) 11.sp else 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Title text
                            Text(
                                text = state.currentVideo?.name ?: "",
                                color = textPrimary,
                                fontSize = if (isLandscape) 14.sp else 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            // Optional: total count badge on the right edge
                            if (playlist.isNotEmpty()) {
                                Text(
                                    "/${playlist.size}",
                                    color = textSecondary,
                                    fontSize = if (isLandscape) 12.sp else 11.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }

                        // Right action buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(if (isLandscape) 8.dp else 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // PiP button
                            Box(
                                modifier = Modifier
                                    .size(if (isLandscape) 40.dp else 32.dp)
                                    .clip(CircleShape)
                                    .background(chipBg)
                                    .border(1.dp, chipBorder, CircleShape)
                                    .clickable { mainActivity?.enterPipIfPlaying() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PictureInPicture,
                                    "PiP",
                                    tint = textPrimary,
                                    modifier = Modifier.size(if (isLandscape) 20.dp else 16.dp)
                                )
                            }
                            // Bookmark button
                            Box(
                                modifier = Modifier
                                    .size(if (isLandscape) 40.dp else 32.dp)
                                    .clip(CircleShape)
                                    .background(if (hasBookmarks) primaryColor.copy(alpha = 0.3f) else chipBg)
                                    .border(
                                        1.dp,
                                        if (hasBookmarks) primaryColor.copy(alpha = 0.5f) else chipBorder,
                                        CircleShape
                                    )
                                    .clickable {
                                        val pos = ExoPlayerManager.getCurrentPosition()
                                        viewModel.addBookmark(pos)
                                        Toast.makeText(
                                            context,
                                            "Bookmark added at ${formatDuration(pos)}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (hasBookmarks) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    "Add bookmark",
                                    tint = if (hasBookmarks) primaryColor else textPrimary,
                                    modifier = Modifier.size(if (isLandscape) 20.dp else 16.dp)
                                )
                            }
                            // More menu button
                            Box {
                                Box(
                                    modifier = Modifier
                                        .size(if (isLandscape) 40.dp else 32.dp)
                                        .clip(CircleShape)
                                        .background(if (showMoreMenu) primaryColor.copy(alpha = 0.4f) else chipBg)
                                        .border(
                                            1.dp,
                                            if (showMoreMenu) primaryColor.copy(alpha = 0.6f) else chipBorder,
                                            CircleShape
                                        )
                                        .clickable {
                                            showMoreMenu =
                                                !showMoreMenu; if (showMoreMenu) isControlsVisible =
                                            true
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        "More",
                                        tint = if (showMoreMenu) primaryColor else textPrimary,
                                        modifier = Modifier.size(if (isLandscape) 20.dp else 16.dp)
                                    )
                                }
                                DropdownMenu(
                                    expanded = showMoreMenu,
                                    onDismissRequest = { showMoreMenu = false },
                                    modifier = Modifier
                                        .glassPanel(cornerRadius = 16.dp)
                                        .width(210.dp)
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Audio Track",
                                                color = textPrimary,
                                                fontSize = 14.sp
                                            )
                                        },
                                        onClick = { showAudioMenu = true; showMoreMenu = false },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Outlined.Audiotrack,
                                                null,
                                                tint = onSurfaceColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        })
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                if (state.subtitleEnabled) "Subtitles: On" else "Subtitles: Off",
                                                color = textPrimary,
                                                fontSize = 14.sp
                                            )
                                        },
                                        onClick = {
                                            showSubtitleMenu = true
                                            showMoreMenu = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Outlined.Subtitles,
                                                null,
                                                tint = if (state.subtitleEnabled) primaryColor else onSurfaceColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        })
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Speed: ${state.playbackSpeed}x",
                                                color = textPrimary,
                                                fontSize = 14.sp
                                            )
                                        },
                                        onClick = { showSpeedMenu = true; showMoreMenu = false },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Outlined.Speed,
                                                null,
                                                tint = onSurfaceColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        })
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Filter: ${viewModel.getCurrentFilterLabel()}",
                                                color = textPrimary,
                                                fontSize = 14.sp
                                            )
                                        },
                                        onClick = { viewModel.cycleFilter(); showMoreMenu = false },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Filter,
                                                null,
                                                tint = if (state.activeFilter != null) primaryColor else onSurfaceColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        })
                                    HorizontalDivider(
                                        Modifier.padding(vertical = 4.dp),
                                        color = chipBorder
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Equalizer",
                                                color = textPrimary,
                                                fontSize = 14.sp
                                            )
                                        },
                                        onClick = {
                                            showEqualizerPanel = true; showMoreMenu = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Tune,
                                                null,
                                                tint = onSurfaceColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        })
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Sleep Timer",
                                                color = textPrimary,
                                                fontSize = 14.sp
                                            )
                                        },
                                        onClick = {
                                            showSleepTimerMenu = true; showMoreMenu = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Timer,
                                                null,
                                                tint = if (state.sleepTimerActive) primaryColor else onSurfaceColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        })
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Video Info",
                                                color = textPrimary,
                                                fontSize = 14.sp
                                            )
                                        },
                                        onClick = { showVideoInfo = true; showMoreMenu = false },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Info,
                                                null,
                                                tint = onSurfaceColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        })
                                    HorizontalDivider(
                                        Modifier.padding(vertical = 4.dp),
                                        color = chipBorder
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Video Adjustments",
                                                color = textPrimary,
                                                fontSize = 14.sp
                                            )
                                        },
                                        onClick = {
                                            showVideoAdjustments = true; showMoreMenu = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Adjust,
                                                null,
                                                tint = onSurfaceColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        })
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Advanced Controls",
                                                color = textPrimary,
                                                fontSize = 14.sp
                                            )
                                        },
                                        onClick = { showAdvancedMenu = true; showMoreMenu = false },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Tune,
                                                null,
                                                tint = onSurfaceColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        })
                                }
                            }
                        }
                    }
                }

                // Center playback controls
                AnimatedVisibility(
                    visible = isControlsVisible && !state.isScreenLocked,
                    enter = GlassAnimations.CenterControlsEnter,
                    exit = GlassAnimations.CenterControlsExit,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Row(
                        Modifier.padding(if (isLandscape) 8.dp else 16.dp),
                        Arrangement.Center,
                        Alignment.CenterVertically
                    ) {
                        // Previous Button - DISABLED when no previous video
                        GlassPlayerButton(
                            icon = Icons.Default.SkipPrevious,
                            enabled = hasPrevious && !isTransitioning,
                            size = 42.dp,
                            tint = if (hasPrevious && !isTransitioning) textPrimary else textTertiary
                        ) { performSmoothPrevious() }

                        Spacer(Modifier.width(12.dp))

                        // Rewind Button
                        GlassPlayerButton(
                            icon = Icons.Default.FastRewind,
                            enabled = true,
                            size = 46.dp,
                            label = "10",
                            tint = textPrimary
                        ) {
                            val newPos =
                                (ExoPlayerManager.getCurrentPosition() - 10000).coerceAtLeast(0)
                            ExoPlayerManager.seekTo(newPos)
                            viewModel.updatePlaybackState(currentPosition = newPos)
                        }

                        Spacer(Modifier.width(16.dp))

                        // Play/Pause Button
                        Box(Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                            if (state.isPlaying) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .graphicsLayer { scaleX = glowScale; scaleY = glowScale }
                                        .clip(CircleShape)
                                        .background(primaryColor.copy(alpha = glowAlpha * 0.4f))
                                        .blur(14.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(
                                                primaryColor.copy(0.85f),
                                                primaryColor
                                            )
                                        )
                                    )
                                    .border(
                                        width = 1.5.dp,
                                        brush = Brush.linearGradient(
                                            listOf(
                                                Color.White.copy(0.50f),
                                                Color.White.copy(0.15f)
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        val newPlaying = !state.isPlaying
                                        if (newPlaying) ExoPlayerManager.play() else ExoPlayerManager.pause()
                                        viewModel.updatePlaybackState(isPlaying = newPlaying)
                                        onPlayPause()
                                    }, contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    null,
                                    tint = onPrimary,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }

                        Spacer(Modifier.width(16.dp))

                        // Fast Forward Button
                        GlassPlayerButton(
                            icon = Icons.Default.FastForward,
                            enabled = true,
                            size = 46.dp,
                            label = "10",
                            tint = textPrimary
                        ) {
                            val newPos =
                                (ExoPlayerManager.getCurrentPosition() + 10000).coerceAtMost(state.duration)
                            ExoPlayerManager.seekTo(newPos)
                            viewModel.updatePlaybackState(currentPosition = newPos)
                        }

                        Spacer(Modifier.width(12.dp))

                        // Next Button - DISABLED when no next video
                        GlassPlayerButton(
                            icon = Icons.Default.SkipNext,
                            enabled = hasNext && !isTransitioning,
                            size = 42.dp,
                            tint = if (hasNext && !isTransitioning) textPrimary else textTertiary
                        ) { performSmoothNext() }
                    }
                }

                // Bottom bar
                AnimatedVisibility(
                    visible = (isControlsVisible && !state.isScreenLocked) || showMoreMenu,
                    enter = GlassAnimations.PanelEnter, exit = GlassAnimations.PanelExit,
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = if (isLandscape) 16.dp else 12.dp,
                                vertical = if (isLandscape) 8.dp else 12.dp
                            )
                    ) {
                        AnimatedVisibility(
                            visible = showAudioMenu,
                            enter = GlassAnimations.SubMenuEnter,
                            exit = GlassAnimations.SubMenuExit
                        ) {
                            DynamicAudioMenu(
                                state.selectedAudioTrack,
                                { viewModel.setAudioTrack(it); showAudioMenu = false },
                                { showAudioMenu = false })
                        }
                        AnimatedVisibility(
                            visible = showSpeedMenu,
                            enter = GlassAnimations.SubMenuEnter,
                            exit = GlassAnimations.SubMenuExit
                        ) {
                            DynamicSpeedMenu(
                                state.playbackSpeed,
                                { viewModel.setPlaybackSpeed(it); showSpeedMenu = false },
                                { showSpeedMenu = false })
                        }
                        AnimatedVisibility(
                            visible = showSleepTimerMenu,
                            enter = GlassAnimations.SubMenuEnter,
                            exit = GlassAnimations.SubMenuExit
                        ) {
                            DynamicSleepTimerMenu(viewModel) { showSleepTimerMenu = false }
                        }
                        AnimatedVisibility(
                            visible = showSubtitleMenu,
                            enter = GlassAnimations.SubMenuEnter,
                            exit = GlassAnimations.SubMenuExit
                        ) {
                            DynamicSubtitleMenu(
                                tracks = state.availableSubtitleTracks,
                                selectedTrack = state.selectedSubtitleTrack,
                                onTrackSelected = { track ->
                                    viewModel.selectSubtitleTrack(track)
                                    showSubtitleMenu = false
                                },
                                onDisable = {
                                    viewModel.selectSubtitleTrack(null)
                                    showSubtitleMenu = false
                                },
                                onDismiss = { showSubtitleMenu = false }
                            )
                        }

                        // Bottom Controls Row (with horizontal slide animation)
                        AnimatedVisibility(
                            visible = showBottomControls,
                            enter = bottomRowSlideIn,
                            exit = bottomRowSlideOut
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                Arrangement.SpaceBetween,
                                Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    GlassControlChip(
                                        icon = if (state.isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                        tint = if (state.isMuted) errorColor else textPrimary,
                                        bgAlpha = if (state.isMuted) 0.30f else 0.12f,
                                        size = 32.dp
                                    ) {
                                        viewModel.toggleMute()
                                        ExoPlayerManager.setVolume(if (!state.isMuted) 0f else state.volume)
                                        showVolumeIndicator = true
                                    }
                                    GlassControlChip(
                                        icon = Icons.Default.BrightnessHigh,
                                        tint = amberDot,
                                        size = 32.dp
                                    ) { showBrightnessIndicator = true }
                                    GlassControlChip(
                                        icon = Icons.Default.Lock,
                                        tint = textPrimary,
                                        size = 32.dp
                                    ) { viewModel.toggleScreenLock(); isControlsVisible = false }
                                    GlassControlChip(
                                        icon = Icons.Outlined.AspectRatio,
                                        tint = textPrimary,
                                        size = 32.dp
                                    ) { viewModel.cycleAspectRatio() }
                                    GlassControlChip(
                                        icon = if (showSuggestions) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Outlined.PlaylistPlay,
                                        tint = if (showSuggestions) primaryColor else textPrimary,
                                        bgAlpha = if (showSuggestions) 0.28f else 0.12f,
                                        size = 32.dp
                                    ) { showSuggestions = !showSuggestions }
                                    GlassControlChip(
                                        icon = Icons.Default.VisibilityOff,
                                        tint = textPrimary,
                                        size = 32.dp
                                    ) { showBottomControls = false }  // Hide button
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (playlist.isNotEmpty() && currentIndex >= 0) {
                                        Row(
                                            modifier = Modifier
                                                .glassPill()
                                                .padding(horizontal = 10.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.AutoMirrored.Outlined.PlaylistPlay,
                                                null,
                                                tint = textPrimary,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(Modifier.width(3.dp))
                                            Text(
                                                "${currentIndex + 1}/${playlist.size}",
                                                color = textPrimary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Show controls button (appears when bottom row is hidden)
                        AnimatedVisibility(
                            visible = !showBottomControls,
                            enter = slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(250, easing = FastOutSlowInEasing)
                            ) + fadeIn(tween(150)),
                            exit = slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = tween(200, easing = FastOutSlowInEasing)
                            ) + fadeOut(tween(100)),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 16.dp, bottom = 8.dp)
                                    .size(44.dp)
                                    .glass(cornerRadius = 22.dp, bgAlpha = 0.65f)
                                    .clickable { showBottomControls = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Visibility,
                                    "Show controls",
                                    tint = textPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(if (isLandscape) 6.dp else 10.dp))

                        YouTubeStyleBottomBar(
                            currentPosition = if (isSeeking) seekPosition else state.currentPosition,
                            duration = state.duration,
                            onSeek = { position ->
                                isSeeking = true
                                seekPosition = position
                                ExoPlayerManager.seekTo(position)
                                viewModel.updatePlaybackState(currentPosition = position)
                                scope.launch { delay(100); isSeeking = false }
                            },
                            onFullscreenToggle = { viewModel.toggleFullScreen() },
                            isFullscreen = state.isFullScreen,
                            modifier = Modifier.fillMaxWidth(),
                            progressColor = redAccent,
                            backgroundColor = Color.White.copy(0.20f),
                            height = 3.dp,
                            videoUri = state.currentVideo?.uri ?: video.uri,
                            context = context,
                            viewModel = viewModel,
                            isLandscape = isLandscape,
                            bookmarks = currentBookmarks,
                            onRemoveBookmark = { id -> viewModel.removeBookmark(id) }
                        )
                    }
                }
            }

            // Suggestions row
            if (showSuggestions && playlist.isNotEmpty() && !state.isScreenLocked && !isTransitioning) {
                AnimatedVisibility(
                    visible = showSuggestions,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(tween(200)),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(tween(150)),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .glass(cornerRadius = 14.dp, bgAlpha = 0.55f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Outlined.PlaylistPlay,
                                        null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        "Up Next",
                                        color = textPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        "${playlist.size} videos",
                                        color = textSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .glass(cornerRadius = 16.dp, bgAlpha = 0.55f)
                                    .clickable { showSuggestions = false }
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    "Close",
                                    tint = textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(playlist, key = { it.id }) { suggestionVideo ->
                                EnhancedSuggestionItem(
                                    video = suggestionVideo,
                                    isCurrentVideo = suggestionVideo.id == state.currentVideo?.id,
                                    onPlay = {
                                        if (!isTransitioning && suggestionVideo.id != state.currentVideo?.id) {
                                            val newIndex =
                                                playlist.indexOfFirst { it.id == suggestionVideo.id }
                                            if (newIndex == -1) return@EnhancedSuggestionItem
                                            isTransitioning = true
                                            ExoPlayerManager.pause()
                                            ExoPlayerManager.resetForNewVideo()
                                            scope.launch {
                                                delay(150)
                                                viewModel.setCurrentVideo(suggestionVideo, newIndex)
                                                viewModel.clearBookmarks()
                                                ExoPlayerManager.prepareVideo(
                                                    uri = suggestionVideo.uri.toString(),
                                                    startPosition = 0L,
                                                    shouldPlay = true
                                                )
                                                viewModel.updatePlaybackState(isPlaying = true)
                                                if (!isPlaying) onPlayPause()
                                                showSuggestions = false
                                                isTransitioning = false
                                            }
                                        }
                                    }
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }

            // Overlay panels
            AnimatedVisibility(
                visible = showVideoAdjustments && !state.isScreenLocked,
                enter = GlassAnimations.PanelEnter,
                exit = GlassAnimations.PanelExit,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                DynamicVideoAdjustmentsPanel(viewModel, state) { showVideoAdjustments = false }
            }
            AnimatedVisibility(
                visible = showAdvancedMenu && !state.isScreenLocked,
                enter = GlassAnimations.PanelEnter,
                exit = GlassAnimations.PanelExit,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                DynamicAdvancedControlsPanel(viewModel, state) { showAdvancedMenu = false }
            }
            AnimatedVisibility(
                visible = showEqualizerPanel && !state.isScreenLocked,
                enter = GlassAnimations.PanelEnter,
                exit = GlassAnimations.PanelExit,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                DynamicEqualizerPanel(
                    viewModel = viewModel,
                    state = state,
                    errorColor = errorColor
                ) { showEqualizerPanel = false }
            }
            AnimatedVisibility(
                visible = showVideoInfo && !state.isScreenLocked,
                enter = GlassAnimations.PanelEnter,
                exit = GlassAnimations.PanelExit,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                VideoInfoPanel(
                    viewModel,
                    state.currentVideo ?: video,
                    state,
                    thumbnailBitmap
                ) { showVideoInfo = false }
            }

            // Unlock button
            AnimatedVisibility(
                visible = state.isScreenLocked,
                enter = fadeIn(tween(250)) + scaleIn(
                    initialScale = 0.6f,
                    animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
                ),
                exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.6f),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                        .size(54.dp)
                        .glass(cornerRadius = 27.dp, bgAlpha = 0.55f, borderTopAlpha = 0.35f)
                        .clickable { viewModel.toggleScreenLock(); isControlsVisible = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.LockOpen,
                        "Unlock",
                        tint = textPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            if (state.isScreenLocked) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(onDoubleTap = {
                                viewModel.toggleScreenLock(); isControlsVisible = true
                            })
                        })
            }
        }
    }
}



@Composable
fun DynamicSubtitleMenu(
    tracks: List<SubtitleTrack>,
    selectedTrack: SubtitleTrack?,
    onTrackSelected: (SubtitleTrack) -> Unit,
    onDisable: () -> Unit,
    onDismiss: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val textPrimary = GlassTokens.getTextPrimary()
    val chipBg = GlassTokens.getChipBg()
    val chipBorder = GlassTokens.getChipBorder()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .glassPanel()
            .padding(18.dp)
    ) {
        GlassPanelHeader("Subtitles", onDismiss)
        Spacer(Modifier.height(10.dp))

        // Disable option
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (selectedTrack == null) cs.primary.copy(0.20f) else chipBg.copy(0.30f))
                .border(
                    1.dp,
                    if (selectedTrack == null) cs.primary.copy(0.40f) else chipBorder,
                    RoundedCornerShape(10.dp)
                )
                .clickable { onDisable() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Off",
                color = if (selectedTrack == null) cs.primary else textPrimary,
                fontSize = 14.sp,
                fontWeight = if (selectedTrack == null) FontWeight.Bold else FontWeight.Normal
            )
            if (selectedTrack == null) {
                Icon(
                    Icons.Default.Check,
                    null,
                    tint = cs.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        if (tracks.isEmpty()) {
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No subtitle tracks available",
                    color = textPrimary.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            }
        } else {
            Spacer(Modifier.height(6.dp))
            tracks.forEach { track ->
                val isSelected = selectedTrack?.id == track.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) cs.primary.copy(0.20f) else chipBg.copy(0.30f))
                        .border(
                            1.dp,
                            if (isSelected) cs.primary.copy(0.40f) else chipBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onTrackSelected(track) }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            track.label,
                            color = if (isSelected) cs.primary else textPrimary,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (track.language.isNotEmpty() && track.language != "und") {
                            Text(
                                track.language.uppercase(),
                                color = textPrimary.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            )
                        }
                    }
                    if (isSelected) {
                        Icon(
                            Icons.Default.Check,
                            null,
                            tint = cs.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}


@Composable
private fun GlassVerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    color: Color,
    label: String,
    isVisible: Boolean
) {
    val density = LocalDensity.current
    val textPrimary = GlassTokens.getTextPrimary()

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(200)) + scaleIn(initialScale = 0.8f),
        exit = fadeOut(tween(150)) + scaleOut(targetScale = 0.8f)
    ) {
        Box(
            modifier = modifier
                .size(60.dp, 180.dp)
                .glass(cornerRadius = 24.dp, bgAlpha = 0.85f)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    if (label == "Volume") {
                        if (value == 0f) Icons.AutoMirrored.Filled.VolumeOff
                        else Icons.AutoMirrored.Filled.VolumeUp
                    } else Icons.Default.BrightnessHigh,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .width(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(value)
                            .align(Alignment.BottomCenter)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(color, color.copy(alpha = 0.7f))
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .offset(
                                x = 6.dp,
                                y = with(density) { ((1f - value) * 100.dp.toPx() - 10.dp.toPx()).toInt().dp }
                            )
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, color, CircleShape)
                            .shadow(4.dp, CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDrag = { _, dragAmount ->
                                        val delta = -dragAmount.y / size.height
                                        val newValue = (value + delta).coerceIn(0f, 1f)
                                        onValueChange(newValue)
                                    }
                                )
                            }
                    )
                }
                Text(
                    "${(value * 100).toInt()}%",
                    color = textPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun GlassProgressBar(progress: Float, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .width(72.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color.White.copy(0.15f))
    ) {
        Box(
            Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .background(Brush.horizontalGradient(listOf(color.copy(0.7f), color)))
        )
    }
}

@Composable
private fun GlassPlayerButton(
    icon: ImageVector,
    enabled: Boolean,
    size: Dp,
    tint: Color,
    label: String? = null,
    onClick: () -> Unit
) {
    val chipBg = GlassTokens.getChipBg()
    val chipBorder = GlassTokens.getChipBorder()
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(chipBg.copy(if (enabled) 0.18f else 0.06f))
            .border(1.dp, chipBorder.copy(if (enabled) 1f else 0.4f), CircleShape)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(size * 0.55f))
        if (label != null) {
            Text(
                label,
                color = tint,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 3.dp)
            )
        }
    }
}

@Composable
fun EnhancedSuggestionItem(video: VideoFile, isCurrentVideo: Boolean, onPlay: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()

    Box(
        modifier = Modifier
            .width(160.dp)
            .height(78.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isCurrentVideo) cs.primary.copy(0.22f) else Color(0xFF1A1A1A).copy(0.85f))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        if (isCurrentVideo) cs.primary.copy(0.50f) else Color.White.copy(0.12f),
                        Color.White.copy(0.04f)
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(enabled = !isCurrentVideo) { onPlay() }
    ) {
        if (video.thumbnailUri != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(video.thumbnailUri)
                    .crossfade(true).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(cs.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.VideoLibrary, null,
                    tint = Color.White.copy(0.25f),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(0.75f)
                        )
                    )
                )
        )
        Column(
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                video.name.removeSuffix(".mp4").removeSuffix(".mkv").removeSuffix(".avi")
                    .removeSuffix(".mov").removeSuffix(".webm").removeSuffix(".flv"),
                fontSize = 11.sp,
                fontWeight = if (isCurrentVideo) FontWeight.Bold else FontWeight.Medium,
                color = textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Timer,
                    null,
                    tint = textSecondary,
                    modifier = Modifier.size(10.dp)
                )
                Text(formatDuration(video.duration), fontSize = 9.sp, color = textSecondary)
            }
        }
        if (isCurrentVideo) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .glassPill()
                    .padding(horizontal = 7.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    null,
                    tint = cs.primary,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    "NOW PLAYING", fontSize = 8.sp, fontWeight = FontWeight.Bold,
                    color = cs.primary, letterSpacing = 0.5.sp
                )
            }
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(0.90f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        "Play",
                        tint = cs.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun YouTubeStyleBottomBar(
    currentPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    onFullscreenToggle: () -> Unit,
    isFullscreen: Boolean,
    modifier: Modifier = Modifier,
    progressColor: Color,
    backgroundColor: Color = Color.White.copy(0.20f),
    height: Dp = 3.dp,
    videoUri: Uri? = null,
    context: Context = LocalContext.current,
    viewModel: ExoPlayerViewModel,
    isLandscape: Boolean = false,
    bookmarks: List<Bookmark> = emptyList(),
    onRemoveBookmark: (Long) -> Unit = {}
) {
    val density = LocalDensity.current
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val chipBg = GlassTokens.getChipBg()
    val chipBorder = GlassTokens.getChipBorder()
    val amberDot = GlassTokens.AmberDot
    val isDark = GlassTokens.isDarkTheme()

    var barWidth by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableLongStateOf(0L) }
    var dragX by remember { mutableFloatStateOf(0f) }
    var showPreview by remember { mutableStateOf(false) }
    var previewPosition by remember { mutableLongStateOf(0L) }
    var previewX by remember { mutableFloatStateOf(0f) }
    var currentThumbnail by remember { mutableStateOf<Bitmap?>(null) }
    var isLoadingThumbnail by remember { mutableStateOf(false) }
    val thumbnailCache = remember { mutableMapOf<Long, Bitmap>() }

    val progress = if (duration > 0) (currentPosition.toFloat() / duration).coerceIn(0f, 1f) else 0f
    val currentProgress =
        if (isDragging && duration > 0) (dragPosition.toFloat() / duration).coerceIn(
            0f,
            1f
        ) else progress
    val animatedProgress by animateFloatAsState(
        targetValue = currentProgress,
        animationSpec = tween(if (isDragging) 0 else 180),
        label = "progress"
    )
    val thumbSize by animateFloatAsState(
        targetValue = if (isDragging) 14f else 10f,
        animationSpec = spring(),
        label = "thumbSize"
    )

    LaunchedEffect(previewPosition, isDragging, videoUri) {
        if (!isDragging || videoUri == null || duration <= 0) {
            currentThumbnail = null
            return@LaunchedEffect
        }
        if (isLoadingThumbnail) return@LaunchedEffect
        val cached = thumbnailCache[previewPosition]
        if (cached != null && !cached.isRecycled) {
            currentThumbnail = cached
            return@LaunchedEffect
        }
        isLoadingThumbnail = true
        try {
            delay(65)
            var frame = viewModel.getVideoFrameAtPosition(context, videoUri, previewPosition, true)
            if (frame == null) {
                delay(40)
                frame = viewModel.getVideoFrameAtPosition(
                    context, videoUri,
                    (previewPosition + 150L).coerceAtMost(duration), true
                )
            }
            if (frame != null && !frame.isRecycled) {
                val scaled = frame.scale(100, 74)
                currentThumbnail = scaled
                thumbnailCache[previewPosition] = scaled
            } else {
                currentThumbnail = null
            }
        } catch (e: Exception) {
            print(e)
            currentThumbnail = null
        } finally {
            isLoadingThumbnail = false
        }
    }

    DisposableEffect(videoUri) {
        onDispose {
            thumbnailCache.values.forEach { if (!it.isRecycled) it.recycle() }
            thumbnailCache.clear()
            currentThumbnail = null
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isLandscape) 32.dp else 40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                formatDuration(if (isDragging) dragPosition else currentPosition),
                color = textPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier.width(42.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(if (isLandscape) 28.dp else 34.dp)
                    .onGloballyPositioned { barWidth = it.size.width.toFloat() }
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            if (barWidth > 0f && duration > 0) onSeek(
                                (tapOffset.x / barWidth * duration).toLong().coerceIn(0L, duration)
                            )
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { off ->
                                isDragging = true; showPreview = true
                                if (barWidth > 0f) {
                                    dragX = off.x.coerceIn(0f, barWidth)
                                    dragPosition = (dragX / barWidth * duration).toLong()
                                        .coerceIn(0L, duration)
                                    previewPosition = dragPosition; previewX = dragX
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                if (barWidth > 0f) {
                                    dragX = (dragX + dragAmount.x).coerceIn(0f, barWidth)
                                    dragPosition = (dragX / barWidth * duration).toLong()
                                        .coerceIn(0L, duration)
                                    previewPosition = dragPosition; previewX = dragX
                                }
                            },
                            onDragEnd = {
                                if (duration > 0) onSeek(dragPosition)
                                isDragging = false; showPreview = false; currentThumbnail = null
                            },
                            onDragCancel = {
                                isDragging = false; showPreview = false; currentThumbnail = null
                            }
                        )
                    }
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(height)
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(height / 2))
                        .background(backgroundColor)
                )
                Box(
                    Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(height)
                        .align(Alignment.CenterStart)
                        .clip(RoundedCornerShape(height / 2))
                        .background(progressColor)
                )
                if (barWidth > 0f) {
                    val thumbPx = with(density) { thumbSize.dp.toPx() }
                    val thumbPos =
                        (animatedProgress * barWidth).coerceIn(thumbPx / 2, barWidth - thumbPx / 2)
                    Box(
                        modifier = Modifier
                            .size(thumbSize.dp)
                            .offset { IntOffset((thumbPos - thumbPx / 2).toInt(), 0) }
                            .align(Alignment.CenterStart)
                            .clip(CircleShape)
                            .background(Color.White)
                            .shadow(4.dp, CircleShape)
                            .border(1.5.dp, progressColor, CircleShape)
                    )
                }
                if (barWidth > 0f && duration > 0 && bookmarks.isNotEmpty()) {
                    bookmarks.forEach { bookmark ->
                        val fraction = (bookmark.positionMs.toFloat() / duration).coerceIn(0f, 1f)
                        val dotX = fraction * barWidth
                        val dotPx = with(density) { 10.dp.toPx() }
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .offset { IntOffset((dotX - dotPx / 2).toInt(), 0) }
                                .align(Alignment.CenterStart)
                                .clip(CircleShape)
                                .background(amberDot)
                                .border(1.dp, Color.Black.copy(0.35f), CircleShape)
                                .pointerInput(bookmark.id) {
                                    detectTapGestures(
                                        onTap = { onSeek(bookmark.positionMs) },
                                        onLongPress = { onRemoveBookmark(bookmark.id) }
                                    )
                                }
                        )
                    }
                }
            }

            Text(
                formatDuration(duration),
                color = textSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier.width(42.dp)
            )
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(chipBg)
                    .border(1.dp, chipBorder, CircleShape)
                    .clickable { onFullscreenToggle() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                    null, tint = textPrimary, modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    // Seek preview popup
    if (showPreview && duration > 0 && videoUri != null) {
        val previewWidth = 132.dp
        val previewHeight = 74.dp
        val previewY = with(density) { (if (isLandscape) 54.dp else 64.dp).toPx() }.toInt()
        val previewBgColor =
            if (isDark) Color(0xFF0D1117).copy(0.90f) else Color(0xFFF5F5F5).copy(0.90f)
        val previewBorderStart = if (isDark) Color.White.copy(0.22f) else Color.Black.copy(0.22f)
        val previewBorderEnd = if (isDark) Color.White.copy(0.05f) else Color.Black.copy(0.05f)
        val caretColor =
            if (isDark) Color(0xFF0D1117).copy(0.90f) else Color(0xFFF5F5F5).copy(0.90f)
        val gradientStart = if (isDark) Color(0xFF1A2235) else Color(0xFFE8EAEF)
        val gradientEnd = if (isDark) Color(0xFF0D1117) else Color(0xFFF5F5F5)

        Popup(
            alignment = Alignment.BottomCenter,
            offset = IntOffset(
                (previewX - with(density) { previewWidth.toPx() / 2 }).toInt(),
                y = -previewY
            )
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(100)) + scaleIn(initialScale = 0.95f),
                exit = fadeOut(tween(50)) + scaleOut(targetScale = 0.95f)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .width(previewWidth)
                            .height(previewHeight)
                            .clip(RoundedCornerShape(10.dp))
                            .background(previewBgColor)
                            .border(
                                1.dp,
                                Brush.linearGradient(listOf(previewBorderStart, previewBorderEnd)),
                                RoundedCornerShape(10.dp)
                            )
                            .shadow(8.dp, RoundedCornerShape(10.dp))
                    ) {
                        when {
                            currentThumbnail != null -> Image(
                                bitmap = currentThumbnail!!.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            isLoadingThumbnail -> Box(
                                Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = progressColor, strokeWidth = 2.5.dp
                                )
                            }

                            else -> Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                gradientStart,
                                                gradientEnd
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow, null,
                                    tint = progressColor.copy(0.55f),
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(6.dp)
                                .glassPill()
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                formatDuration(previewPosition),
                                color = textPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Canvas(
                        modifier = Modifier
                            .size(12.dp)
                            .offset(y = (-2).dp)
                    ) {
                        val path = Path().apply {
                            moveTo(size.width / 2, 0f)
                            lineTo(size.width, size.height)
                            lineTo(0f, size.height)
                            close()
                        }
                        drawPath(path, caretColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassControlChip(
    icon: ImageVector,
    tint: Color,
    bgAlpha: Float = 0.12f,
    size: Dp = 32.dp,
    onClick: () -> Unit
) {
    val chipBg = GlassTokens.getChipBg()
    val chipBorder = GlassTokens.getChipBorder()
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(chipBg.copy(bgAlpha))
            .border(1.dp, chipBorder, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(size * 0.55f))
    }
}

@Composable
fun DynamicEqualizerPanel(
    viewModel: ExoPlayerViewModel,
    state: ExoPlayerState,
    errorColor: Color,
    onClose: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val chipBg = GlassTokens.getChipBg()
    val chipBorder = GlassTokens.getChipBorder()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .glassPanel()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        GlassPanelHeader("Equalizer", onClose)
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text(
                "Enable Equalizer",
                color = textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Switch(
                checked = state.equalizerEnabled,
                onCheckedChange = { viewModel.toggleEqualizer() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = cs.primary, checkedTrackColor = cs.primary.copy(0.5f),
                    uncheckedTrackColor = chipBg, uncheckedBorderColor = chipBorder
                )
            )
        }
        if (state.equalizerEnabled) {
            HorizontalDivider(color = chipBorder)
            Text("Preset", color = textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(EqualizerPreset.entries) { preset ->
                    FilterChip(
                        selected = state.equalizerPreset == preset,
                        onClick = { viewModel.setEqualizerPreset(preset) },
                        label = { Text(preset.displayName, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = cs.primary, selectedLabelColor = cs.onPrimary,
                            containerColor = chipBg, labelColor = textPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true, selected = state.equalizerPreset == preset,
                            borderColor = chipBorder, selectedBorderColor = cs.primary
                        )
                    )
                }
            }
            if (state.equalizerPreset == EqualizerPreset.CUSTOM) {
                Text(
                    "Custom EQ",
                    color = textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                val frequencies = listOf(
                    "31Hz",
                    "62Hz",
                    "125Hz",
                    "250Hz",
                    "500Hz",
                    "1kHz",
                    "2kHz",
                    "4kHz",
                    "8kHz",
                    "16kHz"
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(frequencies.size) { index ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(frequencies[index], fontSize = 9.sp, color = textSecondary)
                            Slider(
                                value = state.customEqBands.getOrNull(index) ?: 0f,
                                onValueChange = { viewModel.setCustomEqBand(index, it) },
                                valueRange = -1f..1f,
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(120.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = cs.primary,
                                    activeTrackColor = cs.primary,
                                    inactiveTrackColor = chipBg
                                )
                            )
                            Text(
                                String.format(
                                    Locale.US,
                                    "%.1f",
                                    state.customEqBands.getOrNull(index) ?: 0f
                                ),
                                fontSize = 9.sp, color = cs.primary
                            )
                        }
                    }
                }
            }
            Button(
                onClick = { viewModel.resetEqualizer() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = errorColor.copy(0.15f),
                    contentColor = errorColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Reset Equalizer", fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun DynamicAudioMenu(
    selectedTrack: String,
    onTrackSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val textPrimary = GlassTokens.getTextPrimary()
    val chipBg = GlassTokens.getChipBg()
    val chipBorder = GlassTokens.getChipBorder()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .glassPanel()
            .padding(18.dp)
    ) {
        GlassPanelHeader("Audio Track", onDismiss)
        Spacer(Modifier.height(10.dp))
        listOf("Original", "English", "Spanish", "French", "German").forEach { track ->
            val selected = selectedTrack == track
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) cs.primary.copy(0.20f) else chipBg.copy(0.30f))
                    .border(
                        1.dp,
                        if (selected) cs.primary.copy(0.40f) else chipBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onTrackSelected(track) }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    track, color = if (selected) cs.primary else textPrimary, fontSize = 14.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
                if (selected) Icon(
                    Icons.Default.Check,
                    null,
                    tint = cs.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
fun DynamicSpeedMenu(playbackSpeed: Float, onSpeedChange: (Float) -> Unit, onDismiss: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val textPrimary = GlassTokens.getTextPrimary()
    val chipBg = GlassTokens.getChipBg()
    val chipBorder = GlassTokens.getChipBorder()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .glassPanel()
            .padding(18.dp)
    ) {
        GlassPanelHeader("Playback Speed", onDismiss)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("Pitch Shift", color = textPrimary, fontSize = 14.sp)
            Switch(
                checked = false, onCheckedChange = {},
                colors = SwitchDefaults.colors(
                    checkedThumbColor = cs.primary, checkedTrackColor = cs.primary.copy(0.5f),
                    uncheckedTrackColor = chipBg, uncheckedBorderColor = chipBorder
                )
            )
        }
        Spacer(Modifier.height(8.dp))
        listOf(0.25f, 0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f).forEach { speed ->
            val selected = playbackSpeed == speed
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) cs.primary.copy(0.20f) else chipBg.copy(0.30f))
                    .border(
                        1.dp,
                        if (selected) cs.primary.copy(0.40f) else chipBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSpeedChange(speed) }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${speed}x",
                    color = if (selected) cs.primary else textPrimary,
                    fontSize = 14.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
                if (selected) Icon(
                    Icons.Default.Check,
                    null,
                    tint = cs.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
fun DynamicSleepTimerMenu(viewModel: ExoPlayerViewModel, onClose: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val textPrimary = GlassTokens.getTextPrimary()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .glassPanel()
            .padding(18.dp)
    ) {
        GlassPanelHeader("Sleep Timer", onClose)
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf(15, 30, 45, 60, 90, 120)) { min ->
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(40.dp)
                        .glass(bgAlpha = 0.45f)
                        .clickable { viewModel.setSleepTimer(min); onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${min}m",
                        color = textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(cs.error.copy(0.20f))
                        .border(1.dp, cs.error.copy(0.35f), RoundedCornerShape(20.dp))
                        .clickable { viewModel.cancelSleepTimer(); onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Off", color = cs.error, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun DynamicVideoAdjustmentsPanel(
    viewModel: ExoPlayerViewModel,
    state: ExoPlayerState,
    onClose: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val textPrimary = GlassTokens.getTextPrimary()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .glassPanel()
            .padding(20.dp)
    ) {
        GlassPanelHeader("Video Adjustments", onClose)
        Spacer(Modifier.height(14.dp))
        GlassAdjustmentSlider(
            "Brightness",
            state.videoAdjustment.brightness,
            { viewModel.setVideoBrightness(it) },
            0f..2f
        )
        GlassAdjustmentSlider(
            "Contrast",
            state.videoAdjustment.contrast,
            { viewModel.setContrast(it) },
            0f..2f
        )
        GlassAdjustmentSlider(
            "Saturation",
            state.videoAdjustment.saturation,
            { viewModel.setSaturation(it) },
            0f..2f
        )
        GlassAdjustmentSlider(
            "Sharpness",
            state.videoAdjustment.sharpness,
            { viewModel.setSharpness(it) },
            0f..2f
        )
        GlassAdjustmentSlider(
            "Hue",
            state.videoAdjustment.hue,
            { viewModel.setHue(it) },
            -180f..180f
        )
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glass(bgAlpha = 0.30f)
                .clickable { viewModel.resetVideoAdjustments() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    null,
                    tint = cs.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    "Reset to Default",
                    color = cs.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun GlassAdjustmentSlider(
    label: String, value: Float, onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>
) {
    val cs = MaterialTheme.colorScheme
    val textPrimary = GlassTokens.getTextPrimary()
    val chipBg = GlassTokens.getChipBg()

    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text(label, color = textPrimary, fontSize = 14.sp)
            Text(
                String.format(Locale.US, "%.1f", value),
                color = cs.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value, onValueChange, valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = cs.primary,
                activeTrackColor = cs.primary,
                inactiveTrackColor = chipBg
            ),
            modifier = Modifier.height(32.dp)
        )
    }
}

@Composable
fun DynamicAdvancedControlsPanel(
    viewModel: ExoPlayerViewModel,
    state: ExoPlayerState,
    onClose: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val textPrimary = GlassTokens.getTextPrimary()
    val chipBg = GlassTokens.getChipBg()
    val chipBorder = GlassTokens.getChipBorder()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .glassPanel()
            .padding(20.dp)
    ) {
        GlassPanelHeader("Advanced Controls", onClose)
        Spacer(Modifier.height(14.dp))
        Text("Zoom Presets", color = textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ZoomPreset.entries) { preset ->
                FilterChip(
                    selected = state.zoomPreset == preset,
                    onClick = { viewModel.setZoomPreset(preset) },
                    label = { Text(preset.name.replace("_", " "), fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = cs.primary, selectedLabelColor = cs.onPrimary,
                        containerColor = chipBg, labelColor = textPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true, selected = state.zoomPreset == preset,
                        borderColor = chipBorder, selectedBorderColor = cs.primary
                    )
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                GlassAdvancedItem(
                    Icons.AutoMirrored.Filled.RotateRight,
                    "Rotate CW"
                ) { viewModel.rotateClockwise() }
            }
            item {
                GlassAdvancedItem(
                    Icons.AutoMirrored.Filled.RotateRight,
                    "Rotate CCW"
                ) { viewModel.rotateCounterClockwise() }
            }
            item {
                GlassAdvancedItem(
                    Icons.Default.Flip,
                    "Flip H",
                    isActive = state.flipHorizontal
                ) { viewModel.flipHorizontal() }
            }
            item {
                GlassAdvancedItem(
                    Icons.Default.Flip,
                    "Flip V",
                    isActive = state.flipVertical
                ) { viewModel.flipVertical() }
            }
            item {
                GlassAdvancedItem(
                    Icons.Default.Crop,
                    "Crop",
                    isActive = state.cropMode != CropMode.NONE
                ) { viewModel.cycleCropMode() }
            }
            item {
                GlassAdvancedItem(
                    Icons.Default.Tune,
                    "Deinterlace",
                    isActive = state.deinterlaceEnabled
                ) { viewModel.toggleDeinterlace() }
            }
            item {
                GlassAdvancedItem(
                    Icons.Default.AutoAwesome,
                    "Enhance",
                    isActive = state.enhancementMode != EnhancementMode.NONE
                ) { viewModel.cycleEnhancementMode() }
            }
            item {
                GlassAdvancedItem(
                    Icons.Default.HdrOn,
                    "HDR",
                    isActive = state.hdrEnabled
                ) { viewModel.toggleHdr() }
            }
            item {
                GlassAdvancedItem(
                    Icons.Default.Palette,
                    "Color Space"
                ) { viewModel.cycleColorSpace() }
            }
            item {
                GlassAdvancedItem(
                    Icons.Default.Speed,
                    "Frame Rate",
                    isActive = state.frameRateConversionEnabled
                ) { viewModel.toggleFrameRateConversion() }
            }
            item {
                GlassAdvancedItem(
                    Icons.Default.Refresh,
                    "Reset All"
                ) { viewModel.resetAllSettings() }
            }
        }
    }
}

@Composable
private fun GlassAdvancedItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val chipBg = GlassTokens.getChipBg()
    val chipBorder = GlassTokens.getChipBorder()

    Column(Modifier.clickable { onClick() }, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(if (isActive) cs.primary.copy(0.30f) else chipBg)
                .border(1.dp, if (isActive) cs.primary.copy(0.55f) else chipBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                label,
                tint = if (isActive) cs.primary else textPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.height(5.dp))
        Text(label, color = textSecondary, fontSize = 10.sp)
    }
}

@Composable
fun VideoInfoPanel(
    viewModel: ExoPlayerViewModel, video: VideoFile, state: ExoPlayerState,
    thumbnailBitmap: Bitmap? = null, onClose: () -> Unit
) {
    val chipBorder = GlassTokens.getChipBorder()
    val displayThumbnail = remember(thumbnailBitmap) {
        thumbnailBitmap?.let { if (it.width > 160 || it.height > 90) it.scale(160, 90) else it }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .glassPanel()
            .padding(20.dp)
    ) {
        GlassPanelHeader("Video Information", onClose)
        Spacer(Modifier.height(14.dp))
        if (displayThumbnail != null) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(
                    bitmap = displayThumbnail.asImageBitmap(), contentDescription = null,
                    modifier = Modifier
                        .size(160.dp, 90.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, chipBorder, RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.height(14.dp))
        }
        GlassInfoRow("Name", video.name)
        GlassInfoRow("Resolution", "${state.videoWidth}×${state.videoHeight}")
        GlassInfoRow("Duration", formatDuration(video.duration))
        GlassInfoRow("Size", formatFileSize(video.size))
        GlassInfoRow("Folder", video.folderName)
        GlassInfoRow("Path", video.path, isPath = true)
        GlassInfoRow("Format", video.name.substringAfterLast(".").uppercase())
        if (state.videoWidth > 0 && state.videoHeight > 0) {
            GlassInfoRow(
                "Aspect Ratio",
                String.format(Locale.US, "%.2f:1", state.videoWidth.toFloat() / state.videoHeight)
            )
        }
    }
}

@Composable
private fun GlassPanelHeader(title: String, onClose: () -> Unit) {
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val chipBg = GlassTokens.getChipBg()
    val chipBorder = GlassTokens.getChipBorder()

    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(title, color = textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(chipBg)
                .border(1.dp, chipBorder, CircleShape)
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Close, null, tint = textSecondary, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun GlassInfoRow(label: String, value: String, isPath: Boolean = false) {
    val cs = MaterialTheme.colorScheme
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()

    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            label,
            color = cs.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.3.sp
        )
        Text(
            value, color = if (isPath) textSecondary else textPrimary, fontSize = 13.sp,
            maxLines = if (isPath) 2 else 1, overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(4.dp))
    }
}

