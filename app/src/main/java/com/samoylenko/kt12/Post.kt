package com.samoylenko.kt12

data class Post (
    val id:Long,
    val author: String,
    val content: String,
    val published: String,
    val sharing: Int,
    val like: Int,
    var countVisability: Int,
    val likedByMe: Boolean = false
)