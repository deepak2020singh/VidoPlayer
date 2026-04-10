package com.foss.vidoplay.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
    val textPrimary = GlassTokens.getTextPrimary()

    val folders by viewModel.videoFolders.collectAsState()
    val resumableVideo by lastPlayedViewModel.resumableVideo.collectAsState()
    val folder = folders.firstOrNull { it.path == folderPath }
    val videos = folder?.videos ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        folderName,
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
            onVideoClick = { video, savedPosition ->
                onVideoClick(video, savedPosition)
            },
            modifier = Modifier.padding(padding)
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
                    resumableVideo.position < video.duration) {
                    resumableVideo.position
                } else {
                    0L
                }

                VideoItem(
                    video = video,
                    viewModel = viewModel,
                    lastPlayedViewModel = lastPlayedViewModel,
                    onVideoUpdated = { refreshTrigger++ },
                    onClick = { onVideoClick(video, savedPosition) }
                )
            }
        }
    }
}