package com.foss.vidoplay.presentation.utils


import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import androidx.annotation.OptIn
import androidx.core.graphics.createBitmap
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

object ScreenshotCapture {
    suspend fun capture(view: View): Bitmap? {
        return withContext(Dispatchers.Main) {

            when (view) {
                is PlayerView -> capturePlayerView(view)
                else -> captureRegularView(view)
            }
        }
    }

    private fun captureRegularView(view: View): Bitmap? {
        return try {
            val bitmap = createBitmap(view.width, view.height)
            val canvas = android.graphics.Canvas(bitmap)
            view.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @OptIn(UnstableApi::class)
    private suspend fun capturePlayerView(playerView: PlayerView): Bitmap? {

        val textureView = playerView.videoSurfaceView as? TextureView
        if (textureView != null) {
            return textureView.bitmap
        }

        val surfaceView = playerView.videoSurfaceView as? SurfaceView
        if (surfaceView != null) {
            return captureSurfaceView(surfaceView)
        }

        return null
    }

    private suspend fun captureSurfaceView(surfaceView: SurfaceView): Bitmap? {
        return suspendCancellableCoroutine { continuation ->

            val bitmap = createBitmap(surfaceView.width, surfaceView.height)

            try {
                PixelCopy.request(
                    surfaceView, bitmap, { result ->
                        if (result == PixelCopy.SUCCESS) {
                            continuation.resume(bitmap)
                        } else {
                            bitmap.recycle()
                            continuation.resume(null)
                        }
                    }, Handler(Looper.getMainLooper())
                )
            } catch (e: Exception) {
                e.printStackTrace()
                bitmap.recycle()
                continuation.resume(null)
            }
        }
    }
}