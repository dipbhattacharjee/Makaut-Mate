package com.dev.makautmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.makautmate.BuildConfig
import com.dev.makautmate.data.model.YoutubePlaylistItem
import com.dev.makautmate.data.remote.YoutubeApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val youtubeApiService: YoutubeApiService
) : ViewModel() {

    private val _playlists = MutableStateFlow<List<YoutubePlaylistItem>>(emptyList())
    val playlists: StateFlow<List<YoutubePlaylistItem>> = _playlists

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val API_KEY = BuildConfig.YOUTUBE_API_KEY

    fun fetchPlaylist(playlistId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = youtubeApiService.getPlaylistItems(
                    playlistId = playlistId,
                    apiKey = API_KEY
                )
                _playlists.value = response.items
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
