package com.samoylenko.kt12.uimodel

data class FeedModel (
    val loading: Boolean = false,
    val errorVisible: Boolean = false,
    val error: ApiError? = null,
    val empty: Boolean = false,
    val progressBar: Boolean = false
)