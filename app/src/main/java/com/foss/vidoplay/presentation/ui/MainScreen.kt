package com.foss.vidoplay.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.foss.vidoplay.AuthRoutes
import com.foss.vidoplay.MainRoutes
import com.foss.vidoplay.NavItem
import com.foss.vidoplay.R
import com.foss.vidoplay.domain.model.VideoFolder
import com.foss.vidoplay.presentation.viewModel.ExoPlayerViewModel
import com.foss.vidoplay.presentation.viewModel.LastPlayedViewModel
import com.foss.vidoplay.presentation.viewModel.VideoViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    onFolderClick: (VideoFolder) -> Unit,
    onNavigateToAbout: () -> Unit,
    onPlayVideo: (videoId: Long, folderPath: String, startPosition: Long) -> Unit
) {
    val backStack1 = rememberNavBackStack(MainRoutes.File)
    val navList = listOf(
        NavItem(R.drawable.ic_file, "File"),
        NavItem(R.drawable.ic_search, "Search"),
        NavItem(R.drawable.ic_playlists, "Playlists"),
        NavItem(R.drawable.ic_setttings, "Settings")
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
            )
            {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navList.forEachIndexed { _, navItem ->
                        val isSelected = when (backStack1.last()) {
                            MainRoutes.File -> navItem.title == "File"
                            MainRoutes.Search -> navItem.title == "Search"
                            MainRoutes.Playlists -> navItem.title == "Playlists"
                            MainRoutes.Settings -> navItem.title == "Settings"
                            else -> false
                        }
                        val iconScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.2f else 1f,
                            animationSpec = tween(durationMillis = 200),
                            label = "iconScale"
                        )
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                val target = when (navItem.title) {
                                    "File" -> MainRoutes.File
                                    "Search" -> MainRoutes.Search
                                    "Playlists" -> MainRoutes.Playlists
                                    "Settings" -> MainRoutes.Settings
                                    else -> MainRoutes.File
                                }
                                if (backStack1.last() != target) {
                                    backStack1.clear()
                                    backStack1.add(target)
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(navItem.icon),
                                    contentDescription = navItem.title,
                                    modifier = Modifier.size(24.dp).scale(iconScale),
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavDisplay(
            backStack = backStack1,
            onBack = { if (backStack1.size > 1) backStack1.removeLastOrNull() },
            entryDecorators = listOf(),
            entryProvider = entryProvider {

                entry<MainRoutes.File> {
                    FileScreen(
                        innerPadding = padding,
                        onVideoClick = { video, playlist, startPosition ->
                            onPlayVideo(video.id, video.folderPath, startPosition)
                        },
                        onFolderClick = onFolderClick,
                        onNavigateToSearch = {
                            backStack1.clear()
                            backStack1.add(MainRoutes.Search)
                        },
                    )
                }

                entry<MainRoutes.Search> {
                    SearchScreen(
                        innerPadding = padding,
                        onNavigateToFile = {
                            backStack1.clear()
                            backStack1.add(MainRoutes.File)
                        },
                        onVideoClick = { video, playlist, startPosition ->
                            onPlayVideo(video.id, video.folderPath, startPosition)
                        },
                    )
                }

                entry<MainRoutes.Playlists> {
                    PlaylistsScreen(
                        innerPadding = padding,
                        onPlayVideo = { videoId, folderPath, startPosition ->
                            onPlayVideo(videoId, folderPath, startPosition)
                        }
                    )
                }

                entry<MainRoutes.Settings> {
                    SettingScreen(
                        innerPadding = padding,
                        onNavigateToAbout = onNavigateToAbout
                    )
                }

            },
            transitionSpec = {
                slideInHorizontally(initialOffsetX = { it }) togetherWith slideOutHorizontally(targetOffsetX = { -it })
            },
            popTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(targetOffsetX = { it })
            },
            predictivePopTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(targetOffsetX = { it })
            },
        )
    }
}

@Composable
fun AuthScreen(innerPadding: PaddingValues) {
    val backStack = rememberNavBackStack(AuthRoutes.Splash)
    val lastPlayedViewModel: LastPlayedViewModel = koinViewModel()
    val resumableVideo by lastPlayedViewModel.resumableVideo.collectAsState()

    NavDisplay(
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
        entryDecorators = listOf(),
        entryProvider = entryProvider {

            entry<AuthRoutes.Splash> {
                SplashScreen(
                    onSplashFinished = {
                        backStack.clear()
                        backStack.add(AuthRoutes.Main)
                    },
                )
            }

            entry<AuthRoutes.About> {
                AboutScreen(innerPadding = innerPadding, onBack = { backStack.removeLastOrNull() })
            }

            entry<AuthRoutes.AllVideosFolder> { route ->
                AllVideosFolderScreen(
                    folderPath = route.folderPath,
                    folderName = route.folderName,
                    onBack = { backStack.removeLastOrNull() },
                    onVideoClick = { video, startPosition ->
                        backStack.add(
                            MainRoutes.Player(
                                videoId = video.id,
                                folderPath = route.folderPath,
                                startPosition = startPosition
                            )
                        )
                    },
                )
            }

            entry<MainRoutes.Player> { playerRoute ->
                val videoViewModel: VideoViewModel = koinViewModel()
                val folders by videoViewModel.videoFolders.collectAsState()

                val folder = remember(playerRoute.folderPath, folders) {
                    folders.firstOrNull { it.path == playerRoute.folderPath }
                }

                val allVideos = remember(folder) {
                    folder?.videos ?: emptyList()
                }

                val video = remember(playerRoute.videoId, allVideos) {
                    allVideos.firstOrNull { it.id == playerRoute.videoId }
                }

                if (video != null) {
                    val currentIndex = remember(video.id, allVideos) {
                        allVideos.indexOf(video)
                    }

                    var isPlaying by remember { mutableStateOf(true) }

                    val startPosition = remember(playerRoute.startPosition, resumableVideo, video.id) {
                        when {
                            playerRoute.startPosition > 0 -> playerRoute.startPosition
                            resumableVideo != null &&
                                    resumableVideo!!.videoId == video.id &&
                                    resumableVideo!!.position > 0 &&
                                    resumableVideo!!.position < video.duration -> resumableVideo!!.position
                            else -> 0L
                        }
                    }

                    // Obtain ExoPlayerViewModel and set the playlist
                    val exoPlayerViewModel: ExoPlayerViewModel = koinViewModel()
                    LaunchedEffect(Unit) {
                        exoPlayerViewModel.setPlaylist(allVideos, video, currentIndex)
                    }

                    LaunchedEffect(video.id) {
                        isPlaying = true
                    }

                    // Use the refactored ExoPlayerScreen (no allVideos, currentIndex, onNext/onPrevious)
                    ExoPlayerScreen(
                        video = video,
                        isPlaying = isPlaying,
                        onPlayPause = { isPlaying = !isPlaying },
                        onClose = { backStack.removeLastOrNull() },
                        viewModel = exoPlayerViewModel,
                        startPosition = startPosition,
                        innerPadding = innerPadding
                    )
                }
            }

            entry<AuthRoutes.Main> {
                MainScreen(
                    onNavigateToAbout = { backStack.add(AuthRoutes.About) },
                    onFolderClick = { folder ->
                        backStack.add(
                            AuthRoutes.AllVideosFolder(
                                folderPath = folder.path, folderName = folder.name
                            )
                        )
                    },
                    onPlayVideo = { videoId, folderPath, startPosition ->
                        backStack.add(
                            MainRoutes.Player(
                                videoId = videoId,
                                folderPath = folderPath,
                                startPosition = startPosition
                            )
                        )
                    },
                )
            }
        },
        transitionSpec = {
            slideInHorizontally(initialOffsetX = { it }) togetherWith slideOutHorizontally(targetOffsetX = { -it })
        },
        popTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(targetOffsetX = { it })
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(targetOffsetX = { it })
        },
    )
}