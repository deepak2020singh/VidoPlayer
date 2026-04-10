package com.foss.vidoplay.presentation.viewModel

// Presentation/viewModel/PlaylistViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foss.vidoplay.domain.model.Playlist
import com.foss.vidoplay.domain.model.PlaylistVideo
import com.foss.vidoplay.domain.model.VideoFile
import com.foss.vidoplay.domain.repo.PlaylistRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PlaylistUiState(
    val playlists: List<Playlist> = emptyList(),
    val recentVideos: List<PlaylistVideo> = emptyList(),
    val selectedPlaylistVideos: List<PlaylistVideo> = emptyList(),
    val selectedPlaylist: Playlist? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class PlaylistViewModel(
    private val repository: PlaylistRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PlaylistUiState())
    val state: StateFlow<PlaylistUiState> = _state.asStateFlow()

    init {
        loadPlaylists()
        loadRecentVideos()
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            repository.getAllPlaylists()
                .catch { e -> _state.update { it.copy(error = e.message) } }
                .collect { playlists ->
                    _state.update { it.copy(playlists = playlists) }
                }
        }
    }

    fun loadRecentVideos() {
        viewModelScope.launch {
            repository.getRecentVideos()
                .catch { e -> _state.update { it.copy(error = e.message) } }
                .collect { videos ->
                    _state.update { it.copy(recentVideos = videos) }
                }
        }
    }

    fun loadPlaylistVideos(playlistId: Long) {
        viewModelScope.launch {
            repository.getVideosForPlaylist(playlistId)
                .catch { e -> _state.update { it.copy(error = e.message) } }
                .collect { videos ->
                    _state.update { it.copy(selectedPlaylistVideos = videos) }
                }
        }
    }

    fun createPlaylist(name: String, description: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                repository.createPlaylist(name.trim(), description.trim())
                _state.update { it.copy(isLoading = false, successMessage = "Playlist '$name' created") }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updatePlaylist(id: Long, name: String, description: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            try {
                repository.updatePlaylist(id, name.trim(), description.trim())
                _state.update { it.copy(successMessage = "Playlist updated") }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            try {
                repository.deletePlaylist(playlist.id)
                _state.update { it.copy(successMessage = "'${playlist.name}' deleted") }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun addVideoToPlaylist(playlistId: Long, video: VideoFile) {
        viewModelScope.launch {
            try {
                val alreadyIn = repository.isVideoInPlaylist(playlistId, video.id)
                if (alreadyIn) {
                    _state.update { it.copy(successMessage = "Video already in playlist") }
                    return@launch
                }
                repository.addVideoToPlaylist(playlistId, video)
                _state.update { it.copy(successMessage = "Added to playlist") }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun removeVideoFromPlaylist(playlistId: Long, videoId: Long) {
        viewModelScope.launch {
            try {
                repository.removeVideoFromPlaylist(playlistId, videoId)
                _state.update { it.copy(successMessage = "Removed from playlist") }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun selectPlaylist(playlist: Playlist?) {
        _state.update { it.copy(selectedPlaylist = playlist) }
        playlist?.let { loadPlaylistVideos(it.id) }
    }

    fun clearMessage() {
        _state.update { it.copy(error = null, successMessage = null) }
    }
}