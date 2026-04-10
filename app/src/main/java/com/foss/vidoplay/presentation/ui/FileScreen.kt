package com.foss.vidoplay.presentation.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.automirrored.outlined.QueueMusic
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.foss.vidoplay.R
import com.foss.vidoplay.data.repos.LastPlayedInfo
import com.foss.vidoplay.domain.model.VideoFile
import com.foss.vidoplay.domain.model.VideoFolder
import com.foss.vidoplay.presentation.common.GlassTokens
import com.foss.vidoplay.presentation.common.SortOption
import com.foss.vidoplay.presentation.common.ViewMode
import com.foss.vidoplay.presentation.common.formatDuration
import com.foss.vidoplay.presentation.common.glassCard
import com.foss.vidoplay.presentation.common.glassChip
import com.foss.vidoplay.presentation.viewModel.LastPlayedViewModel
import com.foss.vidoplay.presentation.viewModel.PlaylistViewModel
import com.foss.vidoplay.presentation.viewModel.ThemeViewModel
import com.foss.vidoplay.presentation.viewModel.VideoViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun FileScreen(
    innerPadding: PaddingValues,
    onVideoClick: (VideoFile, List<VideoFile>, Long) -> Unit,
    onFolderClick: (VideoFolder) -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: VideoViewModel = koinViewModel(),
    lastPlayedViewModel: LastPlayedViewModel = koinViewModel()
) {
    val textSecondary = GlassTokens.getTextSecondary()
    val isDark = GlassTokens.isDarkTheme()

    LaunchedEffect(Unit) {
        Log.d("FileScreen", "VideoFolders size: ${viewModel.videoFolders.value.size}")
        Log.d("FileScreen", "AllVideos size: ${viewModel.allVideos.value.size}")
    }

    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showAllVideos by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf(SortOption.DATE_ADDED_DESC) }
    var showSortMenu by remember { mutableStateOf(false) }

    val videoFolders by viewModel.videoFolders.collectAsState()
    val allVideos by viewModel.allVideos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedFolder by viewModel.selectedFolder.collectAsState()
    val error by viewModel.error.collectAsState()

    val resumableVideo by lastPlayedViewModel.resumableVideo.collectAsState()

    val sortedVideos = remember(allVideos, sortOption) {
        when (sortOption) {
            SortOption.NAME_ASC -> allVideos.sortedBy { it.name.lowercase() }
            SortOption.NAME_DESC -> allVideos.sortedByDescending { it.name.lowercase() }
            SortOption.DATE_ADDED_ASC -> allVideos.sortedBy { it.dateAdded }
            SortOption.DATE_ADDED_DESC -> allVideos.sortedByDescending { it.dateAdded }
            SortOption.DURATION_ASC -> allVideos.sortedBy { it.duration }
            SortOption.DURATION_DESC -> allVideos.sortedByDescending { it.duration }
            SortOption.SIZE_ASC -> allVideos.sortedBy { it.size }
            SortOption.SIZE_DESC -> allVideos.sortedByDescending { it.size }
        }
    }

    var hasPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED
            else ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            hasPermission = granted
            showPermissionDialog = false
            if (granted) {
                viewModel.loadVideos()
            }
        }

    LaunchedEffect(hasPermission) {
        if (hasPermission && videoFolders.isEmpty() && !isLoading) {
            viewModel.loadVideos()
        }
    }

    LaunchedEffect(error) {
        if (error != null) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    val handleRefresh = {
        if (!isRefreshing) {
            isRefreshing = true
            viewModel.refreshVideos()
            Toast.makeText(context, "Refreshing videos...", Toast.LENGTH_SHORT).show()
            kotlinx.coroutines.MainScope().launch {
                kotlinx.coroutines.delay(2000)
                isRefreshing = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF0A0A0A) else Color(0xFFF5F5F5))
    ) {
        if (!hasPermission) {
            PermissionRequestScreen { showPermissionDialog = true }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                FileTopBar(
                    innerPadding = innerPadding,
                    onSearch = onNavigateToSearch,
                    onAllVideosClick = { showAllVideos = true },
                    onRefresh = handleRefresh,
                    isRefreshing = isRefreshing || isLoading,
                    showBack = selectedFolder != null || showAllVideos,
                    folderName = when {
                        showAllVideos -> "All Videos"
                        selectedFolder != null -> selectedFolder?.name
                        else -> null
                    },
                    onBack = {
                        if (showAllVideos) {
                            showAllVideos = false
                        } else {
                            viewModel.selectFolder(null)
                        }
                    })

                AnimatedContent(
                    targetState = Triple(isLoading, showAllVideos, selectedFolder),
                    transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(180)) },
                    label = "content"
                ) { (loading, showAll, folder) ->
                    when {
                        loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.loading_videos),
                                        fontSize = 14.sp,
                                        color = textSecondary
                                    )
                                    Text(
                                        text = stringResource(R.string.please_wait_scan),
                                        fontSize = 12.sp,
                                        color = textSecondary.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }

                        showAll -> {
                            AllVideosContent(
                                videos = sortedVideos,
                                viewModel = viewModel,
                                lastPlayedViewModel = lastPlayedViewModel,
                                sortOption = sortOption,
                                onSortChange = { sortOption = it },
                                showSortMenu = showSortMenu,
                                onSortMenuChange = { showSortMenu = it },
                                onVideoClick = { video, position ->
                                    onVideoClick(video, sortedVideos, position)
                                },
                                innerPadding = innerPadding
                            )
                        }

                        folder != null -> {
                            GlassVideoListContent(
                                lastPlayedViewModel = lastPlayedViewModel,
                                videos = folder.videos,
                                viewModel = viewModel,
                                resumableVideo = resumableVideo,
                                onVideoClick = { video, position ->
                                    onVideoClick(video, folder.videos, position)
                                },
                                innerPadding = innerPadding
                            )
                        }

                        else -> {
                            GlassFolderListContent(
                                folders = videoFolders,
                                viewModel = viewModel,
                                onFolderClick = onFolderClick,
                                innerPadding = innerPadding
                            )
                        }
                    }
                }
            }
        }

        // Resume FAB
        AnimatedVisibility(
            visible = resumableVideo != null && hasPermission && !isLoading
                    && !showAllVideos && selectedFolder == null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = innerPadding.calculateBottomPadding() + 16.dp, end = 20.dp),
            enter = fadeIn(tween(300)) + scaleIn(initialScale = 0.85f, animationSpec = spring(
                Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium
            )),
            exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.85f)
        ) {
            resumableVideo?.let { info ->
                ResumeFab(
                    info = info,
                    onResume = {
                        Log.d("FileScreen", "Resume clicked for video: ${info.videoName}")
                        val folder = viewModel.videoFolders.value
                            .firstOrNull { it.path == info.folderPath }
                        val video = folder?.videos?.firstOrNull { it.id == info.videoId }
                            ?: viewModel.allVideos.value.firstOrNull { it.id == info.videoId }
                        if (video != null && File(video.path).exists()) {
                            val playlist = folder?.videos ?: listOf(video)
                            onVideoClick(video, playlist, info.position)
                        } else {
                            Toast.makeText(context, "Video not found", Toast.LENGTH_SHORT).show()
                            lastPlayedViewModel.clear()
                        }
                    },
                    onDismiss = { lastPlayedViewModel.clear() }
                )
            }
        }
    }

    if (showPermissionDialog) {
        PermissionDialog(onDismiss = { showPermissionDialog = false }, onAllow = {
            val perm =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_VIDEO
                else Manifest.permission.READ_EXTERNAL_STORAGE
            permissionLauncher.launch(perm)
        })
    }
}

@Composable
private fun FileTopBar(
    innerPadding: PaddingValues,
    onSearch: () -> Unit,
    onAllVideosClick: () -> Unit,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    showBack: Boolean,
    folderName: String?,
    onBack: () -> Unit
) {
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val isDark = GlassTokens.isDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        if (isDark) Color(0xFF0A0A0A) else Color(0xFFF5F5F5),
                        if (isDark) Color(0xFF0A0A0A).copy(alpha = 0.95f) else Color(0xFFF5F5F5).copy(
                            alpha = 0.95f
                        ),
                        Color.Transparent
                    )
                )
            )
            .padding(top = innerPadding.calculateTopPadding())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnimatedVisibility(visible = showBack) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .glassChip()
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = textPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column {
                    if (!showBack) {
                        Text(
                            text = stringResource(R.string.vidoPlay),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 3.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = if (showBack) folderName ?: stringResource(R.string.folder) else stringResource(R.string.my_library),
                        fontSize = if (showBack) 18.sp else 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        letterSpacing = (-0.4).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Refresh button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .glassChip()
                        .clickable(enabled = !isRefreshing) { onRefresh() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.refresh),
                            tint = textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (!showBack) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .glassChip()
                            .clickable { onAllVideosClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.VideoLibrary,
                            contentDescription = stringResource(R.string.all_videos),
                            tint = textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .glassChip()
                        .clickable { onSearch() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = stringResource(R.string.search),
                        tint = textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun AllVideosContent(
    videos: List<VideoFile>,
    viewModel: VideoViewModel,
    lastPlayedViewModel: LastPlayedViewModel,
    sortOption: SortOption,
    onSortChange: (SortOption) -> Unit,
    showSortMenu: Boolean,
    onSortMenuChange: (Boolean) -> Unit,
    onVideoClick: (VideoFile, Long) -> Unit,
    innerPadding: PaddingValues = PaddingValues(),
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val savedViewMode by themeViewModel.themePreferences.collectAsState()
    var viewMode by remember { mutableStateOf(savedViewMode.viewMode) }
    var showViewMenu by remember { mutableStateOf(false) }

    LaunchedEffect(savedViewMode.viewMode) {
        viewMode = savedViewMode.viewMode
    }

    LaunchedEffect(viewMode) {
        if (viewMode != savedViewMode.viewMode) {
            themeViewModel.setVideoViewMode(viewMode)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassStatChip("${videos.size}", stringResource(R.string.videos))
                GlassStatChip(viewModel.formatSize(videos.sumOf { it.size }), stringResource(R.string.total_size))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Sort button
                Box {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .glassChip()
                            .clickable { onSortMenuChange(true) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Sort",
                            tint = textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { onSortMenuChange(false) },
                        containerColor = if (GlassTokens.isDarkTheme()) Color(0xFF1E1E1E) else Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        SortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        option.displayName,
                                        color = if (sortOption == option) MaterialTheme.colorScheme.primary else textPrimary,
                                        fontSize = 13.sp
                                    )
                                },
                                onClick = {
                                    onSortChange(option)
                                    onSortMenuChange(false)
                                },
                                leadingIcon = {
                                    Icon(
                                        option.icon,
                                        contentDescription = null,
                                        tint = if (sortOption == option) MaterialTheme.colorScheme.primary else textSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (sortOption == option) {
                                        Icon(
                                            Icons.Default.Check,
                                            null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                // View mode button
                Box {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .glassChip()
                            .clickable { showViewMenu = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (viewMode == ViewMode.GRID) Icons.Default.GridView
                            else Icons.AutoMirrored.Filled.ViewList,
                            contentDescription = "Toggle view",
                            tint = textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showViewMenu,
                        onDismissRequest = { showViewMenu = false },
                        containerColor = if (GlassTokens.isDarkTheme()) Color(0xFF1E1E1E) else Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        listOf(
                            ViewMode.LIST to "List View",
                            ViewMode.GRID to "Grid View"
                        ).forEach { (mode, label) ->
                            DropdownMenuItem(text = {
                                Text(
                                    label,
                                    color = if (viewMode == mode) MaterialTheme.colorScheme.primary else textPrimary,
                                    fontSize = 14.sp
                                )
                            }, onClick = {
                                viewMode = mode
                                themeViewModel.setVideoViewMode(mode)
                                showViewMenu = false
                            }, leadingIcon = {
                                Icon(
                                    if (mode == ViewMode.LIST) Icons.AutoMirrored.Filled.ViewList
                                    else Icons.Default.GridView,
                                    contentDescription = null,
                                    tint = if (viewMode == mode) MaterialTheme.colorScheme.primary else textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }, trailingIcon = {
                                if (viewMode == mode) Icon(
                                    Icons.Default.Check,
                                    null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(15.dp)
                                )
                            })
                        }
                    }
                }
            }
        }

        if (videos.isEmpty()) {
            GlassEmptyState(stringResource(R.string.no_videos_found), Icons.Default.VideoLibrary)
        } else {
            AnimatedContent(
                targetState = viewMode,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) + scaleIn(
                        initialScale = 0.9f, animationSpec = tween(300)
                    ) togetherWith fadeOut(animationSpec = tween(300)) + scaleOut(
                        targetScale = 0.9f, animationSpec = tween(300)
                    )
                },
                label = "viewMode"
            ) { mode ->
                when (mode) {
                    ViewMode.LIST -> {
                        AllVideosListView(
                            videos = videos,
                            viewModel = viewModel,
                            lastPlayedViewModel = lastPlayedViewModel,
                            onVideoClick = onVideoClick,
                            innerPadding = innerPadding
                        )
                    }
                    ViewMode.GRID -> {
                        AllVideosGridView(
                            videos = videos,
                            viewModel = viewModel,
                            lastPlayedViewModel = lastPlayedViewModel,
                            onVideoClick = onVideoClick,
                            innerPadding = innerPadding
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GlassStatChip(value: String, label: String) {
    val textSecondary = GlassTokens.getTextSecondary()

    Row(
        modifier = Modifier
            .glassChip()
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = textSecondary,
            letterSpacing = 1.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoItem(
    video: VideoFile,
    viewModel: VideoViewModel,
    lastPlayedViewModel: LastPlayedViewModel,
    playlistViewModel: PlaylistViewModel = koinViewModel(),
    onVideoUpdated: () -> Unit = {},
    onClick: () -> Unit,
    context: Context = LocalContext.current
) {
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val chipBorder = GlassTokens.getChipBorder()

    var showMenu by remember { mutableStateOf(false) }
    var showDetailsSheet by remember { mutableStateOf(false) }
    val playlists by playlistViewModel.state.collectAsState()
    val resumableVideo by lastPlayedViewModel.resumableVideo.collectAsState()

    val savedPosition = remember(resumableVideo, video.id) {
        if (resumableVideo != null &&
            resumableVideo!!.videoId == video.id &&
            resumableVideo!!.position > 0 &&
            resumableVideo!!.position < video.duration) {
            resumableVideo!!.position
        } else {
            0L
        }
    }
    val hasProgress = savedPosition > 0 && savedPosition < video.duration
    val progressFraction = if (hasProgress) savedPosition.toFloat() / video.duration.toFloat() else 0f
    val videoExists = remember(video.path) { File(video.path).exists() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard(cornerRadius = 14.dp)
            .clickable(enabled = videoExists) {
                if (videoExists) onClick()
                else Toast.makeText(context, "Video file not found", Toast.LENGTH_SHORT).show()
            }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 104.dp, height = 66.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (videoExists && video.thumbnailUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(video.thumbnailUri)
                        .crossfade(true).build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (videoExists) Icons.Outlined.VideoLibrary else Icons.Default.Error,
                        null,
                        tint = if (videoExists) textSecondary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            if (videoExists && hasProgress) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 3.dp.toPx()
                    drawArc(
                        color = Color.White.copy(alpha = 0.25f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = stroke)
                    )
                    drawArc(
                        color = Color.Black,
                        startAngle = -90f,
                        sweepAngle = 360f * progressFraction,
                        useCenter = false,
                        style = Stroke(width = stroke)
                    )
                }
            }

            if (videoExists) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(26.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(0.52f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(0.72f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    viewModel.formatDuration(video.duration),
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = video.name.removeSuffix(".mp4").removeSuffix(".mkv").removeSuffix(".avi")
                    .removeSuffix(".mov").removeSuffix(".webm").removeSuffix(".flv"),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (videoExists) textPrimary else textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .glassChip()
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        viewModel.formatDuration(video.duration),
                        fontSize = 10.sp,
                        color = if (videoExists) MaterialTheme.colorScheme.primary else textSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .clip(RoundedCornerShape(50))
                        .background(textSecondary.copy(alpha = 0.5f))
                )
                Text(
                    viewModel.formatSize(video.size),
                    fontSize = 11.sp,
                    color = textSecondary
                )

                if (!videoExists) {
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                    )
                    Text(
                        stringResource(R.string.file_missing),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                } else if (hasProgress) {
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    )
                    Text(
                        stringResource(R.string.percent_watched, (progressFraction * 100).toInt()),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (videoExists) {
            Box {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showMenu = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        null,
                        tint = textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    containerColor = if (GlassTokens.isDarkTheme()) Color(0xFF1E1E1E) else Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    DropdownMenuItem(text = {
                        Text(stringResource(R.string.play), color = textPrimary, fontSize = 14.sp)
                    }, onClick = {
                        showMenu = false
                        onClick()
                    }, leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.13f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    })

                    DropdownMenuItem(text = {
                        Text(stringResource(R.string.details), color = textPrimary, fontSize = 14.sp)
                    }, onClick = {
                        showMenu = false
                        showDetailsSheet = true
                    }, leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.13f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Info,
                                null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    })

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 3.dp),
                        color = chipBorder
                    )

                    if (playlists.playlists.isNotEmpty()) {
                        playlists.playlists.forEach { playlist ->
                            DropdownMenuItem(text = {
                                Text(
                                    stringResource(R.string.add_to_playlist, playlist.name),
                                    color = textPrimary,
                                    fontSize = 14.sp
                                )
                            }, onClick = {
                                playlistViewModel.addVideoToPlaylist(playlist.id, video)
                                showMenu = false
                            }, leadingIcon = {
                                Icon(
                                    Icons.AutoMirrored.Outlined.QueueMusic,
                                    null,
                                    tint = textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            })
                        }
                    } else {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(R.string.no_playlists_yet),
                                    color = textSecondary,
                                    fontSize = 13.sp
                                )
                            },
                            onClick = { showMenu = false },
                            enabled = false
                        )
                    }
                }
            }
        }
    }

    if (showDetailsSheet && videoExists) {
        GlassVideoDetailsBottomSheet(
            video = video,
            viewModel = viewModel,
            onDismiss = { showDetailsSheet = false },
            onPlay = {
                showDetailsSheet = false
                onClick()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassVideoDetailsBottomSheet(
    video: VideoFile, viewModel: VideoViewModel, onDismiss: () -> Unit, onPlay: () -> Unit
) {
    val context = LocalContext.current
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val isDark = GlassTokens.isDarkTheme()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = if (isDark) Color(0xFF1A1C1E) else Color(0xFFFFFFFF),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (video.thumbnailUri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(video.thumbnailUri)
                            .crossfade(true).build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.VideoLibrary,
                            null,
                            tint = textSecondary,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(50.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = video.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Folder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = video.folderName,
                    fontSize = 13.sp,
                    color = textSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF2C2C2C) else Color(0xFFF0F0F0)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailRow(
                        icon = Icons.Default.Timer,
                        label = stringResource(R.string.duration),
                        value = viewModel.formatDuration(video.duration),
                        iconColor = MaterialTheme.colorScheme.primary
                    )
                    DetailRow(
                        icon = Icons.Default.Storage,
                        label = stringResource(R.string.size),
                        value = viewModel.formatSize(video.size),
                        iconColor = MaterialTheme.colorScheme.primary
                    )
                    DetailRow(
                        icon = Icons.Default.DateRange,
                        label = stringResource(R.string.date_added),
                        value = formatDate(video.dateAdded),
                        iconColor = MaterialTheme.colorScheme.primary
                    )
                    DetailRow(
                        icon = Icons.Default.Folder,
                        label = stringResource(R.string.location),
                        value = video.folderPath,
                        iconColor = MaterialTheme.colorScheme.primary,
                        isPath = true
                    )
                    DetailRow(
                        icon = Icons.Default.Description,
                        label = stringResource(R.string.file_name),
                        value = File(video.path).name,
                        iconColor = MaterialTheme.colorScheme.primary,
                        isPath = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        onDismiss()
                        onPlay()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.play), fontSize = 14.sp, color = Color.White)
                }

                OutlinedButton(
                    onClick = {
                        val shareIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_STREAM, video.uri)
                            type = "video/*"
                        }
                        context.startActivity(
                            android.content.Intent.createChooser(
                                shareIntent, "Share Video"
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.share), fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun GlassFolderListContent(
    folders: List<VideoFolder>,
    viewModel: VideoViewModel,
    onFolderClick: (VideoFolder) -> Unit,
    innerPadding: PaddingValues = PaddingValues(),
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val savedViewMode by themeViewModel.themePreferences.collectAsState()
    var viewMode by remember { mutableStateOf(savedViewMode.viewMode) }
    var showViewMenu by remember { mutableStateOf(false) }

    LaunchedEffect(savedViewMode.viewMode) {
        viewMode = savedViewMode.viewMode
    }

    LaunchedEffect(viewMode) {
        if (viewMode != savedViewMode.viewMode) {
            themeViewModel.setFolderViewMode(viewMode)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassStatChip("${folders.size}", stringResource(R.string.folders))
                GlassStatChip("${folders.sumOf { it.videoCount }}", stringResource(R.string.videos))
            }

            Box {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .glassChip()
                        .clickable { showViewMenu = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (viewMode == ViewMode.GRID) Icons.Default.GridView
                        else Icons.AutoMirrored.Filled.ViewList,
                        contentDescription = "Toggle view",
                        tint = textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = showViewMenu,
                    onDismissRequest = { showViewMenu = false },
                    containerColor = if (GlassTokens.isDarkTheme()) Color(0xFF1E1E1E) else Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    DropdownMenuItem(text = {
                        Text(
                            stringResource(R.string.list_view),
                            color = if (viewMode == ViewMode.LIST) MaterialTheme.colorScheme.primary else textPrimary,
                            fontSize = 14.sp
                        )
                    }, onClick = {
                        viewMode = ViewMode.LIST
                        themeViewModel.setFolderViewMode(ViewMode.LIST)
                        showViewMenu = false
                    }, leadingIcon = {
                        Icon(
                            Icons.AutoMirrored.Filled.ViewList,
                            contentDescription = null,
                            tint = if (viewMode == ViewMode.LIST) MaterialTheme.colorScheme.primary else textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }, trailingIcon = {
                        if (viewMode == ViewMode.LIST) {
                            Icon(
                                Icons.Default.Check,
                                null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    })

                    DropdownMenuItem(text = {
                        Text(
                            stringResource(R.string.grid_view),
                            color = if (viewMode == ViewMode.GRID) MaterialTheme.colorScheme.primary else textPrimary,
                            fontSize = 14.sp
                        )
                    }, onClick = {
                        viewMode = ViewMode.GRID
                        themeViewModel.setFolderViewMode(ViewMode.GRID)
                        showViewMenu = false
                    }, leadingIcon = {
                        Icon(
                            Icons.Default.GridView,
                            contentDescription = null,
                            tint = if (viewMode == ViewMode.GRID) MaterialTheme.colorScheme.primary else textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }, trailingIcon = {
                        if (viewMode == ViewMode.GRID) {
                            Icon(
                                Icons.Default.Check,
                                null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    })
                }
            }
        }

        if (folders.isEmpty()) {
            GlassEmptyState(stringResource(R.string.no_video_folders_found), Icons.Default.Folder)
        } else {
            AnimatedContent(
                targetState = viewMode,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) + scaleIn(
                        initialScale = 0.9f, animationSpec = tween(300)
                    ) togetherWith fadeOut(animationSpec = tween(300)) + scaleOut(
                        targetScale = 0.9f, animationSpec = tween(300)
                    )
                },
                label = "viewMode"
            ) { mode ->
                when (mode) {
                    ViewMode.LIST -> {
                        GlassFolderListView(folders, viewModel, onFolderClick, innerPadding)
                    }
                    ViewMode.GRID -> {
                        GlassFolderGridView(folders, viewModel, onFolderClick, innerPadding)
                    }
                }
            }
        }
    }
}

@Composable
fun GlassFolderGridView(
    folders: List<VideoFolder>,
    viewModel: VideoViewModel,
    onFolderClick: (VideoFolder) -> Unit,
    innerPadding: PaddingValues = PaddingValues()
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 6.dp,
            bottom = 6.dp + innerPadding.calculateBottomPadding() + 80.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(folders, key = { it.path }) { folder ->
            GlassFolderGridItem(folder, viewModel) { onFolderClick(folder) }
        }
    }
}

@Composable
fun GlassFolderGridItem(
    folder: VideoFolder, viewModel: VideoViewModel, onClick: () -> Unit
) {
    val thumbUri = folder.videos.firstOrNull()?.thumbnailUri
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.88f)
            .clip(RoundedCornerShape(16.dp))
            .glassCard()
            .clickable { onClick() }) {
        if (thumbUri != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(thumbUri).crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Folder,
                    null,
                    tint = textSecondary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(if (thumbUri != null) 0.15f else 0f),
                            Color.Black.copy(0.88f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .size(30.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Folder,
                null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(0.55f))
                .padding(horizontal = 7.dp, vertical = 4.dp)
        ) {
            Text(
                "${folder.videoCount}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = folder.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                viewModel.formatSize(folder.totalSize),
                fontSize = 11.sp,
                color = Color.White.copy(0.55f)
            )
        }
    }
}



@Composable
fun GlassFolderListView(
    folders: List<VideoFolder>,
    viewModel: VideoViewModel,
    onFolderClick: (VideoFolder) -> Unit,
    innerPadding: PaddingValues = PaddingValues()
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 6.dp,
            bottom = 6.dp + innerPadding.calculateBottomPadding() + 80.dp
        )
    ) {
        items(folders, key = { it.path }) { folder ->
            GlassFolderItem(folder, viewModel) { onFolderClick(folder) }
        }
    }
}

@Composable
fun GlassFolderItem(folder: VideoFolder, viewModel: VideoViewModel, onClick: () -> Unit) {
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val thumbUri = folder.videos.firstOrNull()?.thumbnailUri

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard(cornerRadius = 14.dp)
            .clickable { onClick() }
            .padding(11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (thumbUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(thumbUri)
                        .crossfade(true).build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(0.38f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Folder,
                        null,
                        tint = Color.White.copy(0.85f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Icon(
                    Icons.Default.Folder,
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(13.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = folder.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    stringResource(R.string.videos_count, folder.videoCount),
                    fontSize = 12.sp,
                    color = textSecondary
                )
                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .clip(RoundedCornerShape(50))
                        .background(textSecondary.copy(alpha = 0.5f))
                )
                Text(
                    viewModel.formatSize(folder.totalSize),
                    fontSize = 12.sp,
                    color = textSecondary
                )
            }
        }

        Icon(
            Icons.Default.ChevronRight,
            null,
            tint = textSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun GlassEmptyState(message: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    val textSecondary = GlassTokens.getTextSecondary()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .glassChip(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    null,
                    tint = textSecondary,
                    modifier = Modifier.size(34.dp)
                )
            }
            Text(
                message,
                fontSize = 14.sp,
                color = textSecondary
            )
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun AllVideosListView(
    videos: List<VideoFile>,
    viewModel: VideoViewModel,
    lastPlayedViewModel: LastPlayedViewModel,
    onVideoClick: (VideoFile, Long) -> Unit,
    innerPadding: PaddingValues = PaddingValues()
) {
    var refreshTrigger by remember { mutableIntStateOf(0) }
    val currentVideos = remember(videos, refreshTrigger) { videos }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 6.dp,
            bottom = 6.dp + innerPadding.calculateBottomPadding() + 80.dp
        )
    ) {
        items(currentVideos, key = { it.id }) { video ->
            VideoItem(
                video = video,
                viewModel = viewModel,
                lastPlayedViewModel = lastPlayedViewModel,
                onVideoUpdated = { refreshTrigger++ },
                onClick = { onVideoClick(video, 0L) }
            )
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun AllVideosGridView(
    videos: List<VideoFile>,
    viewModel: VideoViewModel,
    lastPlayedViewModel: LastPlayedViewModel,
    onVideoClick: (VideoFile, Long) -> Unit,
    innerPadding: PaddingValues = PaddingValues()
) {
    var refreshTrigger by remember { mutableIntStateOf(0) }
    val currentVideos = remember(videos, refreshTrigger) { videos }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 6.dp,
            bottom = 6.dp + innerPadding.calculateBottomPadding() + 80.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(currentVideos, key = { it.id }) { video ->
            VideoGridItem(
                video = video,
                viewModel = viewModel,
                lastPlayedViewModel = lastPlayedViewModel,
                onClick = { onVideoClick(video, 0L) }
            )
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoGridItem(
    video: VideoFile,
    viewModel: VideoViewModel,
    lastPlayedViewModel: LastPlayedViewModel,
    onClick: () -> Unit,
    context: Context = LocalContext.current
) {
    val resumableVideo by lastPlayedViewModel.resumableVideo.collectAsState()
    val savedPosition = remember(resumableVideo, video.id) {
        if (resumableVideo?.videoId == video.id &&
            resumableVideo!!.position > 0 &&
            resumableVideo!!.position < video.duration) {
            resumableVideo!!.position
        } else {
            0L
        }
    }
    val hasProgress = savedPosition > 0 && savedPosition < video.duration
    val progressFraction = if (hasProgress) savedPosition.toFloat() / video.duration.toFloat() else 0f
    val videoExists = remember(video.path) { File(video.path).exists() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.88f)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (videoExists) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .clickable(enabled = videoExists) {
                if (videoExists) onClick()
                else {
                    Toast.makeText(context, "Video file not found", Toast.LENGTH_SHORT).show()
                }
            }
    ) {
        if (videoExists && video.thumbnailUri != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(video.thumbnailUri)
                    .crossfade(true).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (videoExists) Icons.Outlined.VideoLibrary else Icons.Default.Error,
                    null,
                    tint = if (videoExists) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        if (videoExists && hasProgress) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = 4.dp.toPx()
                drawArc(
                    color = Color.White.copy(alpha = 0.3f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = stroke)
                )
                drawArc(
                    color = Color.Black,
                    startAngle = -90f,
                    sweepAngle = 360f * progressFraction,
                    useCenter = false,
                    style = Stroke(width = stroke)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(if (videoExists) 0.15f else 0f),
                            Color.Black.copy(0.88f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black.copy(0.72f))
                .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Text(
                viewModel.formatDuration(video.duration),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (videoExists) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = video.name.removeSuffix(".mp4").removeSuffix(".mkv").removeSuffix(".avi")
                    .removeSuffix(".mov").removeSuffix(".webm").removeSuffix(".flv"),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (videoExists) Color.White else Color.White.copy(alpha = 0.5f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    viewModel.formatSize(video.size),
                    fontSize = 10.sp,
                    color = Color.White.copy(0.7f)
                )
                if (!videoExists) {
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                    )
                    Text(
                        stringResource(R.string.missing),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                } else if (hasProgress) {
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(0.5f))
                    )
                    Text(
                        "${(progressFraction * 100).toInt()}%",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ResumeFab(
    info: LastPlayedInfo,
    onResume: () -> Unit,
    onDismiss: () -> Unit
) {
    val progressFraction = if (info.duration > 0)
        (info.position.toFloat() / info.duration.toFloat()).coerceIn(0f, 1f)
    else 0f
    val primaryColor = MaterialTheme.colorScheme.primary
    val cs = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .widthIn(min = 180.dp, max = 220.dp)
            .clip(RoundedCornerShape(32.dp))
            .shadow(12.dp, shape = RoundedCornerShape(32.dp), clip = false),
        color = cs.surface,
        tonalElevation = 4.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .clickable { onResume() }
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .background(cs.surfaceVariant)
                ) {
                    if (!info.thumbnailUri.isNullOrEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(info.thumbnailUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.VideoLibrary,
                                null,
                                tint = cs.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.Center)
                    )
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 2.dp.toPx()
                    val radius = (size.minDimension - stroke) / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    drawCircle(
                        color = Color.White.copy(alpha = 0.25f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = stroke)
                    )

                    val sweepAngle = 360f * progressFraction
                    if (sweepAngle > 0) {
                        drawArc(
                            color = primaryColor,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = Offset(stroke / 2, stroke / 2),
                            size = androidx.compose.ui.geometry.Size(
                                size.width - stroke,
                                size.height - stroke
                            ),
                            style = Stroke(width = stroke)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = stringResource(R.string.continue_text),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = info.videoName
                        .removeSuffix(".mp4").removeSuffix(".mkv")
                        .removeSuffix(".avi").removeSuffix(".mov")
                        .removeSuffix(".webm").removeSuffix(".flv"),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = cs.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        tint = cs.onSurfaceVariant,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = formatDuration(info.position),
                        fontSize = 9.sp,
                        color = cs.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "•",
                        fontSize = 8.sp,
                        color = cs.onSurfaceVariant
                    )
                    Text(
                        text = formatDuration(info.duration),
                        fontSize = 9.sp,
                        color = cs.onSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = cs.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun StatChip(value: String, label: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun FolderGridView(
    folders: List<VideoFolder>,
    viewModel: VideoViewModel,
    onFolderClick: (VideoFolder) -> Unit,
    innerPadding: PaddingValues = PaddingValues()
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 6.dp,
            bottom = 6.dp + innerPadding.calculateBottomPadding() + 80.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(folders, key = { it.path }) { folder ->
            FolderGridItem(folder, viewModel) { onFolderClick(folder) }
        }
    }
}

@Composable
fun FolderGridItem(
    folder: VideoFolder, viewModel: VideoViewModel, onClick: () -> Unit
) {
    val thumbUri = folder.videos.firstOrNull()?.thumbnailUri

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.88f)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }) {
        if (thumbUri != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(thumbUri).crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(if (thumbUri != null) 0.15f else 0f),
                            Color.Black.copy(0.88f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .size(30.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Folder,
                null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(0.55f))
                .padding(horizontal = 7.dp, vertical = 4.dp)
        ) {
            Text(
                "${folder.videoCount}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = folder.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                viewModel.formatSize(folder.totalSize),
                fontSize = 11.sp,
                color = Color.White.copy(0.55f)
            )
        }
    }
}

@Composable
fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    iconColor: Color,
    isPath: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = value,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = if (isPath) 2 else 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(0.6f)
                .padding(start = 8.dp)
        )
    }
}

fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    return format.format(date)
}

@Composable
fun PermissionRequestScreen(onRequestPermission: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.13f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.VideoLibrary,
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                stringResource(R.string.access_required),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = (-0.3).sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                stringResource(R.string.allow_browse_videos),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = onRequestPermission,
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    stringResource(R.string.grant_permission),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun PermissionDialog(onDismiss: () -> Unit, onAllow: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                stringResource(R.string.permission_required),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Text(
                stringResource(R.string.permission_message),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onAllow) {
                Text(
                    stringResource(R.string.allow), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.deny), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}




