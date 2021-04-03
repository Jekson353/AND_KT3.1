package com.samoylenko.kt12.uimodel

import com.samoylenko.kt12.dto.Post

data class FeedModel (
    val posts: List<Post> = emptyList(),
    val loading: Boolean = false,
    val errorVisible: Boolean = false,
    val error: ApiError? = null,
    val empty: Boolean = false,
    val progressBar: Boolean = false
)