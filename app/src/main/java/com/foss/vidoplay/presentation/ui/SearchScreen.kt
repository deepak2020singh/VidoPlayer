package com.foss.vidoplay.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foss.vidoplay.domain.model.VideoFile
import com.foss.vidoplay.domain.model.VideoFolder
import com.foss.vidoplay.presentation.common.GlassTokens
import com.foss.vidoplay.presentation.common.SearchFilter
import com.foss.vidoplay.presentation.common.formatDuration
import com.foss.vidoplay.presentation.common.formatFileSize
import com.foss.vidoplay.presentation.common.glassCard
import com.foss.vidoplay.presentation.common.glassChip
import com.foss.vidoplay.presentation.viewModel.VideoViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    innerPadding: PaddingValues,
    onNavigateToFile: () -> Unit = {},
    onVideoClick: (VideoFile, List<VideoFile>, Long) -> Unit = { _, _, _ -> },
    onFolderClick: (VideoFolder) -> Unit = {},
    viewModel: VideoViewModel = koinViewModel()
) {
    // Dynamic colors
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val textTertiary = GlassTokens.getTextTertiary()
    val chipBg = GlassTokens.getChipBg()
    val chipBorder = GlassTokens.getChipBorder()
    val isDark = GlassTokens.isDarkTheme()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(SearchFilter.ALL) }
    val recentSearches = remember { mutableStateListOf<String>() }

    var searchResults by remember { mutableStateOf(SearchResults(emptyList(), emptyList())) }
    var isSearching by remember { mutableStateOf(false) }

    val videoFolders by viewModel.videoFolders.collectAsState()

    LaunchedEffect(Unit) {
        loadRecentSearches { recentSearches.addAll(it) }
    }

    LaunchedEffect(searchQuery, selectedFilter) {
        if (searchQuery.isNotBlank()) {
            isSearching = true
            kotlinx.coroutines.delay(300)
            val results = performSearch(searchQuery, selectedFilter, videoFolders)
            searchResults = results
            isSearching = false

            if (searchQuery.isNotBlank() && !recentSearches.contains(searchQuery)) {
                recentSearches.add(0, searchQuery)
                if (recentSearches.size > 10) {
                    recentSearches.removeAt(recentSearches.size - 1)
                }
                saveRecentSearches(recentSearches)
            }
        } else {
            searchResults = SearchResults(emptyList(), emptyList())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF0A0A0A) else Color(0xFFF5F5F5))
            .padding(innerPadding)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Glass back button
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .glassChip(cornerRadius = 16.dp)
                    .clickable { onNavigateToFile() },
                color = Color.Transparent
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = textPrimary
                    )
                }
            }

            // Glass search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "Search videos, folders...",
                        color = textTertiary
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = chipBorder,
                    focusedContainerColor = if (isDark) Color(0xFF1A1C1E).copy(alpha = 0.6f) else Color(0xFFFFFFFF).copy(alpha = 0.9f),
                    unfocusedContainerColor = if (isDark) Color(0xFF1A1C1E).copy(alpha = 0.6f) else Color(0xFFFFFFFF).copy(alpha = 0.9f),
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                singleLine = true,
                textStyle = TextStyle(color = textPrimary)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Glass Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassFilterChip(
                selected = selectedFilter == SearchFilter.ALL,
                onClick = { selectedFilter = SearchFilter.ALL },
                icon = Icons.Default.Search,
                label = "All",
                modifier = Modifier.weight(1f)
            )
            GlassFilterChip(
                selected = selectedFilter == SearchFilter.VIDEOS,
                onClick = { selectedFilter = SearchFilter.VIDEOS },
                icon = Icons.Outlined.Videocam,
                label = "Videos",
                modifier = Modifier.weight(1f)
            )
            GlassFilterChip(
                selected = selectedFilter == SearchFilter.FOLDERS,
                onClick = { selectedFilter = SearchFilter.FOLDERS },
                icon = Icons.Outlined.Folder,
                label = "Folders",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = chipBorder
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            searchQuery.isEmpty() -> {
                GlassRecentSearchesSection(
                    recentSearches = recentSearches,
                    onSearchClick = { query -> searchQuery = query },
                    onClearAll = {
                        recentSearches.clear()
                        saveRecentSearches(recentSearches)
                    }
                )
            }

            isSearching -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            else -> {
                GlassSearchResultsContent(
                    query = searchQuery,
                    selectedFilter = selectedFilter,
                    videoResults = searchResults.videos,
                    folderResults = searchResults.folders,
                    onVideoClick = { video ->
                        val folder = videoFolders.find { it.videos.any { v -> v.id == video.id } }
                        val playlist = folder?.videos ?: listOf(video)
                        onVideoClick(video, playlist, 0L)
                    },
                    onFolderClick = onFolderClick
                )
            }
        }
    }
}


@Composable
fun GlassFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    val textSecondary = GlassTokens.getTextSecondary()

    Surface(
        modifier = modifier
            .glassChip(cornerRadius = 24.dp)
            .clickable { onClick() },
        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) Color.White else textSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                label,
                fontSize = 14.sp,
                color = if (selected) Color.White else textSecondary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun GlassRecentSearchesSection(
    recentSearches: List<String>,
    onSearchClick: (String) -> Unit,
    onClearAll: () -> Unit
) {
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val chipBorder = GlassTokens.getChipBorder()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    "Recent Searches",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
            }

            if (recentSearches.isNotEmpty()) {
                Text(
                    text = "Clear All",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { onClearAll() }
                        .padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (recentSearches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No recent searches",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textSecondary
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentSearches) { search ->
                    GlassRecentSearchItem(
                        query = search,
                        onClick = { onSearchClick(search) }
                    )
                }
            }
        }
    }
}

@Composable
fun GlassRecentSearchItem(
    query: String,
    onClick: () -> Unit
) {
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .glassChip(cornerRadius = 12.dp)
            .clickable { onClick() },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = query,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textPrimary
                )
            }

            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = textSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ==================== GLASS SEARCH RESULTS CONTENT ====================

@Composable
fun GlassSearchResultsContent(
    query: String,
    selectedFilter: SearchFilter,
    videoResults: List<VideoFile>,
    folderResults: List<VideoFolder>,
    onVideoClick: (VideoFile) -> Unit,
    onFolderClick: (VideoFolder) -> Unit
) {
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()

    val filteredVideos = if (selectedFilter == SearchFilter.FOLDERS) emptyList() else videoResults
    val filteredFolders = if (selectedFilter == SearchFilter.VIDEOS) emptyList() else folderResults

    val totalResults = filteredVideos.size + filteredFolders.size

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (totalResults > 0) {
            Text(
                text = "Found $totalResults results for \"$query\"",
                style = MaterialTheme.typography.labelLarge,
                color = textSecondary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            if (filteredVideos.isNotEmpty()) {
                item {
                    GlassSectionHeader(
                        title = "Videos",
                        count = filteredVideos.size,
                        icon = Icons.Outlined.Videocam
                    )
                }

                items(filteredVideos) { video ->
                    GlassVideoSearchResultItem(
                        video = video,
                        query = query,
                        onClick = { onVideoClick(video) }
                    )
                }
            }

            if (filteredFolders.isNotEmpty()) {
                item {
                    GlassSectionHeader(
                        title = "Folders",
                        count = filteredFolders.size,
                        icon = Icons.Outlined.Folder
                    )
                }

                items(filteredFolders) { folder ->
                    GlassFolderSearchResultItem(
                        folder = folder,
                        query = query,
                        onClick = { onFolderClick(folder) }
                    )
                }
            }

            if (totalResults == 0) {
                item {
                    GlassNoResultsFound(query = query)
                }
            }
        }
    }
}

// ==================== GLASS SECTION HEADER ====================

@Composable
fun GlassSectionHeader(
    title: String,
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val textPrimary = GlassTokens.getTextPrimary()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = textPrimary
        )
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

// ==================== GLASS VIDEO SEARCH RESULT ITEM ====================

@Composable
fun GlassVideoSearchResultItem(
    video: VideoFile,
    query: String,
    onClick: () -> Unit
) {
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glassCard(cornerRadius = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(60.dp, 60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .glassChip(),
                    color = Color.Transparent
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.Videocam,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    GlassHighlightedText(
                        text = video.name,
                        highlight = query,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = textPrimary
                        ),
                        highlightColor = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDuration(video.duration),
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecondary
                        )

                        Text(
                            text = "•",
                            color = textSecondary,
                            fontSize = 12.sp
                        )

                        Text(
                            text = formatFileSize(video.size),
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecondary
                        )
                    }
                }
            }
        }
    }
}

// ==================== GLASS FOLDER SEARCH RESULT ITEM ====================

@Composable
fun GlassFolderSearchResultItem(
    folder: VideoFolder,
    query: String,
    onClick: () -> Unit
) {
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glassCard(cornerRadius = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(50.dp, 50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .glassChip(),
                    color = Color.Transparent
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.Folder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    GlassHighlightedText(
                        text = folder.name,
                        highlight = query,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = textPrimary
                        ),
                        highlightColor = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${folder.videoCount} videos",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "•",
                            color = textSecondary,
                            fontSize = 12.sp
                        )

                        Text(
                            text = formatFileSize(folder.totalSize),
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecondary
                        )
                    }
                }
            }
        }
    }
}

// ==================== GLASS NO RESULTS FOUND ====================

@Composable
fun GlassNoResultsFound(query: String) {
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val chipBg = GlassTokens.getChipBg()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = chipBg
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = textSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Text(
            text = "No results found",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = textPrimary
        )

        Text(
            text = "No videos or folders match \"$query\"",
            style = MaterialTheme.typography.bodyMedium,
            color = textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Text(
            text = "Try different keywords",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

// ==================== GLASS HIGHLIGHTED TEXT ====================

@Composable
fun GlassHighlightedText(
    text: String,
    highlight: String,
    style: TextStyle,
    highlightColor: Color
) {
    if (highlight.isEmpty()) {
        Text(text = text, style = style, maxLines = 1, overflow = TextOverflow.Ellipsis)
        return
    }

    val lowerText = text.lowercase()
    val lowerHighlight = highlight.lowercase()
    val parts = mutableListOf<String>()
    var lastIndex = 0

    while (lastIndex <= text.length) {
        val foundIndex = lowerText.indexOf(lowerHighlight, lastIndex)
        if (foundIndex == -1) {
            if (lastIndex < text.length) {
                parts.add(text.substring(lastIndex))
            }
            break
        }

        if (foundIndex > lastIndex) {
            parts.add(text.substring(lastIndex, foundIndex))
        }

        parts.add(text.substring(foundIndex, foundIndex + highlight.length))
        lastIndex = foundIndex + highlight.length
    }

    Row {
        parts.forEach { part ->
            if (part.lowercase() == lowerHighlight) {
                Text(
                    text = part,
                    style = style.copy(
                        color = highlightColor,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    maxLines = 1
                )
            } else {
                Text(
                    text = part,
                    style = style,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ==================== HELPER FUNCTIONS ====================

data class SearchResults(
    val videos: List<VideoFile>,
    val folders: List<VideoFolder>
)

fun performSearch(
    query: String,
    filter: SearchFilter,
    videoFolders: List<VideoFolder>
): SearchResults {
    val lowerQuery = query.lowercase()

    val allVideos = videoFolders.flatMap { it.videos }
    val allFolders = videoFolders

    val filteredVideos = when (filter) {
        SearchFilter.ALL, SearchFilter.VIDEOS -> {
            allVideos.filter { video ->
                video.name.lowercase().contains(lowerQuery)
            }
        }
        SearchFilter.FOLDERS -> emptyList()
    }

    val filteredFolders = when (filter) {
        SearchFilter.ALL, SearchFilter.FOLDERS -> {
            allFolders.filter { folder ->
                folder.name.lowercase().contains(lowerQuery) ||
                        folder.videos.any { it.name.lowercase().contains(lowerQuery) }
            }
        }
        SearchFilter.VIDEOS -> emptyList()
    }

    return SearchResults(
        videos = filteredVideos,
        folders = filteredFolders
    )
}

suspend fun loadRecentSearches(onLoaded: (List<String>) -> Unit) {
    onLoaded(listOf("nature", "music", "tutorial"))
}

fun saveRecentSearches(searches: List<String>) {
}