package com.samoylenko.kt12.repository

import com.samoylenko.kt12.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long)
    fun shareById(id: Long)
    fun save(post: Post)
    fun removeById(id: Long)
}