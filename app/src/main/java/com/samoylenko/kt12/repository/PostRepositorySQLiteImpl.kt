package com.samoylenko.kt12.repository

import com.samoylenko.kt12.api.PostsApi
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.uimodel.ApiError
import okhttp3.*
import java.util.concurrent.TimeUnit


class PostRepositorySQLiteImpl: PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    val emptyPost = Post(
        id = 0,
        author = "Локальное сохранение",
        authorAvatar = "",
        content = "",
        published = "",
        sharing = 0,
        likes = 0,
        countVisability = 0,
        video = "",
        likedByMe = false
    )

    override fun getAll(callback: PostRepository.Callback<List<Post>>) {
        PostsApi.retrofitService.getAll()
            .enqueue(object : retrofit2.Callback<List<Post>> {
                override fun onResponse(call: retrofit2.Call<List<Post>>, response: retrofit2.Response<List<Post>>) {
                    callback.onSuccess(response.body().orEmpty())
                }

                override fun onFailure(call: retrofit2.Call<List<Post>>, t: Throwable) {
                    callback.onFailure(ApiError.fromThrowable(t))
                }
            })
    }


    override fun getPost(id: Long, callback: PostRepository.CallbackPost<Post>) {
        PostsApi.retrofitService.getById(id)
            .enqueue(object : retrofit2.Callback<Post> {
                override fun onResponse( call: retrofit2.Call<Post>, response: retrofit2.Response<Post>) {
                        callback.onSuccess(response.body() ?: emptyPost)
                }

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.onFailure(ApiError.fromThrowable(t))
                }
            })
    }

    override fun dislikeById(id: Long, callback: PostRepository.CallbackPost<Post>) {
        PostsApi.retrofitService.dislikeById(id)
            .enqueue(object : retrofit2.Callback<Post> {
                override fun onResponse(call: retrofit2.Call<Post>, response: retrofit2.Response<Post> ) {
                        callback.onSuccess(response.body() ?: emptyPost)
                }

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.onFailure(ApiError.fromThrowable(t))
                }
            })
    }

    override fun likeById(id: Long, callback: PostRepository.CallbackPost<Post>) {
        PostsApi.retrofitService.likeById(id)
            .enqueue(object : retrofit2.Callback<Post> {
                override fun onResponse(call: retrofit2.Call<Post>, response: retrofit2.Response<Post>) {
                        callback.onSuccess(response.body() ?: emptyPost)
                }

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.onFailure(ApiError.fromThrowable(t))
                }
            })
    }

    override fun shareById(id: Long, callback: PostRepository.CallbackPost<Post>) {
        PostsApi.retrofitService.shareById(id)
            .enqueue(object : retrofit2.Callback<Post> {
                override fun onResponse(call: retrofit2.Call<Post>, response: retrofit2.Response<Post>) {
                        callback.onSuccess(response.body() ?: emptyPost)
                }

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.onFailure(ApiError.fromThrowable(t))
                }
            })
    }

    override fun save(post: Post, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.save(post)
            .enqueue(object : retrofit2.Callback<Post>{
                override fun onResponse(call: retrofit2.Call<Post>, response: retrofit2.Response<Post>) {
                    callback.onSuccess(response.body() ?: throw java.lang.RuntimeException("body is null"))
                }

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.onFailure(ApiError.fromThrowable(t))
                }
            })
    }

    override fun removeById(id: Long, callback: PostRepository.Callback<Unit>) {
        PostsApi.retrofitService.removeById(id).enqueue(
            object : retrofit2.Callback<Unit>{
                override fun onResponse(call: retrofit2.Call<Unit>, response: retrofit2.Response<Unit>) {
                        response.body()?.let { callback.onSuccess(it) }
                }

                override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                    callback.onFailure(ApiError.fromThrowable(t))
                }
            }
        )
    }
}