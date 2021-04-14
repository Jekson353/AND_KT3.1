package com.samoylenko.kt12.repository

import com.samoylenko.kt12.dto.Auth

interface AuthRepository {
    suspend fun getAuth(login: String, pass: String): Auth
}