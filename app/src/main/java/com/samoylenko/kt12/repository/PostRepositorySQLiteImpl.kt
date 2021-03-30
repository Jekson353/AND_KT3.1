package com.samoylenko.kt12.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.samoylenko.kt12.dto.Post
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.lang.Exception
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

    override fun getAll(callback: PostRepository.Callback<List<Post>>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/posts")
            .build()
        return client.newCall(request)
            .enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailure(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body?.use {
                        try {
                            response.body?.string().let {
                                gson.fromJson<List<Post>>(it, typeToken.type)
                            }?.also(callback::onSuccess)
                        }catch (e: Exception){
                            callback.onFailure(e)
                        }
                    }

                }
            })
    }


    override fun getPost(id: Long, callback: PostRepository.CallbackPost<Post>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/posts/$id")
            .build()
         return client.newCall(request)
             .enqueue(object : Callback{
                 override fun onFailure(call: Call, e: IOException) {
                     e.printStackTrace()
                 }

                 override fun onResponse(call: Call, response: Response) {
                     response.body?.use {
                         try {
                             response.body?.string().let {
                               gson.fromJson(it, typeTokenPost.type)
                             }
                         }catch (e: Exception){
                             e.printStackTrace()
                         }
                     }
                 }
             })
    }


    override fun likeById(id: Long, callback: PostRepository.Callback<Post>) {
        var post: Post = Post(0, "","","",0,0,0,"",false)
        getPost(id, object : PostRepository.CallbackPost<Post>{
            override fun onSuccess(result: Post) {
                post =  result

            }
            override fun onFailure(error: Throwable) {
                error.printStackTrace()
            }
        })

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

        return client.newCall(request)
            .enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body?.use {
                        try {
                            response.body?.string().let {
                                gson.fromJson(it, typeTokenPost.type)
                            }
                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    }
                }
            })
    }

    override fun shareById(id: Long) {
        TODO()
    }

    override fun save(post: Post, callback: PostRepository.Callback<List<Post>>) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts")
            .build()
        return client.newCall(request)
            .enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body?.use {
                        try {
                            response.body?.string().let {
                                gson.fromJson<List<Post>>(it, typeToken.type)
                            }?.also(callback::onSuccess)
                        }catch (e: Exception){
                            callback.onFailure(e)
                        }
                    }
                }
            })
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