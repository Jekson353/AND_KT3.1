package com.samoylenko.kt12.repository

import com.samoylenko.kt12.api.PostsApi
import com.samoylenko.kt12.dto.Auth

class AuthRepositoryImpl : AuthRepository {
    override suspend fun getAuth(login: String, pass: String): Auth {
        return PostsApi.retrofitService.updateUser(login, pass)
    }
}