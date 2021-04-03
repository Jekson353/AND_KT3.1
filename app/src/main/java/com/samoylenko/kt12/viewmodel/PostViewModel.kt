package com.samoylenko.kt12.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.repository.PostRepository
import com.samoylenko.kt12.repository.PostRepositorySQLiteImpl
import com.samoylenko.kt12.uimodel.FeedModel
import com.samoylenko.kt12.util.SingleLiveEvent
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
    private val repository: PostRepository = PostRepositorySQLiteImpl()
    private val _state = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _state

    val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val executorService = Executors.newFixedThreadPool(64)

    init {
        getPosts()
    }

    fun getPosts() {
        // Начинаем загрузку
        _state.postValue(FeedModel(loading = true))
        // Данные успешно получены
        repository.getAll(
            object : PostRepository.Callback<List<Post>> {
                override fun onSuccess(posts: List<Post>) {
                    _state.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
                }

                override fun onFailure(error: Throwable) {
                    _state.postValue(FeedModel(error = true))
                }
            }
        )
    }

    fun likeById(id: Long) {
        _state.postValue(_state.value?.copy(progressBar = true))
        repository.getPost(id, object : PostRepository.CallbackPost<Post> {
            override fun onSuccess(result: Post) {
                val i: Long = 0
                if (result.id == i) {
                    Toast.makeText(
                        getApplication(),
                        "Нечего лайкать",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (result.likedByMe) {
                    repository.dislikeById(id, object : PostRepository.CallbackPost<Post> {
                        override fun onSuccess(result: Post) {
                            val old: MutableList<Post> = _state.value?.posts.orEmpty().toMutableList()

                            old.forEachIndexed { index, post -> post.takeIf { it.id == result.id }?.let { old[index] = it.copy(likes = result.likes, likedByMe = result.likedByMe) } }
                            _state.postValue(_state.value?.copy(posts = old, progressBar = false))
                        }

                        override fun onFailure(error: Throwable) {
                            error.printStackTrace()
                        }
                    })
                } else {
                    repository.likeById(id, object : PostRepository.CallbackPost<Post> {
                        override fun onSuccess(result: Post) {
                            val old: MutableList<Post> = _state.value?.posts.orEmpty().toMutableList()
                            old.forEachIndexed { index, post -> post.takeIf { it.id == result.id }?.let { old[index] = it.copy(likes = result.likes, likedByMe = result.likedByMe) } }
                            _state.postValue(
                                _state.value?.copy(posts = old, progressBar = false)
                            )
                        }

                        override fun onFailure(error: Throwable) {
                            error.printStackTrace()
                        }
                    })
                }
            }

            override fun onFailure(error: Throwable) {
                error.printStackTrace()
            }
        })
    }

    fun shareById(id: Long) {
        repository.shareById(id, object : PostRepository.CallbackPost<Post>{
            override fun onSuccess(result: Post) {
                val old: MutableList<Post> = _state.value?.posts.orEmpty().toMutableList()
                old.forEachIndexed { index, post -> post.takeIf { it.id == result.id }?.let { old[index] = it.copy(sharing = result.sharing) } }
                _state.postValue(
                    _state.value?.copy(posts = old)
                )
            }

            override fun onFailure(error: Throwable) {
                _state.postValue(FeedModel(error = true))
            }
        })
    }

    fun removeById(id: Long) {
        _state.postValue(_state.value?.copy(progressBar = true))
        val old = _state.value?.posts.orEmpty()
        repository.removeById(id, object : PostRepository.Callback<Unit>{
            override fun onSuccess(posts: Unit) {
                _state.postValue(
                    _state.value?.copy(posts = _state.value?.posts.orEmpty()
                        .filter { it.id != id }, progressBar = false
                    )
                )
            }

            override fun onFailure(error: Throwable) {
                _state.postValue(_state.value?.copy(posts = old, progressBar = false))
            }
        })
    }

    fun save() {
        edited.value?.let {
            repository.save(
                it,
                object : PostRepository.Callback<List<Post>> {
                    override fun onSuccess(posts: List<Post>) {
                        _state.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
                    }

                    override fun onFailure(error: Throwable) {
                        _state.postValue(FeedModel(error = true))
                    }
                },
            )
            _postCreated.value = Unit
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

    override fun onCleared() {
        executorService.shutdown()
    }
}
