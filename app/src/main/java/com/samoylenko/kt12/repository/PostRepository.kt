package com.samoylenko.kt12.repository

import com.samoylenko.kt12.dto.Post

interface PostRepository {
    fun getAll(callback: Callback<List<Post>>)
    fun likeById(id: Long, callback: Callback<Post>)
    //fun likeById(id: Long)
    fun shareById(id: Long)
    fun save(post: Post, callback: Callback<List<Post>>)
    fun removeById(id: Long)
    fun getPost(id: Long, callback: CallbackPost<Post>)

    interface Callback<T>{
        fun onSuccess (result: List<Post>)
        fun onFailure (error: Throwable)
    }
    interface CallbackPost<T>{
        fun onSuccess (result: Post)
        fun onFailure (error: Throwable)
    }
}