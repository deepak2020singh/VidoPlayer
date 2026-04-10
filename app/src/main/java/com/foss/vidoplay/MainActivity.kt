package com.foss.vidoplay

import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation3.runtime.NavKey
import com.foss.vidoplay.presentation.common.ExoPlayerManager
import com.foss.vidoplay.presentation.ui.AuthScreen
import com.foss.vidoplay.presentation.viewModel.ThemeViewModel
import com.foss.vidoplay.ui.theme.VidoPlayTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    private var _isInPipMode = false

    // PiP control callbacks
    var onPipPlayPause: (() -> Unit)? = null
    var onPipPrevious: (() -> Unit)? = null
    var onPipNext: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeViewModel: ThemeViewModel = koinViewModel()
            val themePrefs by themeViewModel.themePreferences.collectAsState()

            VidoPlayTheme(themePreferences = themePrefs) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AuthScreen(innerPadding = innerPadding)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ExoPlayerManager.release()
    }

    @SuppressLint("NewApi")
    fun updatePipParams(
        videoWidth: Int = 0,
        videoHeight: Int = 0,
        isPlaying: Boolean,
        hasNext: Boolean,
        hasPrevious: Boolean
    ) {
        Log.d(TAG, "updatePipParams - isPlaying: $isPlaying, hasNext: $hasNext, hasPrevious: $hasPrevious")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val params = buildPipParams(videoWidth, videoHeight, isPlaying, hasNext, hasPrevious)
            setPictureInPictureParams(params)

            // If already in PiP mode, update immediately
            if (_isInPipMode) {
                setPictureInPictureParams(params)
            }
        }
    }

    fun enterPipIfPlaying() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "Entering PiP mode")
            enterPictureInPictureMode(
                buildPipParams(isPlaying = true, hasNext = false, hasPrevious = false)
            )
        }
    }

    private fun buildPipParams(
        videoWidth: Int = 0,
        videoHeight: Int = 0,
        isPlaying: Boolean = false,
        hasNext: Boolean = false,
        hasPrevious: Boolean = false
    ): PictureInPictureParams {
        // Calculate aspect ratio
        val aspectRatio = if (videoWidth > 0 && videoHeight > 0) {
            val ratio = videoWidth.toFloat() / videoHeight.toFloat()
            when {
                ratio < 1f / 2.39f -> Rational(1, 239)
                ratio > 2.39f -> Rational(239, 100)
                else -> Rational(videoWidth, videoHeight)
            }
        } else {
            Rational(16, 9) // Default aspect ratio
        }

        val builder = PictureInPictureParams.Builder()
            .setAspectRatio(aspectRatio)

        // Add Android 12+ features
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setAutoEnterEnabled(true)
            builder.setSeamlessResizeEnabled(true)
        }

        // Add Android 13+ title
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            builder.setTitle("VidoPlay")
        }

        return builder.build()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        Log.d(TAG, "onUserLeaveHint called")
        // For Android 8-11, manually enter PiP
        if (Build.VERSION.SDK_INT in Build.VERSION_CODES.O until Build.VERSION_CODES.S) {
            enterPipIfPlaying()
        }
        // Android 12+ uses autoEnterEnabled
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        _isInPipMode = isInPictureInPictureMode
        Log.d(TAG, "PiP mode changed: $isInPictureInPictureMode")

        // When entering PiP, ensure params are updated
        if (isInPictureInPictureMode && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            updatePipParams(
                isPlaying = false,
                hasNext = false,
                hasPrevious = false
            )
        }
    }
}

@Serializable
sealed class MainRoutes : NavKey {
    @Serializable
    data object File : MainRoutes()

    @Serializable
    data object Search : MainRoutes()

    @Serializable
    data object Playlists : MainRoutes()

    @Serializable
    data object StreamScreen : MainRoutes()

    @Serializable
    data object Settings : MainRoutes()

    @Serializable
    data class Player(val videoId: Long, val folderPath: String, val startPosition: Long = 0L) :
        MainRoutes()
}

data class NavItem(val icon: Int, val title: String)

sealed class AuthRoutes : NavKey {
    @Serializable
    data object Splash : AuthRoutes()

    @Serializable
    data object About : AuthRoutes()

    @Serializable
    data object Main : AuthRoutes()

    @Serializable
    data class AllVideosFolder(val folderPath: String, val folderName: String) : AuthRoutes()
}