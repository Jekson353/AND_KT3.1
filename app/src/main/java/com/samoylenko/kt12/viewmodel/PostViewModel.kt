package com.samoylenko.kt12.viewmodel

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.*
import com.samoylenko.kt12.db.AppDb
import com.samoylenko.kt12.dto.MediaUpload
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.repository.PostRepository
import com.samoylenko.kt12.repository.PostRepositorySQLiteImpl
import com.samoylenko.kt12.error.ApiError
import com.samoylenko.kt12.uimodel.FeedModel
import com.samoylenko.kt12.uimodel.PhotoModel
import com.samoylenko.kt12.util.SingleLiveEvent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

val empty = Post(
    id = 0,
    author = "Локальное сохранение",
    authorAvatar = "",
    content = "",
    published = "",
    sharing = 0,
    likes = 0,
    countVisability = 0,
    likedByMe = false
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositorySQLiteImpl(
        AppDb.getInstance(application).postDao
    )

    val posts: LiveData<List<Post>>
            get() = repository.posts.asLiveData()

    val newPosts = posts.switchMap {
        repository.getNewerCount(it.size.toLong())
            .catch { e->e.printStackTrace() }
            .asLiveData()
    }

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

    private val noPhoto = PhotoModel()

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

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

    fun refreshPosts() = viewModelScope.launch {
        try {
            _state.value = FeedModel(loading = true)
            repository.getAll()
            _state.value = FeedModel()
        } catch (e: Exception) {
            _state.value = FeedModel(error = ApiError.fromThrowable(e))
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
                    when(_photo.value) {
                        noPhoto -> repository.save(it)
                        else -> _photo.value?.file?.let { file ->
                            repository.saveWithAttachment(it, MediaUpload(file))
                        }
                    }
                    _postCreated.value = Unit
                    _state.postValue(FeedModel(progressBar = false))
                    //edited.value = empty
                }catch (e: IOException){
                    _postCreateError.value = ApiError.fromThrowable(e)
                }
            }
            edited.value = empty
            _photo.value = noPhoto
        }
    }

    fun changePhoto(uri: Uri?, file: File?){
        _photo.value = PhotoModel(uri, file)
    }

    fun changeContent(content: String) {
        val text = content.trim()
        //val namePhoto = photo.trim()
        if (edited.value?.content == text) {
            //if (edited.value?.content == text && edited.value?.photo == namePhoto) {
            return
        }
//        if (namePhoto == "") {
//            edited.value = edited.value?.copy(content = text, photo = "")
//        } else {
//            edited.value = edited.value?.copy(content = text, photo = namePhoto)
//        }
        edited.value = edited.value?.copy(content = text)
    }

    fun edit(post: Post) {
        edited.value = post
    }
}
