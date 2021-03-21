package com.samoylenko.kt12.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.samoylenko.kt12.db.AppDb
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.repository.PostRepository
import com.samoylenko.kt12.repository.PostRepositorySQLiteImpl

private val empty = Post(
    id = 0,
    author = "Локальное сохранение",
    content = "",
    published = "",
    sharing = 0,
    like = 0,
    countVisability = 0,
    video = "",
    likedByMe = false
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositorySQLiteImpl(
        AppDb.getInstance(application).postDao
    )
    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun changeContent(content: String, video: String) {
        val text = content.trim()
        val textVideo = video.trim()
        if (edited.value?.content == text && edited.value?.video == textVideo) {
            return
        }
        if (textVideo.equals("")) {
            edited.value = edited.value?.copy(content = text, video = "")
        } else {
            edited.value = edited.value?.copy(content = text, video = textVideo)
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }
}