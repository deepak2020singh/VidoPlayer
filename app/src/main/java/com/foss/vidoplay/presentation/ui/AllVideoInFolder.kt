package com.foss.vidoplay.presentation.ui

import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foss.vidoplay.data.repos.LastPlayedInfo
import com.foss.vidoplay.domain.model.VideoFile
import com.foss.vidoplay.presentation.common.GlassTokens
import com.foss.vidoplay.presentation.common.glassChip
import com.foss.vidoplay.presentation.viewModel.LastPlayedViewModel
import com.foss.vidoplay.presentation.viewModel.VideoViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllVideosFolderScreen(
    folderPath: String,
    folderName: String,
    onBack: () -> Unit,
    onVideoClick: (VideoFile, Long) -> Unit,
    viewModel: VideoViewModel = koinViewModel(),
    lastPlayedViewModel: LastPlayedViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val textPrimary = GlassTokens.getTextPrimary()

    val folders by viewModel.videoFolders.collectAsState()
    val resumableVideo by lastPlayedViewModel.resumableVideo.collectAsState()

    val folder = folders.firstOrNull { it.path == folderPath }
    val videos = folder?.videos ?: emptyList()

    var videoToRename by remember { mutableStateOf<VideoFile?>(null) }
    var pendingRenameVideo by remember { mutableStateOf<VideoFile?>(null) }
    var pendingNewName by remember { mutableStateOf("") }
    var videoToDelete by remember { mutableStateOf<VideoFile?>(null) }

    // ─── Rename launcher — uses WRITE request, NOT delete ────────────────────
    val renameLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val video = pendingRenameVideo
            val name = pendingNewName
            if (video != null && name.isNotBlank()) {
                viewModel.renameVideo(video, name)
                Toast.makeText(context, "Video renamed successfully", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Rename permission denied", Toast.LENGTH_SHORT).show()
        }
        pendingRenameVideo = null
        pendingNewName = ""
    }

    // ─── Delete launcher — uses DELETE request ────────────────────────────────
    val deleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "Video deleted successfully", Toast.LENGTH_SHORT).show()
            viewModel.refreshVideos()
        } else {
            Toast.makeText(context, "Delete cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    fun performRename(video: VideoFile, newName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // getWritePendingIntent — grants write/rename access, does NOT delete
            val pendingIntent = viewModel.getWritePendingIntent(context, video)
            if (pendingIntent != null && activity != null) {
                pendingRenameVideo = video
                pendingNewName = newName
                try {
                    renameLauncher.launch(
                        IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                    )
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Failed to request rename permission: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.renameVideo(video, newName)
                }
            } else {
                viewModel.renameVideo(video, newName)
            }
        } else {
            viewModel.renameVideo(video, newName)
            Toast.makeText(context, "Video renamed", Toast.LENGTH_SHORT).show()
        }
    }

    fun performDelete(video: VideoFile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // getDeletePendingIntent — grants delete access
            val pendingIntent = viewModel.getDeletePendingIntent(context, video)
            if (pendingIntent != null && activity != null) {
                try {
                    deleteLauncher.launch(
                        IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                    )
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to start delete request", Toast.LENGTH_SHORT)
                        .show()
                    viewModel.deleteVideo(video)
                }
            } else {
                viewModel.deleteVideo(video)
            }
        } else {
            viewModel.deleteVideo(video)
            Toast.makeText(context, "Video deleted", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = folderName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = textPrimary
                    )
                },
                navigationIcon = {
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .glassChip(cornerRadius = 12.dp)
                            .clickable { onBack() },
                        color = Color.Transparent
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        GlassVideoListContent(
            lastPlayedViewModel = lastPlayedViewModel,
            videos = videos,
            viewModel = viewModel,
            resumableVideo = resumableVideo,
            onVideoClick = onVideoClick,
            onRename = { videoToRename = it },
            onDelete = { videoToDelete = it },
            modifier = Modifier.padding(padding)
        )
    }

    // ─── Rename Dialog ────────────────────────────────────────────────────────
    videoToRename?.let { video ->
        RenameVideoDialog(
            video = video,
            onDismiss = { videoToRename = null },
            onConfirm = { newName ->
                videoToRename = null
                performRename(video, newName)
            }
        )
    }

    // ─── Delete Confirmation Dialog ───────────────────────────────────────────
    videoToDelete?.let { video ->
        AlertDialog(
            onDismissRequest = { videoToDelete = null },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = { Text("Delete Video", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to delete \"${video.name}\"?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        videoToDelete = null
                        performDelete(video)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { videoToDelete = null }) { Text("Cancel") }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun GlassVideoListContent(
    lastPlayedViewModel: LastPlayedViewModel,
    videos: List<VideoFile>,
    viewModel: VideoViewModel,
    resumableVideo: LastPlayedInfo?,
    onVideoClick: (VideoFile, Long) -> Unit,
    onRename: (VideoFile) -> Unit,
    onDelete: (VideoFile) -> Unit,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues()
) {
    var refreshTrigger by remember { mutableIntStateOf(0) }
    val currentVideos = remember(videos, refreshTrigger) { videos }

    if (currentVideos.isEmpty()) {
        GlassEmptyState("No videos in this folder", Icons.Default.VideoLibrary)
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 6.dp,
                bottom = 6.dp + innerPadding.calculateBottomPadding() + 80.dp
            )
        ) {
            items(currentVideos, key = { it.id }) { video ->
                val savedPosition = if (resumableVideo != null &&
                    resumableVideo.videoId == video.id &&
                    resumableVideo.position > 0 &&
                    resumableVideo.position < video.duration
                ) resumableVideo.position else 0L

                VideoItem(
                    video = video,
                    viewModel = viewModel,
                    lastPlayedViewModel = lastPlayedViewModel,
                    onVideoUpdated = { refreshTrigger++ },
                    onClick = { onVideoClick(video, savedPosition) },
                    onRename = onRename,
                    onDelete = onDelete
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameVideoDialog(
    video: VideoFile,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val primaryColor = MaterialTheme.colorScheme.primary
    val isDark = GlassTokens.isDarkTheme()

    val baseName = remember(video.name) { video.name.substringBeforeLast('.', video.name) }
    var newName by remember { mutableStateOf(baseName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Edit,
                null,
                tint = primaryColor,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text("Rename Video", fontWeight = FontWeight.Bold, color = textPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("New name", color = textSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = textPrimary,
                        unfocusedTextColor = textPrimary,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = primaryColor,
                        unfocusedIndicatorColor = GlassTokens.getChipBorder(),
                        cursorColor = primaryColor
                    )
                )
                Text(
                    text = "Extension: .${video.name.substringAfterLast('.', "mp4")}",
                    fontSize = 12.sp,
                    color = textSecondary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (newName.isNotBlank()) onConfirm(newName.trim()) },
                enabled = newName.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text("Rename", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = textSecondary)
            }
        },
        containerColor = if (isDark) Color(0xFF1E2024) else Color(0xFFF8F9FA),
        shape = RoundedCornerShape(20.dp)
    )
}
