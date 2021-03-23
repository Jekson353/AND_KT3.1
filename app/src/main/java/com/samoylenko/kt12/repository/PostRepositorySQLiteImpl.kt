package com.samoylenko.kt12.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.samoylenko.kt12.dto.Post
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class PostRepositorySQLiteImpl: PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>(){}
    private val typeTokenPost = object : TypeToken<Post>(){}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/posts")
            .build()
        return client.newCall(request)
            .execute()
            .use { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }

    fun getPost(id: Long): Post {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/posts/$id")
            .build()
        return client.newCall(request)
            .execute()
            .use { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeTokenPost.type)
            }
    }

    override fun likeById(id: Long) {
        val post = getPost(id)
        val request: Request
        request = if (post.likedByMe){
            Request.Builder()
                .delete()
                .url("${BASE_URL}/api/posts/${id}/likes")
                .build()

        }else{
            Request.Builder()
                .post(gson.toJson(id).toRequestBody(jsonType))
                .url("${BASE_URL}/api/posts/${id}/likes")
                .build()
        }
        client.newCall(request)
            .execute()
    }

    override fun shareById(id: Long) {
        TODO()
    }

    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts")
            .build()
        client.newCall(request)
            .execute()
            .close()
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}