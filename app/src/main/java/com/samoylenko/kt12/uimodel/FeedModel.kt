package com.samoylenko.kt12.uimodel

import com.samoylenko.kt12.error.ApiError

data class FeedModel (
    val loading: Boolean = false,
    val errorVisible: Boolean = false,
    val error: ApiError? = null,
    val empty: Boolean = false,
    val progressBar: Boolean = false
)