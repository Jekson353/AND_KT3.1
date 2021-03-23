package com.samoylenko.kt12.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.samoylenko.kt12.db.AppDb
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.repository.PostRepository
import com.samoylenko.kt12.repository.PostRepositorySQLiteImpl
import com.samoylenko.kt12.uimodel.FeedModel
import com.samoylenko.kt12.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    author = "Локальное сохранение",
    content = "",
    published = "",
    sharing = 0,
    likes = 0,
    countVisability = 0,
    video = "",
    likedByMe = false
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositorySQLiteImpl()
    private val _state = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _state

    val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        getPosts()
    }

    fun getPosts() {
        thread {
            // Начинаем загрузку
            _state.postValue(FeedModel(loading = true))
            try {
                // Данные успешно получены
                val posts = repository.getAll()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (e: IOException) {
                // Получена ошибка
                FeedModel(error = true)
            }.also(_state::postValue)
        }
    }

    fun likeById(id: Long) {
        thread {
            repository.likeById(id)
            val posts = repository.getAll()
            _state.postValue(FeedModel(posts = posts))
        }
    }

    fun shareById(id: Long) {
        thread { repository.shareById(id) }
    }

    fun removeById(id: Long) {
        thread {
            val old = _state.value?.posts.orEmpty()
            _state.postValue(
                _state.value?.copy(posts = _state.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            try {
                repository.removeById(id)
            } catch (e: IOException) {
                _state.postValue(_state.value?.copy(posts = old))
            }
        }
    }

    fun save() {
        edited.value?.let {
            thread {
                repository.save(it)
                _postCreated.postValue(Unit)
            }
        }
        edited.value = empty
    }

    fun changeContent(content: String, video: String) {
        val text = content.trim()
        val textVideo = video.trim()
        if (edited.value?.content == text && edited.value?.video == textVideo) {
            return
        }
        if (textVideo == "") {
            edited.value = edited.value?.copy(content = text, video = "")
        } else {
            edited.value = edited.value?.copy(content = text, video = textVideo)
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }
}