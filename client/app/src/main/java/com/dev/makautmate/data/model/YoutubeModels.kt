package com.dev.makautmate.data.model

data class YoutubeResponse(
    val items: List<YoutubePlaylistItem>
)

data class YoutubePlaylistItem(
    val snippet: YoutubeSnippet
)

data class YoutubeSnippet(
    val title: String,
    val channelTitle: String,
    val thumbnails: YoutubeThumbnails,
    val resourceId: YoutubeResourceId
)

data class YoutubeThumbnails(
    val high: YoutubeThumbnail?
)

data class YoutubeThumbnail(
    val url: String
)

data class YoutubeResourceId(
    val videoId: String
)
