package com.samoylenko.kt12.api

import com.samoylenko.kt12.api.PostsApi.BASE_URL
import com.samoylenko.kt12.dto.Media
import com.samoylenko.kt12.dto.Post
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*



private val logging = HttpLoggingInterceptor().apply {
    //if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    //}
}

private val okhttp = OkHttpClient.Builder()
    .addInterceptor(logging)
    .addInterceptor(PostInterceptor())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okhttp)
    .build()

interface PostsApiService {
    @GET("posts")
    suspend fun getAll(): List<Post>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Post

    @POST("posts")
    suspend fun save(@Body post: Post): Post

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long)

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Post

    @POST("posts/{id}/sharing")
    suspend fun shareById(@Path("id") id: Long): Post

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Post

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): List<Post>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Media
}

object PostsApi {
    val retrofitService: PostsApiService by lazy {
        retrofit.create(PostsApiService::class.java)
    }
    const val BASE_URL = "http://10.0.2.2:9999/api/slow/"
    const val MEDIA_URL = "http://10.0.2.2:9999/"
}