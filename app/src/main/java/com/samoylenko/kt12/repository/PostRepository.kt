package com.samoylenko.kt12.repository

import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.uimodel.ApiError

interface PostRepository {
    fun getAll(callback: Callback<List<Post>>)
    fun likeById(id: Long, callback: CallbackPost<Post>)
    fun dislikeById(id: Long, callback: CallbackPost<Post>)
    fun shareById(id: Long, callback: CallbackPost<Post>)
    fun save(post: Post, callback: Callback<Post>)
    fun removeById(id: Long, callback: Callback<Unit>)
    fun getPost(id: Long, callback: CallbackPost<Post>)

    interface Callback<T>{
        fun onSuccess(posts: T) {}
        fun onFailure (error: ApiError)
    }
    interface CallbackPost<Post>{
        fun onSuccess (result: Post)
        fun onFailure (error: ApiError)
    }
}