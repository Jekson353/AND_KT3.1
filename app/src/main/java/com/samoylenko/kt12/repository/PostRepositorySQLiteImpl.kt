package com.samoylenko.kt12.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.samoylenko.kt12.api.PostsApi
import com.samoylenko.kt12.dao.PostDao
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.entity.PostEntity
import com.samoylenko.kt12.entity.toPost
import okhttp3.*
import java.util.concurrent.TimeUnit


class PostRepositorySQLiteImpl(private val dao: PostDao): PostRepository {

    override val posts: LiveData<List<Post>>
        get() = dao.getAll().map { it.map(PostEntity::toPost) }

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

    override suspend fun getAll(): List<Post> {
        val networkPosts = PostsApi.retrofitService.getAll()
            dao.insertPost(networkPosts.map(PostEntity.Companion::fromPost))
        return networkPosts
    }

    override suspend fun getPost(id: Long): Post {
        return PostsApi.retrofitService.getById(id)
    }

    override suspend fun dislikeById(id: Long): Post {
        val postDislike = PostsApi.retrofitService.dislikeById(id)

        postDislike.let {
            dao.dislikesById(id)
        }

        return postDislike
    }

    override suspend fun likeById(id: Long): Post {
        val postLike = PostsApi.retrofitService.likeById(id)

        postLike.let {
            dao.likesById(it.id)
        }
        return postLike
    }

    override suspend fun shareById(id: Long) {
        PostsApi.retrofitService.shareById(id)
        dao.shareById(id)
    }

    override suspend fun save(post: Post) {
        PostsApi.retrofitService.save(post)
    }

    override suspend fun removeById(id: Long) {
        PostsApi.retrofitService.removeById(id)
        dao.removeById(id)
    }
}