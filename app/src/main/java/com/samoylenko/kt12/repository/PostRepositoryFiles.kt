package com.samoylenko.kt12.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.samoylenko.kt12.dto.Post

class PostRepositoryFiles(context: Context) : PostRepository {

    private var nextId = 1L
    private val gson = Gson()
    private val file = context.filesDir.resolve("posts.json")
    private val type = object : TypeToken<List<Post>>() {}.type
    private var posts = run {
        if (!file.exists()) return@run emptyList<Post>()
        file.readText()
            .ifBlank {
                return@run emptyList<Post>()
            }.let {
                gson.fromJson(it, type)
            }
    }

    private val data: MutableLiveData<List<Post>> = MutableLiveData(posts)
    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) {
                it
            } else {
                if (it.likedByMe) {
                    it.copy(likedByMe = !it.likedByMe, like = it.like - 1)
                } else {
                    it.copy(likedByMe = !it.likedByMe, like = it.like + 1)
                }
            }
        }
        data.value = posts
        sync()
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) {
                it
            } else {
                it.copy(sharing = it.sharing + 1)
            }
        }
        data.value = posts
        sync()
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            posts = listOf(
                post.copy(
                    id = nextId++,
                    author = "Локальное сохранение",
                    published = "Только что",
                    sharing = 0,
                    like = 0,
                    countVisability = 0,
                    likedByMe = false
                )
            ) + posts
            data.value = posts
            sync()
            return
        }
        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content, video = post.video)
        }
        data.value = posts
        sync()
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
        sync()
    }

    private fun sync() {
        file.writeText(gson.toJson(posts))
    }
}