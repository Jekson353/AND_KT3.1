package com.samoylenko.kt12.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.samoylenko.kt12.api.PostsApi
import com.samoylenko.kt12.dto.Post
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositorySQLiteImpl: PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>(){}
    private val typeTokenPost = object : TypeToken<Post>(){}
    private var emptyListPost = emptyList<Post>()

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

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(callback: PostRepository.Callback<List<Post>>) {
        PostsApi.retrofitService.getAll()
            .enqueue(object : retrofit2.Callback<List<Post>> {
                override fun onResponse(
                    call: retrofit2.Call<List<Post>>,
                    response: retrofit2.Response<List<Post>>
                ) {
                    if (response.isSuccessful) {
                        callback.onSuccess(response.body().orEmpty())
                    } else {
                        callback.onFailure(RuntimeException(response.message()))
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<Post>>, t: Throwable) {
                    callback.onFailure(RuntimeException(t))
                }
            })
    }


    override fun getPost(id: Long, callback: PostRepository.CallbackPost<Post>) {
        PostsApi.retrofitService.getById(id)
            .enqueue(object : retrofit2.Callback<Post> {
                override fun onResponse( call: retrofit2.Call<Post>, response: retrofit2.Response<Post>) {
                    if (response.isSuccessful) {
                        callback.onSuccess(response.body() ?: emptyPost)
                    } else {
                        callback.onFailure(RuntimeException(response.message()))
                    }
                }

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.onFailure(RuntimeException(t))
                }
            })
    }

    override fun dislikeById(id: Long, callback: PostRepository.CallbackPost<Post>) {
        PostsApi.retrofitService.dislikeById(id)
            .enqueue(object : retrofit2.Callback<Post> {
                override fun onResponse(call: retrofit2.Call<Post>, response: retrofit2.Response<Post> ) {
                    if (response.isSuccessful){
                        callback.onSuccess(response.body() ?: emptyPost)
                    }else{
                        callback.onFailure(RuntimeException(response.message()))
                    }
                }

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.onFailure(RuntimeException(t))
                }
            })
    }

    override fun likeById(id: Long, callback: PostRepository.CallbackPost<Post>) {
        PostsApi.retrofitService.likeById(id)
            .enqueue(object : retrofit2.Callback<Post> {
                override fun onResponse(call: retrofit2.Call<Post>, response: retrofit2.Response<Post>) {
                    if (response.isSuccessful){
                        callback.onSuccess(response.body() ?: emptyPost)
                    }else{
                        callback.onFailure(RuntimeException(response.message()))
                    }
                }

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.onFailure(RuntimeException(t))
                }
            })
    }

    override fun shareById(id: Long, callback: PostRepository.CallbackPost<Post>) {
        PostsApi.retrofitService.shareById(id)
            .enqueue(object : retrofit2.Callback<Post> {
                override fun onResponse(call: retrofit2.Call<Post>, response: retrofit2.Response<Post>) {
                    if (response.isSuccessful){
                        callback.onSuccess(response.body() ?: emptyPost)
                    }else{
                        callback.onFailure(RuntimeException(response.message()))
                    }
                }

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.onFailure(RuntimeException(t))
                }
            })
    }

    override fun save(post: Post, callback: PostRepository.Callback<List<Post>>) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts")
            .build()
        return client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body?.use {
                        try {
                            response.body?.string().let {
                                gson.fromJson<List<Post>>(it, typeToken.type)
                            }?.also(callback::onSuccess)
                        } catch (e: Exception) {
                            callback.onFailure(e)
                        }
                    }
                }
            })
    }

    override fun removeById(id: Long, callback: PostRepository.Callback<Unit>) {
        PostsApi.retrofitService.removeById(id).enqueue(
            object : retrofit2.Callback<Unit>{
                override fun onResponse(call: retrofit2.Call<Unit>, response: retrofit2.Response<Unit>) {
                    if (response.isSuccessful){
                        response.body()?.let { callback.onSuccess(it) }
                    }else{
                        callback.onFailure(RuntimeException(response.message()))
                    }
                }

                override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                    callback.onFailure(RuntimeException(t))
                }
            }
        )
    }
}