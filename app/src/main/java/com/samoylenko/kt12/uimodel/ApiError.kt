package com.samoylenko.kt12.uimodel

import android.content.res.Resources
import java.net.ConnectException

sealed class ApiError {
    object ServerError: ApiError()
    object NetworkError: ApiError()
    object UnknownError : ApiError()

    companion object{
        fun fromThrowable(throwable: Throwable) : ApiError =
            when (throwable){
                is ApiException -> throwable.error
                is ConnectException -> NetworkError
                else -> UnknownError
            }
    }
}

fun ApiError?.getErrorMessage(resources: Resources): String =
    when (this){
        ApiError.UnknownError, null -> "Неизвестная ошибка"
                ApiError.NetworkError -> "Ошибка сети"
                ApiError.ServerError -> "Ошибка сервера"
    }