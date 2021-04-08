package com.samoylenko.kt12.repository

import com.samoylenko.kt12.dto.Media
import com.samoylenko.kt12.dto.MediaUpload
import com.samoylenko.kt12.dto.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val posts: Flow<List<Post>>
    fun getNewerCount(id: Long) : Flow<Int>
    suspend fun getAll(): List<Post>
    suspend fun likeById(id: Long): Post
    suspend fun dislikeById(id: Long): Post
    suspend fun shareById(id: Long)
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun getPost(id: Long): Post
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun upload(upload: MediaUpload): Media
}