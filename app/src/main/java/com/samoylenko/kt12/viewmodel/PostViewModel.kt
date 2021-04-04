package com.samoylenko.kt12.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.samoylenko.kt12.db.AppDb
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.repository.PostRepository
import com.samoylenko.kt12.repository.PostRepositorySQLiteImpl
import com.samoylenko.kt12.uimodel.ApiError
import com.samoylenko.kt12.uimodel.FeedModel
import com.samoylenko.kt12.util.SingleLiveEvent
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.Executors

val empty = Post(
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

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositorySQLiteImpl(
        AppDb.getInstance(application).postDao
    )

    val posts: LiveData<List<Post>>
            get() = repository.posts

    private val _state = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _state

    val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _postCreateError = SingleLiveEvent<ApiError>()
    val postCreateError: LiveData<ApiError>
        get() = _postCreateError

    private val executorService = Executors.newFixedThreadPool(64)

    init {
        getPosts()
    }

    fun getPosts() {
        viewModelScope.launch {
            // Начинаем загрузку
            _state.postValue(FeedModel(loading = true))
            // Данные успешно получены
            try {
                val posts = repository.getAll()
                _state.postValue(FeedModel(empty = posts.isEmpty()))
            }catch (e: IOException){
                _state.postValue(FeedModel(errorVisible = true, error = ApiError.fromThrowable(e)))
            }


        }
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            _state.postValue(_state.value?.copy(progressBar = true))
            val i: Long = 0
            try {
                repository.getPost(id).let {post: Post ->
                    if (post.id== i){
                        Toast.makeText(
                            getApplication(),
                            "Нечего лайкать",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else if (post.likedByMe){
                        try {
                            repository.dislikeById(post.id)
                            _state.postValue(_state.value?.copy(progressBar = false))
                        }catch (e: IOException){
                            _state.postValue(FeedModel(progressBar = false, errorVisible = true, error = ApiError.fromThrowable(e)))
                        }
                    }else{
                        try {
                            repository.likeById(post.id)
                            _state.postValue(_state.value?.copy(progressBar = false))
                        }catch (e: IOException){
                            _state.postValue(FeedModel(progressBar = false, errorVisible = true, error = ApiError.fromThrowable(e)))
                        }

                    }
                }
            }catch (e: IOException){
                _state.postValue(FeedModel(progressBar = false, errorVisible = true, error = ApiError.fromThrowable(e)))
            }
        }
    }

    fun shareById(id: Long) {
        viewModelScope.launch {
            try {
                repository.shareById(id)
            }catch (e: IOException){
                _state.postValue(FeedModel(progressBar = false, errorVisible = true, error = ApiError.fromThrowable(e)))
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            _state.postValue(_state.value?.copy(progressBar = true))
            try {
                repository.removeById(id)
                _state.postValue(FeedModel(progressBar = false))
            }catch (e: IOException){
                _state.postValue(FeedModel(progressBar = false))
                getPosts()
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            _state.postValue(_state.value?.copy(progressBar = true))
            edited.value?.let {
                try {
                    repository.save(it)
                    _postCreated.value = Unit
                    _state.postValue(FeedModel(progressBar = false))
                    edited.value = empty
                }catch (e: IOException){
                    _postCreateError.value = ApiError.fromThrowable(e)
                }
            }

        }
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

    override fun onCleared() {
        executorService.shutdown()
    }
}
