package com.samoylenko.kt12.dto

data class Post(
    var id:Long,
    val author: String,
    val content: String,
    val published: String,
    val sharing: Int,
    val likes: Int,
    var countVisability: Int,
    val video: String,
    val likedByMe: Boolean = false
)