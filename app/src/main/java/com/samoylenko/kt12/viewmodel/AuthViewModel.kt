package com.samoylenko.kt12.viewmodel

import androidx.lifecycle.*
import com.samoylenko.kt12.auth.AppAuth
import com.samoylenko.kt12.auth.AuthState
import com.samoylenko.kt12.dto.Auth
import com.samoylenko.kt12.error.ApiError
import com.samoylenko.kt12.repository.AuthRepository
import com.samoylenko.kt12.repository.AuthRepositoryImpl
import com.samoylenko.kt12.uimodel.FeedModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class AuthViewModel : ViewModel() {
    val data: LiveData<AuthState> = AppAuth.getInstance()
        .authStateFlow
        .asLiveData(Dispatchers.Default)
    val authenticated: Boolean
        get() = AppAuth.getInstance().authStateFlow.value.id != 0L

    private val repository: AuthRepository = AuthRepositoryImpl()

    private val _state = MutableLiveData(FeedModel())
    val state: LiveData<FeedModel>
        get() = _state

    private val status = MutableLiveData(false)
    val statusAuth: LiveData<Boolean>
        get() = status


    fun authentication(login: String, pass: String) {
        val i: Long = 0
        viewModelScope.launch {
            _state.postValue(FeedModel(loading = true))
            try {
                val authorization: Auth = repository.getAuth(login, pass)
                authorization.let {
                    val id = it.id ?: 0
                    val token = it.token ?: "null"
                    if (id > i) {
                        AppAuth.getInstance().setAuth(id, token)
                        status.value = true
                    } else {
                        _state.postValue(FeedModel(loading = false, errorVisible = true))
                    }
                }
                _state.postValue(FeedModel(loading = false))
            } catch (e: IOException) {
                _state.postValue(FeedModel(errorVisible = true, error = ApiError.fromThrowable(e)))
            }
        }
    }
}