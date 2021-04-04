package com.samoylenko.kt12.repository

import androidx.lifecycle.LiveData
import com.samoylenko.kt12.dto.Post

interface PostRepository {
    val posts: LiveData<List<Post>>
    suspend fun getAll(): List<Post>
    suspend fun likeById(id: Long): Post
    suspend fun dislikeById(id: Long): Post
    suspend fun shareById(id: Long)
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun getPost(id: Long): Post
}