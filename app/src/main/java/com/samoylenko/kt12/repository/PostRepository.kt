package com.samoylenko.kt12.repository

import com.samoylenko.kt12.dto.Post
import retrofit2.Call

interface PostRepository {
    fun getAll(callback: Callback<List<Post>>)
    fun likeById(id: Long, callback: CallbackPost<Post>)
    fun dislikeById(id: Long, callback: CallbackPost<Post>)
    //fun likeById(id: Long)
    fun shareById(id: Long, callback: CallbackPost<Post>)
    fun save(post: Post, callback: Callback<List<Post>>)
    fun removeById(id: Long, callback: Callback<Unit>)
    fun getPost(id: Long, callback: CallbackPost<Post>)

    interface Callback<T>{
        fun onSuccess(posts: T) {}
        fun onFailure (error: Throwable)
    }
    interface CallbackPost<Post>{
        fun onSuccess (result: Post)
        fun onFailure (error: Throwable)
    }
}